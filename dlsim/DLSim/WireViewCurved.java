package dlsim.DLSim;
import java.awt.*;
import java.awt.geom.*;
import dlsim.DLSim.Util.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class WireViewCurved extends WireView
{

  CubicCurve2D cc;
  public WireViewCurved(WireModel w)
  {
    this.w=w;
    w.getTo().getView().addLocationListener(this);
    w.getFrom().getView().addLocationListener(this);
    makeShape();
  }

  public void paint(Graphics g)
 {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setPaint(wirecolor);
    if (getSelected()) g2d.setPaint(selectedcolor);
    g2d.fill(selector);
    g2d.draw(cc);
  }

 Shape selector;


 void makeShape()
 {
    Point start = w.getFrom().getView().getPositionOfOutput(w.getOutputTerminalNumber());
    Point end = w.getTo().getView().getPositionOfInput(w.getInputTerminalNumber());
    int dx = (end.x-start.x);
    int dy = (end.y -start.y);
    Point c1=new Point(start.x+(dx/2),start.y);
    Point c2=new Point(start.x+(dx/2),end.y);
    cc = new CubicCurve2D.Double(start.x,start.y,c1.x,c1.y,c2.x,c2.y,end.x,end.y);
    selector = new Ellipse2D.Double(start.x+(dx/2) - 5, start.y+(dy/2) -5, 10 , 10);
 }

 public Shape getSelector()
 {
   return selector;
 }

 public Shape getShape()
 {
   Area a = new Area();
   if (cc!=null)
   a.add(new Area(cc));
   if (selector!=null)
   a.add(new Area(getSelector()));
   return a;
 }

 public String getType()
 {
   return "CURVED";
  }
}