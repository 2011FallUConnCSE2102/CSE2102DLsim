package dlsim.DLSim;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * <p>Title:  PaintableContainer </p>
 * <p>Description: A component that contains a number of user positionable
 * draggable selectable 'paintables' with automatic sizing scrolling and stuff!
 *
 * @author M.Leslie
 * @version 1.0
 */
public class PaintableContainer extends JPanel implements LocationListener {

  private Vector paintables = new Vector();
  private Rectangle dirtyRect = new Rectangle();
  Object lock = new Object();
  double totaltime=0;
  double totalframes=0;

  public PaintableContainer()
  {
    super();
    this.setDoubleBuffered(false);
    this.setAutoscrolls(true);
    this.setControlClass(new PaintableContainerControl(this) );
    this.setBackground(CircuitView.background);
  }

  /** Override swing methods */
  public void paintComponent(Graphics g)
  {
    long startTime = System.currentTimeMillis();
    synchronized(lock)
    {
      super.paintComponent(g);

      // the paint manager will kindly have told us which bit to paint
      Rectangle r = g.getClipBounds();
      Debug.graphics("In paintComponent, "+r.x+","+r.y+","+r.width+","+r.height);
      grid(g);
      Vector v = this.getPaintablesIn(r);
      Enumeration e = v.elements();
      while (e.hasMoreElements())
      {
        ( (Paintable) (e.nextElement()) ).paint(g);
      }
      //we have now cleared all the dirty reigons
      dirtyRect = null;
      // presrve old line and sel rect
      if (oldLine!=null) drawLine(g,oldLine);
      if (oldSelectionRectangle!=null) drawRect(g,oldSelectionRectangle);
    }
    if (Debug.performance)
    {
      double time = (System.currentTimeMillis()-startTime);
      if (time>0)
        Debug.performance("Point "+1000/time+" fps");
        totaltime+=time;
        totalframes++;
        Debug.performance("Average "+totalframes*1000/totaltime+" fps");
    }

  }

  PaintableContainerControl mycontrol;



  public PaintableContainerControl getControlClass()
  {
    return mycontrol;
  }

  public void setControlClass(PaintableContainerControl pcc)
 {
    if (mycontrol!=null)
    {
      this.removeMouseListener(mycontrol);
      this.removeMouseMotionListener(mycontrol);
    }
      mycontrol=pcc;
      this.addMouseListener(mycontrol);
      this.addMouseMotionListener(mycontrol);
  }


  /** Adds this paintable to the container*/
  public void addPaintable(Paintable p)
  {
    synchronized(lock)
    {
      p.setContainer(this);
      p.addLocationListener(this);
      if (!paintables.contains(p))
        paintables.add(p);
      fit();
    }
    p.repaint();
  }

  /** Removes this paintable to the container*/
  public void removePaintable(Paintable p)
  {
    synchronized(lock)
    {
    Vector v = new Vector();
    v.add(p);
    mycontrol.deselect(v);
    paintables.remove(p);
    p.removeLocationListener(this);
    }
    dirty(p.getShape().getBounds());
  }

  /** Removes all paintables from container*/
  public void clear()
  {
    synchronized(lock)
    {
    paintables.removeAllElements();
    selectionRectangle=null;
    this.repaint();
    }
  }

  /** Mark this area as dirty and in need of repainting*/
  public void dirty(Rectangle r)
  {
    if (r==null) return;
    synchronized(lock)
    {
    r = enclosingRect(r);
    if (dirtyRect!=null)
      dirtyRect = dirtyRect.union(r);
    else
      dirtyRect = r;
    this.repaint(30,dirtyRect.x,dirtyRect.y,dirtyRect.width,dirtyRect.height);
    Debug.graphics("Marking as dirty, "+r.x+","+r.y+","+r.width+","+r.height);
    Debug.graphics("Dirty rect now, "+dirtyRect.x+","+dirtyRect.y+","+dirtyRect.width+","+dirtyRect.height);
    }

  }


  private Rectangle enclosingRect(Rectangle r)
  {
    int x = r.x-1;
    if (x<0) x=0;
    int y = r.y-1;
    if (y<0) y=0;
    return new Rectangle(x,y,r.width+2,r.height+2);
  }

  /** Repaint this paintable */
  public void repaint(Paintable p)
  {
       Rectangle r = p.getShape().getBounds();
       dirty(r);
  }

  /**
   * @param r the rectangle to get paintables in
   * @return  the paintables whose shape intersects this rectangle
   */
  public Vector getPaintablesIn(Rectangle r)
  {
   Vector v = new Vector();
   Enumeration e = paintables.elements();
    while (e.hasMoreElements())
    {
      Paintable p = (Paintable) e.nextElement();
      Shape s = p.getShape();
      if (s.intersects(r))
        v.add(p);
    }
    return v;
  }

  private Vector locationListeners = new Vector();

  /**
   * Listners are notified whenever anything in this moves.
   * @param p the Listner to add
   */
  public final void addLocationListener(LocationListener p)
  {
    if (p==null) return;
    if (!locationListeners.contains(p))
      locationListeners.add(p);
  }

  /**
   * Listners are notified whenever  anything in this moves.
   * @param p the Listner to remove
   */
  public final void removeLocationListener(LocationListener p)
  {
    locationListeners.remove(p);
  }


  private void notifyListeners(Paintable p)
  {
    Enumeration e = locationListeners.elements();
    while (e.hasMoreElements())
    {
      ((LocationListener) e.nextElement()).locationChanged(p);
    }
  }

  /**
  * @param r the rectangle to get paintables in
  * @return  the paintables whose shape intersects this rectangle
  */
 public Vector getSelectablesIn(Rectangle r)
 {
  Vector v = new Vector();
  Enumeration e = paintables.elements();
   while (e.hasMoreElements())
   {
     Paintable p = (Paintable) e.nextElement();
     Shape s = p.getSelector();
     if (s.intersects(r))
       v.add(p);
   }
   return v;
  }

  Rectangle oldSelectionRectangle;
  Rectangle selectionRectangle;

  /**
   * Clear the old selection rectangle
   */
  public void setSelectionRectangle(Rectangle r)
  {
    synchronized(lock)
    {
      Graphics g = this.getGraphics();
      if (oldSelectionRectangle!=null) drawRect(g,oldSelectionRectangle);
      if (r!=null)
      {
        drawRect(g,r);
      }
      oldSelectionRectangle=r;
    }
  }

  Line2D oldLine;

  public void setSelectionLine(Point start,Point end)
  {
    synchronized(lock)
    {
      Graphics g = this.getGraphics();
      if (oldLine!=null) drawLine(g,oldLine);
      Line2D newLine=null;
      if (start!=null)
      {
          newLine = new Line2D.Double(start,end);
          drawLine(g,newLine);
      }
      oldLine=newLine;
    }
  }

  public void drawLine(Graphics g,Line2D l2d)
  {
    g.setColor(CircuitView.background);
    g.setXORMode(Color.black);
    ((Graphics2D)g).draw(l2d);
    g.setPaintMode();
  }

  public void drawRect(Graphics g,Rectangle r)
 {
   g.setColor(CircuitView.background);
   g.setXORMode(Color.black);
   ((Graphics2D)g).draw(r);
   g.setPaintMode();
  }

  /**
   * Clear the old selection rectangle, and draw this instead
   */
  private void drawSelectionRectangle(Graphics g)
  {
    g.setColor(this.getBackground());
    g.setXORMode(Color.black);
    if (selectionRectangle!=null)
    {
    Debug.graphics("draw selection rectangle "+selectionRectangle);
    g.drawRect(selectionRectangle.x,
              selectionRectangle.y,
              selectionRectangle.width,
               selectionRectangle.height);
    oldSelectionRectangle=selectionRectangle;
    }
    g.setPaintMode();
  }

  /**
    * @param point the point to get paintables in
    * @return the paintables that contain thise point
    */
   public Vector getPaintables()
   {
     return (Vector)paintables.clone();
   }

  /**
   * @param point the point to get paintables in
   * @return the paintables that contain thise point
   */
  public Vector getPaintablesAt(Point point)
  {
    Vector v = new Vector();
    Enumeration e = paintables.elements();
    while (e.hasMoreElements())
    {
      Paintable p = (Paintable) e.nextElement();
      Shape s = p.getShape();
      if (s.contains(point))
        v.add(p);
    }
    return v;
  }

  /**
     * @param point the point to get paintables in
     * @return the paintables whose selector contains this point
     */
  public Vector getSelectablesAt(Point point)
 {
   Vector v = new Vector();
   Enumeration e = paintables.elements();
   while (e.hasMoreElements())
   {
     Paintable p = (Paintable) e.nextElement();
     Shape s = p.getSelector();
     if (s.contains(point))
     {
       v.add(p);
       // return only one at a time
       return v;
     }
   }
    return v;
 }
  private int gridd=15;

  /**
   * Returns the size of the grid to snap to
   */
  public int getGridDistance()
  {
    return gridd;
  }

  public Point getNearestGridPoint(Point p)
  {
    int x,y;
    double xdiv = p.x*1.0/gridd;
    x=(int)Math.rint(xdiv)*gridd;
    if (x<0) x=0;
    double ydiv = p.y*1.0/gridd;
    y=(int)Math.rint(ydiv)*gridd;
    if (y<0) y=0;
    return new Point(x,y);
  }

  /**
   * Set the size of the grid to snap to
   * @param i grid size. Set to 1 for no snapping
   */
  public void setGridDistance(int i)
  {
    gridd=i;
  }

  /**
   * Tell container whether or not to scroll so any paintable moved is always in view
   * @param b
   */
  public void setScrolls(boolean b)
  {
    scrolls=b;
  }

  private boolean scrolls=true;
  /**
   * Listens to locations changing, and try and scroll so they are visible
   */
  public void locationChanged(Paintable p)
  {
    notifyListeners(p);
    if (!scrolls) return;
    Rectangle r = p.getShape().getBounds();
    if (!this.getVisibleRect().contains(r))
    {
     makeRectangleVisible(r);
    }
  }

  /**
   * Makes sure this rectangle exists,resizing if necsssary, and scrolls there
   * @param r
   */
  public void makeRectangleVisible(Rectangle r)
  {
    // check if we need to resize
     if (!new Rectangle(this.getSize()).contains(r))
     {
      Debug.graphics("Resizing!");
       int w = (int)Math.max(this.getWidth(),r.getMaxX()+200);
       int h = (int)Math.max(this.getHeight(),r.getMaxY()+200);
       Dimension d = new Dimension(w,h);
       this.setPreferredSize(d);
       this.setMinimumSize(d);
       this.setMaximumSize(d);
       this.setSize(d);
       this.revalidate();
       this.getParent().validate();
     }
     Debug.graphics("Scrolling!");
      this.scrollRectToVisible(r);
  }
  private Image gridBuffer;

  private void makeGridBuffer()
  {
    int w= 90;
    int h = 90;
    gridBuffer = this.createImage(w,h);
    Graphics2D g2d = (Graphics2D) gridBuffer.getGraphics();
    g2d.setColor(this.getBackground());
    g2d.fillRect(0,0,w,h);
    Line2D.Double l;
    double x0=0;
    double x1=w;
    double y0=0;
    double y1=h;
    //dashed line style
     float[] dashPattern = { 2, 2 };
     g2d.setColor(CircuitView.grid);
     g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                  BasicStroke.JOIN_MITER, 10,
                                  dashPattern, 0));
    //draw horizontal
    double x,y;
    for (x=x0;x<x1;x+=gridd)
    {
      l = new Line2D.Double(x,y0,x,y1);
      g2d.draw(l);
    }
    //draw vertical
    for (y=y0;y<y1;y+=gridd)
    {
      l = new Line2D.Double(x0,y,x1,y);
      g2d.draw(l);
    }
  }
  private void grid(Graphics g)
  {
    if (gridBuffer==null)
    {
      makeGridBuffer();
    }
   // tile the area with gridbuffer
    Rectangle r = g.getClipBounds();
    double minx = r.getMinX();
    double miny = r.getMinY();
    double maxx = r.getMaxX();
    double maxy = r.getMaxY();
    double x0 = minx - (minx%gridd);
    double y0 = miny - (miny%gridd);
    double h = gridBuffer.getHeight(this);
    double w = gridBuffer.getWidth(this);
    for (double y=y0;y<maxy;y+=h)
    {
      for (double x=x0;x<maxx;x+=w)
      {
        g.drawImage(gridBuffer,(int)x,(int)y,this);
      }
    }
  }

  private void fit()
    {
      Dimension d = new Dimension(790,550);
      int maxx=0;
      int maxy=0;
      Enumeration e = paintables.elements();
          while (e.hasMoreElements())
          {
           Paintable p = (Paintable) e.nextElement();
           Rectangle r = p.getShape().getBounds();
           int rmaxx = (int)r.getMaxX();
           int rmaxy = (int)r.getMaxY();
           if (rmaxx > maxx) maxx=rmaxx;
           if (rmaxy > maxy) maxy=rmaxy;
          }
      d= new Dimension(maxx,maxy);
      setPreferredSize(d);
    }

  public void setFloatingSelection(CircuitViewInterface.FloatingSelection c)
  {
    mycontrol.setFloatingSelection(c);
  }


}
