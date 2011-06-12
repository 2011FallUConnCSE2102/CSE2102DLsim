package dlsim.DLSim;

import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class EmptyView implements CircuitViewInterface
{
  public ComponentModel getComponentModelAt(Point p) {
  return null;

  }
  public void add(Paintable p) {

  }
  public Dimension getSize() {
    return null;

  }
  public void setControl(CircuitControlInterface cci) {

  }
  public void remove(Paintable p) {
    return ;
  }
  public CircuitControlInterface getControl() {

    return null;
  }
  public Vector getComponentModelsIn(Rectangle r) {

    return null;
  }

  public Vector getWiresIn(Rectangle r) {

    return null;
  }
  public void setFloatingSelection(CircuitViewInterface.FloatingSelection parm1) {

  }
  public void revalidate() {

  }
  public void requestUpdateTerminals(ComponentView v) {

  }
  public void updateTerminals() {

  }
  public void clear() {

  }
  public WireBundle getWiresAt(Point p) {
    return null;
  }


}