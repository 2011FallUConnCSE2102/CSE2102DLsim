package dlsim.DLSim;

import javax.swing.event.*;
import javax.swing.*;

import dlsim.DLSim.Util.staticUtils;
/*import dlsim.DLSim.Util.*;*/


/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIMenuBar extends JMenuBar implements ChangeListener
{


  public UIMenuBar(UIActions actions)
  {
    JMenu File = new JMenu2("File");
    File.setMnemonic(KeyStroke.getKeyStroke("alt F").getKeyCode());
    File.add(actions.newFile).setAccelerator(KeyStroke.getKeyStroke("alt N"));;
    File.addSeparator();
    File.add(actions.loadFile).setAccelerator(KeyStroke.getKeyStroke("alt O"));
    File.add(actions.saveFile).setAccelerator(KeyStroke.getKeyStroke("alt S"));
    File.add(actions.saveFileAs).setAccelerator(KeyStroke.getKeyStroke("alt A"));
    File.add(actions.exit).setAccelerator(KeyStroke.getKeyStroke("alt X"));
    JMenu Edit = new JMenu2("Edit");
    Edit.setMnemonic(KeyStroke.getKeyStroke("alt E").getKeyCode());
    Edit.add(actions.undo).setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
    Edit.add(actions.redo).setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
    Edit.addSeparator();
    Edit.add(actions.doCopy).setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
    Edit.add(actions.doDelete);
    Edit.add(actions.selectAll).setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
    JMenu Add = new JMenu2("Insert");
    Add.setMnemonic(KeyStroke.getKeyStroke("alt I").getKeyCode());
    Add.add(actions.addAnd).setIcon(null);
    Add.add(actions.addOnOff).setIcon(null);
    Add.add(actions.addOr).setIcon(null);
    Add.add(actions.addNot).setIcon(null);
    Add.add(actions.addSwitch).setIcon(null);
    Add.add(actions.addPlus).setIcon(null);
    Add.add(actions.addInput).setIcon(null);
    Add.add(actions.addOutput).setIcon(null);
    Add.add(actions.addClock).setIcon(null);
    Add.add(actions.addLatch).setIcon(null);
    Add.add(actions.addOscilloscope).setIcon(null);
    Add.add(actions.addFromFile);
    JMenu Options = new JMenu2("Options");
    Options.setMnemonic(KeyStroke.getKeyStroke("alt O").getKeyCode());
    Options.add(actions.windowsLF);
    Options.add(actions.motifLF);
    Options.add(actions.metalLF);
    Options.addSeparator();
    Options.add(actions.straightWires);
    Options.add(actions.curvedWires);
    //Options.add(actions.routedWires);
    Options.addSeparator();
    // icon sizeing doesnt do anything
    //Options.add(actions.iconSize);
    // changing detail doesnt do anything
    //Options.add(actions.changeDetail);
    snapgrid = new JCheckBoxMenuItem ();
    snapgrid.setSelected(staticUtils.snapToGrid());
    snapgrid.setText("Snap to grid");
    Options.add(snapgrid);
    snapgrid.addChangeListener(this);
    JMenu Simulation = new JMenu2("Simulation");
    Simulation.setMnemonic(KeyStroke.getKeyStroke("alt M").getKeyCode());
    JMenuItem start = Simulation.add(actions.startSimulation);

    start.setAccelerator(KeyStroke.getKeyStroke(']',1));
    JMenuItem stop = Simulation.add(actions.stopSimulation);
    stop.setAccelerator(KeyStroke.getKeyStroke('[',1));
    JMenuItem step = Simulation.add(actions.stepSimulation);
    step.setAccelerator(KeyStroke.getKeyStroke('.',1));
    JMenu Tools = new JMenu2("Tools");
    Tools.setMnemonic(KeyStroke.getKeyStroke("alt T").getKeyCode());
    Tools.add(actions.countComponents);
    Tools.add(actions.bundleWires).setIcon(null);
    JMenu Help = new JMenu2("Help");
    Help.add(actions.showAbout);
    this.add(File);
    this.add(Edit);
    this.add(Add);
    this.add(Tools);
    this.add(Simulation);
    this.add(Options);
    this.add(Help);

  }

  JCheckBoxMenuItem  snapgrid;

  public void stateChanged(ChangeEvent e)
  {
    if (snapgrid.isSelected())
    {
      Preferences.setValue("SNAPTOGRID","TRUE");
    }
    else
    {
      Preferences.setValue("SNAPTOGRID","FALSE");
    }
  }

}

class JMenu2 extends JMenu
{
  public JMenu2(String s)
  {
   super(s);
  }

   public JMenuItem add(Action a)
  {
   JMenuItem m = super.add(a);
   m.setAccelerator((KeyStroke)a.getValue(Action.ACCELERATOR_KEY));
   return m;
  }
}

