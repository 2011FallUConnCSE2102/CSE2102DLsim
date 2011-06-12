package dlsim.DLSim;
import java.awt.*;
import javax.swing.Icon;
import java.awt.geom.*;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.awt.event.*;
import dlsim.DLSim.Util.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public interface CircuitViewInterface
{

/**
* Add a paintable (wire/component) to the circuit view
* @param w paintable to be added
*/
public void add(Paintable p);

/**
 * Remove a paintable (wire/component) from the view
 * @param w paintable to be removed
 */
public void remove(Paintable p);


/**
 * Request this will be redrawn next time updateTerminals() is called
 * @param v Request that this components terminals be updated
 */
public void requestUpdateTerminals(ComponentView v);

/**
 * Update the terminals that have requested this
 */
public void updateTerminals();



/**
 * @return The class interpreting mouse input for this clas
 */
public CircuitControlInterface getControl();


public void revalidate();

/**
 * Remove everything from the view
 */
public void clear();

/**
 * Return the size of the view
 */
public Dimension getSize();

/**
 * @param cci The class to interpret mouse input for this class
 */
public void setControl(CircuitControlInterface cci);





public abstract class FloatingSelection extends ComponentFactory
{
  public abstract Icon getIcon();
}

/**
 *
 * @param c The selection will follow the mouse around, untill it is clicked
 * It will then add a component from the factory
 */
void setFloatingSelection(CircuitViewInterface.FloatingSelection c);
}