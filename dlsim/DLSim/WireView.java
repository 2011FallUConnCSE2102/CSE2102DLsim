
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.JPopupMenu;
import dlsim.DLSim.Util.*;

public class WireView extends Paintable implements LocationListener {

  WireModel w;

  public  Color wirecolor = new Color(255,255,255,150);
  public  Color selectedcolor = Color.red;
  public static boolean transparencyAllowed=true;


  public WireView()
  {
  }

  public WireView(WireModel w)
  {
  this.w=w;
  w.getTo().getView().addLocationListener(this);
  w.getFrom().getView().addLocationListener(this);
  makeShape();
  }

  public void setTransparency()
  {
  if (!WireView.transparencyAllowed) wirecolor=new Color(wirecolor.getRed(),wirecolor.getGreen(),wirecolor.getBlue());
  else wirecolor=new Color(wirecolor.getRed(),wirecolor.getGreen(),wirecolor.getBlue(),150);
  }

  private boolean selected;

  public Point getLocation()
  {
    return w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
  }

  public JPopupMenu getMenu()
  {
    return w.getMenu();
  }

  public void setLocationImpl(Point p)
  {

  }
  public boolean getSelected()
  {
   return selected;
  }

  public void setSelectedImpl(boolean b)
  {
   if (b==getSelected()) return;
   selected=b;
  }

  /** Paint a wire using three horizontal/vertical segments  */
  public void paint(Graphics g)
 {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setPaint(wirecolor);
    if (getSelected()) g2d.setPaint(selectedcolor);
    g2d.fill(myShape);
  }

  Shape selector = new Area();

  public Shape getSelector()
  {
    return selector;
  }

  public String getType()
  {
    return "STRAIGHT";
  }

  /**
   * Create the wire. This needs to be called every time a terminal moves
   */
  void makeShape()
  {
   Point start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
   Point end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
   if (start.x>end.x) {Point tmp=start;start=end;end=tmp;}
   int dx = Math.abs(start.x-end.x);
   int dy = end.y-start.y;
   Point a = new Point(start.x+(dx/2),start.y);
   Point b = new Point(start.x+(dx/2),end.y);
   Shape[] s = new Shape[4];
   Rectangle h1 = staticUtils.boundingRect(
                 new Point(start.x,start.y-1),
                 new Point(a.x,a.y+1));
   Rectangle h2 = staticUtils.boundingRect(
                 new Point(a.x+1,a.y),
                 new Point(b.x-1,b.y));
   Rectangle h3 = staticUtils.boundingRect(
                 new Point(b.x,b.y-1),
                 new Point(end.x,b.y+1));
  selector = new Ellipse2D.Double(start.x+(dx/2) - 5, start.y+(dy/2) -5, 10 , 10);
  Area area = new Area();
  area.add(new Area(h1));
  area.add(new Area(h2));
  area.add(new Area(h3));
  area.add(new Area(getSelector()));
  myShape = area;
  }
  private Shape myShape = new Area();



  public Rectangle getBoundingRect()
  {
    Point start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
    Point end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
    return staticUtils.bigBoundingRect(start,end);
  }

  public Point getStartPoint()
  {
   return w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
  }
  public Point getEndPoint()
    {
  return w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
  }

  public void locationChanged(Paintable p)
  {
    // one of the terminals we are connected to has moved, so its time to repaint.
    if (getContainer()!=null)
    this.getContainer().dirty(myShape.getBounds());
    makeShape();
    this.repaint();
  }

  public Shape getShape()
  {
    return myShape;
  }
}