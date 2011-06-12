package dlsim.DLSim;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Hashtable;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIToolBar extends javax.swing.JToolBar implements PropertyChangeListener, ChangeListener
{
UIActions actions;
JButton undo,redo;
JSlider speed = new JSlider();

  public UIToolBar(UIActions actions)
  {
  this.actions=actions;
    UIToolBar toolBar = this;
    toolBar.add(actions.newFile);
    toolBar.add(actions.loadFile);
    toolBar.add(actions.saveFile);
    toolBar.add(actions.saveFileAs);
    toolBar.addSeparator();
    toolBar.add(actions.doCopy);
    toolBar.add(actions.doDelete);
    toolBar.addSeparator();
    undo = toolBar.add(actions.undo);
    undo.addPropertyChangeListener(JButton.TEXT_CHANGED_PROPERTY,this);
    undo.setText("");
    redo = toolBar.add(actions.redo);
    redo.setText("");
    redo.addPropertyChangeListener(JButton.TEXT_CHANGED_PROPERTY,this);
    toolBar.addSeparator();
    toolBar.add(actions.straightWires);
    toolBar.add(actions.curvedWires);
    toolBar.add(actions.routedWires);
    toolBar.add(actions.bundleWires);
    toolBar.addSeparator();
    toolBar.add(actions.startSimulation);
    toolBar.add(actions.stopSimulation);
    toolBar.add(actions.stepSimulation);
    toolBar.addSeparator();
    JLabel label = new JLabel("Speed");
    label.setVerticalAlignment(SwingConstants.TOP);
    toolBar.add(label);
    this.add(speed, null);
    speed.setPaintTicks(false);
    speed.setMaximumSize(new Dimension(200,30));
    speed.setPreferredSize(new Dimension(250,30));
    speed.setMaximum(1000);
    speed.setMinimum(10);
    speed.setMajorTickSpacing(50);
    Hashtable labelTable = new Hashtable();
    labelTable.put( new Integer( 10 ), new JLabel("Fast") );
    labelTable.put( new Integer( 1000 ), new JLabel("Slow") );
    speed.setValue(500);
    speed.setPaintLabels(true);
    speed.addChangeListener(this);
    speed.setLabelTable( labelTable );
  }

  public void propertyChange(PropertyChangeEvent c)
 {
   if (c.getNewValue().equals("")) return;
   if (undo==null) return;
   if (redo==null) return;
   undo.setText("");
   redo.setText("");
  }

  public void stateChanged(ChangeEvent e)
  {
   actions.simulator.setSpeed(speed.getValue());
  }


}



