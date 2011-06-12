package dlsim.DLSim;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.JPopupMenu;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class WireBundleView extends WireView
{

WireBundle wb;
GeneralPath myWire = new GeneralPath();
Shape selector= new Area();

  public WireBundleView(WireBundle wb)
  {
    super();
    wb.setView(this);
    this.wb=wb;
    makeShape();
  }

  /** Paint a wire using three horizontal/vertical segments  */
  public void paint(Graphics g)
  {
    Graphics2D g2g = (Graphics2D) g;
    g2g.setPaint(wirecolor);
    if (getSelected()) g2g.setPaint(Color.RED);
    g2g.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
    g2g.draw(myWire);
    g2g.fill(selector);
  }

  public JPopupMenu getMenu()
  {
    return wb.getMenu();
  }


  public void makeShape()
  {
    // calculate means
     myWire = new GeneralPath();
    Enumeration wires = this.wb.getWires().elements();
    // calculate point where the 'bus' will start
    int totalstartx=0;
    int totalstarty=0;
    int totalendx=0;
    int totalendy=0;
    int nwires=0;
    while (wires.hasMoreElements())
    {
      WireModel w = (WireModel) wires.nextElement();
      nwires++;
      Point start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
      Point end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
      totalstartx+=start.x+5;
      totalstarty+=start.y;
      totalendx+=end.x;
      totalendy+=end.y;
    }

    // average start pos
    int meanstartx = (int)(1.0*totalstartx/nwires)+20;
    int meanstarty = (int)(1.0*totalstarty/nwires);
    Point meanstart = new Point(meanstartx,meanstarty);

    //average end pos
    int meanendx = (int)(1.0*totalendx/nwires)-20;
    int meanendy = (int)(1.0*totalendy/nwires);
    Point meanend = new Point(meanendx,meanendy);

    // draw lines to these middle points from each terminal
    wires = wb.getWires().elements();
    while (wires.hasMoreElements())
    {
      WireModel w = (WireModel) wires.nextElement();
      Point start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
      Point end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
      Line2D.Double starthoriz = new Line2D.Double(start.x,start.y,meanstart.x,start.y);
      Line2D.Double startvert = new Line2D.Double(meanstart.x,start.y,meanstart.x,meanstart.y);
      Line2D.Double endhoriz = new Line2D.Double(end.x,end.y,meanend.x,end.y);
      Line2D.Double endvert = new Line2D.Double(meanend.x,end.y,meanend.x,meanend.y);
      myWire.append(startvert,false);
      myWire.append(starthoriz,false);
      myWire.append(endvert,false);
      myWire.append(endhoriz,false);
     }

     //connect the mean points with a thicker line
     if (meanstart.x-20<meanend.x+20)
     { //left to right
       drawLeftToRight(meanstart,meanend,myWire);
     }
     else
     { //right to left
        drawRightToLeft(meanstart,meanend,myWire);
     }
     // label the bus
  }



  private int top()
  {
    Enumeration e = wb.getWires().elements();
    int top = Integer.MAX_VALUE;
    while (e.hasMoreElements())
    {
      WireModel w = (WireModel)e.nextElement();
      ComponentView cv = w.getFrom().getView();
      ComponentView cv2 = w.getTo().getView();
      int topfrom = (int) cv.getShape().getBounds().getMinY();
      int topto = (int) cv2.getShape().getBounds().getMinY();
      if (topto<top) top=topto;
      if (topfrom<top) top=topfrom;
    }
    if (top<21) return 21;
    return top;
  }

  private int bottom()
  {
    Enumeration e = wb.getWires().elements();
    int bottom = 0;
    while (e.hasMoreElements())
    {
      WireModel w = (WireModel)e.nextElement();
      ComponentView cv = w.getFrom().getView();
      ComponentView cv2 = w.getTo().getView();
      int topfrom = (int) cv.getShape().getBounds().getMaxY();
      int topto = (int) cv2.getShape().getBounds().getMaxY();
      if (topto>bottom) bottom=topto;
      if (topfrom>bottom) bottom=topfrom;
    }
    return bottom;
  }

  /*private Rectangle bounds()
  {
    // the bounds of this wire
    Enumeration e = wb.getWires().elements();
    int bottom = 0;
    int top  = Integer.MAX_VALUE;
    int left = Integer.MAX_VALUE ;
    int right =  0;
    while (e.hasMoreElements())
    {
      WireModel w = (WireModel)e.nextElement();
      ComponentView cv = w.getFrom().getView();
      ComponentView cv2 = w.getTo().getView();
      int fromxmin = (int) cv.getShape().getBounds().getMinX();
      int toxmin = (int) cv2.getShape().getBounds().getMinX();
      int fromxmax = (int) cv.getShape().getBounds().getMaxX();
      int toxmax = (int) cv2.getShape().getBounds().getMaxX();
      int fromymax = (int) cv.getShape().getBounds().getMaxY();
      int toymax = (int) cv2.getShape().getBounds().getMaxY();
      int fromymin = (int) cv.getShape().getBounds().getMinY();
      int toymin = (int) cv2.getShape().getBounds().getMinY();
      if (fromymax>bottom) bottom=fromymax;
      if (toymax>bottom) bottom=toymax;
      if (fromymin<top) top=fromymin;
      if (toymin<top) top=toymin;
      if (fromxmax>right) right=fromxmax;
      if (toxmax>right) right=fromxmax;
      if (fromxmin<left) left=fromxmin;
      if (toxmin<left) left=toxmin;
    }
    return new Rectangle(left,top,(right-left),(bottom-top));
  }*/

  private void drawLeftToRight(Point from,Point to,GeneralPath myWire)
  {
    int dx = (to.x-from.x);
    int dy = (to.y-from.y);
    int w=1;
    if (to.y<from.y) w=-w;
    Line2D.Double l11 = new Line2D.Double(from.x,from.y-w,from.x+(dx/2)-w,from.y-w);  // across top
    Line2D.Double l12 = new Line2D.Double(from.x,from.y+w,from.x+(dx/2)+w,from.y+w); // across bottom
    myWire.append(l11,false);
    myWire.append(l12,false);
    Line2D.Double l21 = new Line2D.Double(from.x+(dx/2)+w,from.y-w,from.x+(dx/2)+w,to.y-w); //down left
    Line2D.Double l22 = new Line2D.Double(from.x+(dx/2)-w,from.y+w,from.x+(dx/2)-w,to.y+w); // down right
    selector= new Ellipse2D.Double(from.x+(dx/2)-5,from.y+(dy/2)-5,10,10);
    myWire.append(l22,false);
    myWire.append(l21,false);
    Line2D.Double l31 = new Line2D.Double(from.x+(dx/2)+w,to.y-w,to.x,to.y-w); // across top
    Line2D.Double l32 = new Line2D.Double(from.x+(dx/2)-w,to.y+w,to.x,to.y+w); // across bottm
    myWire.append(l31,false);
    myWire.append(l32,false);
  }

  private void drawRightToLeft(Point from,Point to,GeneralPath myWire)
  {
    Point outabit= new Point(from.x,from.y); //out o-
    Line2D.Double line11 = new Line2D.Double(from,outabit);
    Line2D.Double line12 = new Line2D.Double(from,outabit);
    Point vertical;

    int top = top()-15;
    int bottom = bottom()+15;
    int distancedown = (bottom-from.y) + (bottom-to.y);
    int distanceup = (from.y-top) + (to.y-top);

    //decided whether to go up or down
    if (distancedown<distanceup)
      //go down
    vertical = new Point(outabit.x,bottom);
    else
      //go up
    vertical = new Point(outabit.x,top);

    // line downward
    Line2D.Double line2 = new Line2D.Double(outabit,vertical);
    // line across
    Point across= new Point(to.x-20,vertical.y);
    Line2D.Double line3 = new Line2D.Double(vertical,across);
    selector = new Ellipse2D.Double(vertical.x-((vertical.x-across.x)/2)-5,vertical.y-5,10,10);
    // go vertical
    Point outabit2 = new Point(to.x-20,to.y);
     Line2D.Double line4 = new Line2D.Double(across,outabit2);
    //go in
    Point up=new Point(across.x,to.y);
    Line2D.Double line5 = new Line2D.Double(outabit2,to);
    myWire.append(line11,false);
    myWire.append(line12,false);
    myWire.append(line2,false);
    myWire.append(line3,false);
    myWire.append(line4,false);
    myWire.append(line5,false);
  }


  public Shape getShape()
  {
    Area a = new Area(myWire.getBounds());
    a.add(new Area(selector.getBounds()));
    return a.getBounds();
  }

  public Shape getSelector()
  {
    return selector;
  }


}