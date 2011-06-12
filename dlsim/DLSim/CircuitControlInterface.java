package dlsim.DLSim;
import java.awt.*;
import dlsim.DLSim.Util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.Icon;
import java.awt.geom.*;
import java.util.*;
import javax.swing.JOptionPane;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public interface CircuitControlInterface extends MouseListener, MouseMotionListener

{

void deselect();

void setSelection(Vector Components);

public void selectAll();

public Vector selectedWires();

public Vector selectedComponents();

public void translateSelection(int x, int y);


}

