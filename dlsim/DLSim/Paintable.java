package dlsim.DLSim;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * <p>Title:  Paintable </p>
 * <p>Description: An abstract class representing something that can be painted,
 * and moved around a container</p>
 *
 *
 * @author M.Leslie
 * @version 1.0
 */

public abstract class Paintable {

  private boolean locked=false;

  public Paintable() {}
  /**
   * This shape should be a close fit to the paintable - used for setting clip rects
   */
  public abstract Shape getShape();

  /**
   * This should paint this
   */
  public abstract void paint(Graphics g);
  /**
   * This should return the location of this
   */
  public abstract Point getLocation();
  /**
   * This should set the location of this
   */
  public abstract void setLocationImpl(Point p);

  /** Set the selection status of this component*/
  public abstract void setSelectedImpl(boolean selected);

  /** get the selection status of this component*/
  public abstract boolean getSelected();

  /** Sets the location of this paintable */
  public final void setLocation(Point p)
  {
    Debug.out("Marking old shape as dirty");
    Rectangle r = getShape().getBounds();
    if (container!=null)
    setLocationImpl(container.getNearestGridPoint(p));
    else
    setLocationImpl(p);
    notifyListeners();
    // clear old, repaint new
    if (container!=null)
    getContainer().dirty(r.union(getShape().getBounds()));
  }

  /** Translate by (x,y) */
  public final void translate(int x, int y)
  {
    Point p = getLocation();
    p.translate(x,y);
    this.setLocation(p);
  }

  private Vector locationListeners = new Vector();

  /**
   * Listners are notified whenever this moves.
   * @param p the Listner to add
   */
  public final void addLocationListener(LocationListener p)
  {
    if (p==null) return;
    if (!locationListeners.contains(p))
      locationListeners.add(p);
  }

  /**
   * Listners are notified whenever this moves.
   * @param p the Listner to remove
    */
  public final void removeLocationListener(LocationListener p)
  {
    locationListeners.remove(p);
  }


  private void notifyListeners()
  {
    Enumeration e = locationListeners.elements();
    while (e.hasMoreElements())
    {
      ((LocationListener) e.nextElement()).locationChanged(this);
    }
  }



  /** Repaints this by marking its rectangles as dirty */
  public final void repaint()
  {
    if (locked) return;
    Debug.out("Requesting a repaint");
    if (container!=null)
    getContainer().repaint(this);
  }

  public final void setSelected(boolean b)
  {
    if (b==getSelected())
      return;
    if (container!=null)
    this.getContainer().dirty(this.getShape().getBounds());
    this.setSelectedImpl(b);
    this.repaint();
  }
  private PaintableContainer container;

  /** Sets this paintables container */
  public final void setContainer(PaintableContainer container)
  {
    this.container=container;
  }

  /** Gets this paintables container */
  public final PaintableContainer getContainer()
  {
    return container;
  }

  public JPopupMenu getMenu()
  {
    return null;
  }

  /**
   * Called when the user clicks on this paintable
   */
  public void clicked(MouseEvent e)
  {

  }

  /**
   * Called when the users mouse exits this paintable
   */
  public void mouseOver(MouseEvent e)
  {

  }

  /**
   * Called when the user mouses over this paintable
   */
  public void mouseExit(MouseEvent e)
  {

  }



  /**
   * @return The area which will cause this to become selected if clicked
   * this returns getShape() unless overridden
   */
  public Shape getSelector()
  {
    return getShape();
  }


}