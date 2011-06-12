package dlsim.DLSim;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class WireViewAStar extends WireView
{
  Graphics2D g;
  Vector allObjects;
  WireModel w;
  Point start;
  Point end;
  GeneralPath routedWire;
  Shape selector= new Area();
  RouteNode route;

  public WireViewAStar(WireModel w)
  {
    super(w);
    this.w=w;
    start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
    end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
    if (getContainer()!=null)
    getContainer().addLocationListener(this);

    //draw the line
    routeWire();
    this.repaint();
  }



  public void paint(Graphics g)
 {
    if (getContainer()!=null)
    getContainer().addLocationListener(this);
    Graphics2D g2d = (Graphics2D) g;
    this.g=g2d;
    //set up color and width of wire
    g2d.setColor(this.wirecolor);
    g2d.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
    if (routedWire==null)
    {

      //find objects this needs to avoid
      getAllObjects();
      //draw the line
      routeWire();
    }
    g2d.draw(routedWire);
    g2d.draw(selector);
  }

  public Shape getShape()
  {
    Area a = new Area();
    if (routedWire!=null)
    a.add(new Area(routedWire.getBounds()));
    a.add(new Area(selector.getBounds()));
    return a.getBounds();
  }

  public void routeWire()
  {
    //get start and end point
    start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
    end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
    //Set Up
    //find objects this needs to avoid
    getAllObjects();
    int it=0;
    int maxit=500;
    PriorityQueue open = new PriorityQueue();
    Vector closed = new Vector();
    RouteNode startNode = new RouteNode(start);
    startNode.g=0;
    startNode.h=distEstimate(start,end);
    open.put(startNode,startNode.f());
    //Begin Search
    while (!open.empty() && it<maxit)
    {
      RouteNode node_current = (RouteNode) open.get();
      //are we at the end?
      if (node_current.p.equals(end))
      {
        //success!
        makeRoute(node_current);
        return;
      }
     //Where can we go from here without passing an obstacle
     Point[] succ = getSuccessors(node_current.p,end);
     for(int i=0;i<succ.length;i++)
     {
       //make new node to this point
       RouteNode node_succ = new RouteNode(succ[i]);
       // set it's attributes
       node_succ.parent=node_current;
       //Debug.out(node_succ.p);
       //Debug.out(node_current.p);
       node_succ.g=node_current.g+(distEstimate(node_succ.p,node_current.p)/4);
       node_succ.h=distEstimate(node_succ.p,end);
       //System.out.print(" g = "+node_succ.g);
       //System.out.print(" h = "+node_succ.h);
       //Debug.out(" f = "+node_succ.f());
       // check if it is in open or close already
       RouteNode opennode  = (RouteNode)open.contains(node_succ);
       int closeindex = closed.indexOf(node_succ);
       RouteNode closenode = null;
       if (closeindex!=-1)
         closenode=(RouteNode)closed.elementAt(closeindex);
       // if it is in there already, check if the old one is better
       if (opennode!=null)
       {
         if (opennode.g<=node_succ.g) continue; //better already. skip to next
       }
       if (closenode!=null)
       {
         if (closenode.g<=node_succ.g) continue; //better already. skip to next
       }
       //remove any worse old nodes
       closed.remove(node_succ);
       open.remove(node_succ);
       //add this new node
       open.put(node_succ,node_succ.f());
     }
     closed.add(node_current);
     it++;
    }
    // this route is too complex - straight line time
    if (it==maxit) line(start,end);
  }

  private void line(Point start,Point end)
  {
   Line2D.Double l = new Line2D.Double(start.x,start.y,end.x,end.y);
   g.draw(l);
  }

  //returns where we can go in each direction without hitting anything
  //stops 5 pixels away from any obstacle
  Point[] getSuccessors(Point start,Point goal)
  {
    Point[] ret = new Point[6];
    // head toward vertically
    ret[0]=goVertically(start,goal);
    // head toward horizontally
    ret[1]=goHorizontally(start,goal);
    // head 50 pixels either way horizontall
    ret[2] = goHorizontally(start,new Point(start.x-50,start.y));
    ret[3] = goHorizontally(start,new Point(start.x+50,start.y));
    // head 50 pixels either way vertically
    ret[4] = goVertically(start,new Point(start.x,start.y-50));
    ret[5] = goVertically(start,new Point(start.x,start.y+50));
    return ret;
  }



  //draw the route through all parents this has followed
  void makeRoute(RouteNode n)
  {
    route=n;
    routedWire = new GeneralPath();
    RouteNode parent = n.parent;
    while (parent!=null)
    {
      Line2D.Double l = new Line2D.Double(parent.p.x,parent.p.y,n.p.x,n.p.y);
      routedWire.append(l,false);
      n=parent;
      parent=n.parent;
    }
  }

  public String getType()
  {
    return "ROUTED";
  }

   //estimate the distance to goal - uses manhattan distance
   int distEstimate(Point start,Point end)
  {
    return (Math.abs(start.x-end.x)+Math.abs(start.y-end.y));
  }

  // gets all the obstacles this needs to avoid
  private void getAllObjects()
  {
    Vector c = w.getTo().getCircuit().getComponents();
    allObjects=new Vector();
    int i=0;
    while (i<c.size())
    {
      ComponentModel cm = (ComponentModel)c.elementAt(i);
      Shape s=cm.getView().getShape();
      if (cm!=w.getTo() && cm!=w.getFrom() && !s.contains(start.x,start.y) && !s.contains(end.x,end.y))
        allObjects.add(s);
      i++;
    }
    /*Vector wires =w.getTo().getCircuit().getWires();
    i=0;
    while (i<wires.size())
    {
      WireModel w = (WireModel)wires.elementAt(i);
      if (w!=this.w){
      Shape shp = w.getView().getShape();
      allObjects.add(shp);}
      i++;
    }*/
  }

 private Point goHorizontally(Point start,Point end)
 {
   //make line horizontal from start toward end
   end = new Point(end.x,start.y);
   //if we are being asked to make a non-existant line, suggest going further
   if (start.equals(end)) { return end;}
   Point farx = end;
   //is line right or left
   boolean right=(start.x<end.x);
   // make shape to represent line
   Area line;
   if (right) line = new Area(new Rectangle(start.x,start.y-2,(end.x-start.x),4));
   else line = new Area(new Rectangle(end.x,start.y-2,(start.x-end.x),4));
   // loop through obstacles
   int i=0;
   while (i<allObjects.size())
   {
     Shape s = (Shape) allObjects.elementAt(i);

     if (s.intersects(line.getBounds2D()) ) //cant crash into something you start in
     {

       if (right) // we want the leftmost obstacle
       {
         int leftboundary=(int)s.getBounds().getMinX()-5;
         if (leftboundary<farx.x) farx.x=leftboundary;
       }
       else // we want the rightmost obstacle
       {
         int rightboundary=(int)s.getBounds().getMaxX()+5;
         if (rightboundary>farx.x) farx.x=rightboundary;
       }
     }
     i++; //next object
   }
   return farx;
 }

 private Point goVertically(Point start,Point end)
 {
   //make line vertical from start toward end
   end = new Point(start.x,end.y);
   if (start.equals(end)) { return end;}
   Point fary = end;
  //is line up or down
  boolean up=(start.y>end.y);
  // make shape to represent line
  Area line;
  if (up) line = new Area(new Rectangle(end.x-2,end.y,4,(start.y-end.y)));
  else line = new Area(new Rectangle(end.x-2,start.y,4,(end.y-start.y)));
  // loop through obstacles
  int i=0;
  while (i<allObjects.size())
  {
   Shape s = (Shape) allObjects.elementAt(i);
   // crashed into something
   if (s.intersects(line.getBounds2D())  )
   {
     if (up) // we want the bottomost obstacle
     {
       int bottom=(int)s.getBounds().getMaxY()+5;
       if (bottom>fary.y) fary.y=bottom;
     }
     else // we want the topmost obstacle
     {
       int top=(int)s.getBounds().getMinY()-5;
       if (top<fary.y) fary.y=top;
     }
   }
   i++; //next object
 }
   return fary;
 }

 public void locationChanged(Paintable p)
 {
   // repaint if any ComponentView has moved at all
   if ( (p!=this) && (p instanceof ComponentView) )
   {
     if (getContainer()!=null)
     this.getContainer().dirty(routedWire.getBounds());
     routeWire();
     this.repaint();
   }
 }
}
class RouteNode
{
  public RouteNode parent=null;
  public Point p=null;
  public int g=0;
  public int h=0;
  public int f() {return g+h;}

  RouteNode(Point p)
  {
    this.p=p;
  }

  public boolean equals(Object a)
  {
    try
    {
      RouteNode rn = (RouteNode) a;
      return rn.p.equals(this.p);
    }
    catch (ClassCastException e) {return false;}
  }
  public int getCost() {return f();}
}
