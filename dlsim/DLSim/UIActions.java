package dlsim.DLSim;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.Util.*;
import java.io.File;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.net.URL;
import java.net.URI;
import com.sun.java.swing.plaf.motif.*;
import com.sun.java.swing.plaf.windows.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIActions
{
  public static UIActions currentinstance;
  public ListeningAction undo;
  public ListeningAction redo;
  public AbstractAction newFile;
  public AbstractAction loadFile;
  public AbstractAction saveFile;
  public AbstractAction saveFileAs;
  public AbstractAction doCopy;
  public AbstractAction doDelete;
  public AbstractAction selectAll;
  public AbstractAction exit;
  public final AbstractAction startSimulation;
  public final AbstractAction stopSimulation;
  public final AbstractAction stepSimulation;
  public AbstractAction addAnd;
  public AbstractAction addOr;
  public AbstractAction addNot;
  public AbstractAction addPlus;
  public AbstractAction addOnOff;
  public AbstractAction addSwitch;
  public AbstractAction addInput;
  public AbstractAction addOutput;
  public AbstractAction addFromFile;
  public AbstractAction countComponents;
  public AbstractAction changeDetail;
  public AbstractAction addClock;
  public AbstractAction addLatch;
  public AbstractAction addOscilloscope;
  public SimulationRunner simulator;

  public AbstractAction motifLF = new AbstractAction("Motif Look And Feel")
    {
      public void actionPerformed(ActionEvent e)
      {
      try {
      /*tms maybe there's a default UIManager.setLookAndFeel(new MotifLookAndFeel());*/
      Preferences.setValue("LOOKANDFEEL","MOTIF");
      SwingUtilities.updateComponentTreeUI(UIMainFrame.currentinstance);
}
      catch (/*UnsupportedLookAndFeel  tms*/Exception e2) {staticUtils.errorMessage("L&F unsuportted");}
      }
    };

    public  AbstractAction bundleWires;

  public  AbstractAction windowsLF = new AbstractAction("Windows Look And Feel")
    {
      public void actionPerformed(ActionEvent e)
      {
      try {
      /*UIManager.setLookAndFeel(new WindowsLookAndFeel()); maybe there's a default tms*/
       Preferences.setValue("LOOKANDFEEL","WINDOWS");
      SwingUtilities.updateComponentTreeUI(UIMainFrame.currentinstance);}
      catch (/*UnsupportedLookAndFeel tms*/Exception e2) {staticUtils.errorMessage("L&F unsuportted");}
      }
    };

    public  AbstractAction straightWires = new AbstractAction("Staight Wires",UIImages.straightWire)
    {
      public void actionPerformed(ActionEvent e)
      {
        WireViewFactory.setWireType(WireViewFactory.STRAIGHT);
      }
    };

    public  AbstractAction curvedWires = new AbstractAction("Curved Wires",UIImages.curvedWire)
   {
     public void actionPerformed(ActionEvent e)
     {
       WireViewFactory.setWireType(WireViewFactory.CURVED);
     }
    };

   public  AbstractAction routedWires = new AbstractAction("Auto-Routed Wires",UIImages.routedWire)
   {
     public void actionPerformed(ActionEvent e)
     {
       WireViewFactory.setWireType(WireViewFactory.ROUTED);
     }
   };

  public  AbstractAction metalLF  = new AbstractAction("Java Look And Feel")
    {
      public void actionPerformed(ActionEvent e)
      {
      try {
      UIManager.setLookAndFeel(new MetalLookAndFeel());
      Preferences.setValue("LOOKANDFEEL","METAL");
      SwingUtilities.updateComponentTreeUI(UIMainFrame.currentinstance);}
      catch (UnsupportedLookAndFeelException e2) {staticUtils.errorMessage("L&F unsuportted");}
      }
    };

    public  AbstractAction iconSize  = new AbstractAction("Small Icons")
  {
    public void actionPerformed(ActionEvent e)
    {
    SwingUtilities.updateComponentTreeUI(UIMainFrame.currentinstance);
    }
  };


  public UIActions(final CircuitModel m)
  {
    this.currentinstance=this;
    simulator = new SimulationRunner(m);
    final SimulationRunner sim = simulator;
    sim.start();
    //set up start simulation action
    startSimulation = new AbstractAction("Start",UIImages.start)
    {
     public void actionPerformed(ActionEvent e)
     {
       sim.setRunning();
       this.setEnabled(false);
       stopSimulation.setEnabled(true);
       stepSimulation.setEnabled(false);
     }
    };
    startSimulation.putValue(AbstractAction.SHORT_DESCRIPTION,"Start the simulation");

    //set up stop action
    stopSimulation = new AbstractAction("Stop",UIImages.stop)
    {
     public void actionPerformed(ActionEvent e)
     {
       sim.stopRunning();
       this.setEnabled(false);
       startSimulation.setEnabled(true);
       stepSimulation.setEnabled(true);
     }
    };
    stopSimulation.setEnabled(false);
    stopSimulation.putValue(AbstractAction.SHORT_DESCRIPTION,"Stop the simulation");

    //set up step action
    stepSimulation = new AbstractAction("Step",UIImages.ff)
   {
    public void actionPerformed(ActionEvent e)
    {
      sim.runOneStep();
    }
    };
   stepSimulation.putValue(AbstractAction.SHORT_DESCRIPTION,"Advance the simulation by one step");
   stepSimulation.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,0));

   //set up bundle wires action
   bundleWires = new AbstractAction("Bundle Selected Wires Together",UIImages.bus)
      {
        public void actionPerformed(ActionEvent e)
        {
          // get wires
          Vector wires = m.getView().getControl().selectedWires();
          if (wires.size()<2) return;
          // sanity check
          ComponentModel from = ((WireModel)wires.firstElement()).getFrom();
          ComponentModel to = ((WireModel)wires.firstElement()).getTo();
          boolean fromsame = true;
          boolean tosame =true;
          Enumeration ws = wires.elements();
          while (ws.hasMoreElements() && (fromsame || tosame))
          {
            WireModel nextwire = (WireModel)ws.nextElement();
            ComponentModel nextfrom = nextwire.getFrom();
            ComponentModel nextto = nextwire.getTo();
            fromsame = fromsame && (from==nextfrom);
            tosame = tosame && (to==nextto);
          from=nextfrom;
          to=nextto;
          }
          if ((!tosame) && (!fromsame))
          {
            staticUtils.errorMessage("All wires in a bus must share a common component");
            return;
          }
          UICommand.bundleWires(wires).execute();
        }
    };
   //set up step action
   selectAll = new AbstractAction("Select All")
   {
    public void actionPerformed(ActionEvent e)
    {
     m.getView().getControl().selectAll();
    }
    };
   selectAll.putValue(AbstractAction.SHORT_DESCRIPTION,"Select All Components");
   selectAll.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK ));


    // Set up undo action
    undo = new ListeningAction("Undo Impossible",UIImages.undo)
    {
      public void commandExecuted()
      {
        if (UICommand.canUndo())
          this.setEnabled(true);
        else
          this.setEnabled(false);
        this.putValue(AbstractAction.NAME,"Undo "+UICommand.lastDoneCommandName());
        this.putValue(AbstractAction.SHORT_DESCRIPTION,"Undo "+UICommand.lastDoneCommandName());
      }

      public void actionPerformed(ActionEvent e)
      {
        if (UICommand.canUndo()) UICommand.undoLastCommand();
      }
    };
    undo.setEnabled(false);
    undo.putValue(AbstractAction.SHORT_DESCRIPTION,"Undo");
    undo.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK ));

    //set up redo action
    redo = new ListeningAction("Redo Impossible",UIImages.redo)
    {
      public void commandExecuted()
      {
        if (UICommand.canRedo())
          this.setEnabled(true);
        else
          this.setEnabled(false);
        this.putValue(AbstractAction.NAME,"Redo "+UICommand.lastUnDoneCommandName());
        this.putValue(AbstractAction.SHORT_DESCRIPTION,"Undo "+UICommand.lastUnDoneCommandName());
      }

      public void actionPerformed(ActionEvent e)
      {
        if (UICommand.canRedo()) UICommand.redoLastCommand();
      }
    };
    redo.setEnabled(false);
    redo.putValue(AbstractAction.SHORT_DESCRIPTION,"Redo Impossible");
    redo.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK ));

    //make sure these get updated when actions take place
    UICommand.addListener(undo);
    UICommand.addListener(redo);

    //set up add gate actions
    addAnd=addComponentAction("And",ComponentFactory.andFactory(m),m,UIImages.andIcon);
    addOr=addComponentAction("Or",ComponentFactory.orFactory(m),m,UIImages.orIcon);
    addNot=addComponentAction("Not",ComponentFactory.notFactory(m),m,UIImages.notIcon);
    addPlus=addComponentAction("Plus",ComponentFactory.plusFactory(m),m,UIImages.plusIcon);
    addOutput=addComponentAction("Output",ComponentFactory.outFactory(m),m,UIImages.outputIcon);
    addInput=addComponentAction("Input",ComponentFactory.inFactory(m),m,UIImages.inputIcon);
    addSwitch=addComponentAction("Switch",ComponentFactory.switchFactory(m),m,UIImages.switchIcon);
    addOnOff=addComponentAction("On/Off",ComponentFactory.onOffFactory(m),m,UIImages.onoffIcon);
    addClock=addComponentAction("Clock",ComponentFactory.clockFactory(m),m,UIImages.clockIcon);
    addLatch=addComponentAction("Latch",ComponentFactory.latchFactory(m),m,UIImages.latchIcon);
    addOscilloscope=addComponentAction("Oscilloscope",ComponentFactory.oscilloscopeFactory(m),m,UIImages.scopeIcon);

    /**
     *
     * Load file Operation
     *
     */
    loadFile = new AbstractAction("Load",UIImages.load)
    {
      public void actionPerformed(ActionEvent e)
      {
        if (AppletMain.isapplet)
        {
         LMSFileChooser lmsfc = new LMSFileChooser(JOptionPane.getRootFrame(),true);
         return;
        }
        else
        {
          JFileChooser j = new JFileChooser(Preferences.getCircuitsPath());
          j.setFileView( staticUtils.CircuitFileView());
          j.setFileFilter(staticUtils.xmlFilter);
          int returnval = j.showOpenDialog(UIMainFrame.currentinstance);
          if(returnval == JFileChooser.APPROVE_OPTION)
          {
            //create a command and execute it
            File f = j.getSelectedFile();

            //check the filename
            if (!f.exists())
            {
              File g = new File(Preferences.getCircuitsPath(),f.getName()+".circuit.xml");
              if (g.exists())
              {
                f=g;
              }
              else
              {
                g = new File(Preferences.getCircuitsPath(),f.getName()+".component.xml");
                if (g.exists())
                {
                  f=g;
                }
                else
                {
                  staticUtils.errorMessage("File not found");
                  return;
                }
              }
            }
            UICommand.loadFile(m,f).execute();
          }
        }
      }
    };
    loadFile.putValue(AbstractAction.SHORT_DESCRIPTION,"Load a new circuit from disk");
    loadFile.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK ));



    /**
     *
     * Save to file Operation
     *
     */
    saveFileAs = new AbstractAction("Save As",UIImages.saveAs)
    {
      public void actionPerformed(ActionEvent e)
      {
      if (AppletMain.isapplet)
        {
         LMSFileChooser lmsfc = new LMSFileChooser(JOptionPane.getRootFrame(),false);
         return;
        }
        JFileChooser j = new JFileChooser(Preferences.getCircuitsPath());
        j.setFileView( staticUtils.CircuitFileView());
        j.setFileFilter(staticUtils.xmlFilter);
        int returnv = j.showSaveDialog(UIMainFrame.currentinstance);
        if (returnv==JFileChooser.APPROVE_OPTION)
        {
          // check the files extensions
          File f = j.getSelectedFile();
          f= checkExtension(f,m);
          //check this circuit doesnt link to the chosen file
          if (m.hasChild(staticUtils.URLFromFile(f)))
          {
        	  dlsim.DLSim.Util.staticUtils.errorMessage("Not allowed, This would create recursion!");
            return;
          }

          //check if we need to overwrite
          if (f.exists())
              { int i = JOptionPane.showConfirmDialog(UIMainFrame.currentinstance,""+f+"\n Replace This File?","Confirm Overwrite",JOptionPane.YES_NO_OPTION);
          try { if (i==JOptionPane.YES_OPTION) {f.delete(); f.createNewFile();}  else return;}
          catch (Exception e2) {JOptionPane.showMessageDialog(UIMainFrame.currentinstance,"Error, couldnt replace file");return;}
          }

          //do the actual saving
          UICommand.saveFile(m,f).execute();
        }
      }
    };
    saveFileAs.putValue(AbstractAction.SHORT_DESCRIPTION,"Save this circuit to disk");


    /**
     *
     * Save to  predefined file Operation
     *
     */
    saveFile = new AbstractAction("Save",UIImages.save)
    {
      public void actionPerformed(ActionEvent e)
      {

        if (m.getFrom()==null)
        {
         saveFileAs.actionPerformed(e);
         return;
        }
        if (AppletMain.isapplet)
        {
          LMSFileChooser lmsfc = new LMSFileChooser(JOptionPane.getRootFrame(),false);
          return;
        }
        else
      {
        //save to predefined file
        URL u= m.getFrom();
        File f;
        if (u.getProtocol().equals("file"))
        {
          try {
          f = new File(new URI(u.toString()));
          }
          catch (Exception e2) {staticUtils.errorMessage("Malformed filename");e2.printStackTrace();return;}
        }
        else
        {
          staticUtils.errorMessage("Can not save a file loade from a URL"); return;
        }

        f=checkExtension(f,m);
        if (f.exists())
            {
          try { File f2=new File(f.toString()+".bak");
                if (f2.exists()) f2.delete();
                f.renameTo(f2);
                }
          catch (Exception e2) {JOptionPane.showMessageDialog(UIMainFrame.currentinstance,"Error, couldnt replace file");return;}
            }
          UICommand.saveFile(m,f).execute();
      }
     }
    };
    saveFile.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK ));
    saveFile.putValue(AbstractAction.SHORT_DESCRIPTION,"Save this circuit to disk");
    /**
     *
     * New file Operation
     *
     */
    newFile = new AbstractAction("New File",UIImages.newfile)
    {
      public void actionPerformed(ActionEvent e)
      {
        UICommand.newFile(m).execute();
      }
    };
    newFile.putValue(AbstractAction.SHORT_DESCRIPTION,"Create a new blank circuit");

    /**
     *
     * Count components Operation
     *
     */
    countComponents = new AbstractAction("Count Components")
    {
      public void actionPerformed(ActionEvent e)
      {
        JOptionPane.showMessageDialog(UIMainFrame.currentinstance,""+m.getNumberComponents());
      }
    };
    countComponents.putValue(AbstractAction.SHORT_DESCRIPTION,"Count the number of components");


    exit = new AbstractAction("Exit")
   {
     public void actionPerformed(ActionEvent e)
     {
       int i = JOptionPane.showConfirmDialog(UIMainFrame.currentinstance,"Are you sure you want to quit?");
       if (i!=JOptionPane.YES_OPTION) return;
       System.exit(0);
     }
   };
    exit.putValue(AbstractAction.SHORT_DESCRIPTION,"Quit the program");
    /**
     *
     * Delete  Operation
     *
     */
    doDelete = new AbstractAction("Delete",UIImages.delete)
    {
      public void actionPerformed(ActionEvent e)
      {
        {
        Vector components = m.getView().getControl().selectedComponents();
        Vector wires = m.getView().getControl().selectedWires();
        if (components.isEmpty() && wires.isEmpty()) return;
        // check all wires connected to any deleted components are killed too
        Enumeration c = components.elements();
        while (c.hasMoreElements())
        {
          ComponentModel cm = (ComponentModel) c.nextElement();
          Enumeration w1 = cm.getInputWires().elements();
          while (w1.hasMoreElements())
          {
            Object o = w1.nextElement();
            if (!wires.contains(o)) wires.add(o);
          }
          w1 = cm.getOutputWires().elements();
          while (w1.hasMoreElements())
          {
            Object o = w1.nextElement();
            if (!wires.contains(o)) wires.add(o);
          }
        }
        UICommand.deleteComponents(components,wires,m).execute();
        }
      }
    };
    doDelete.putValue(AbstractAction.SHORT_DESCRIPTION,"Delete currently selected wires and components");
    doDelete.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE,0));


    /**
     *
     * Copy Operation
     *
     */
    doCopy =  new AbstractAction("Copy",UIImages.copy)
    {
      public void actionPerformed(ActionEvent e)
      {

        Vector components = m.getView().getControl().selectedComponents();
        Vector wires = m.getView().getControl().selectedWires();
        if (components.isEmpty() && wires.isEmpty()) return;
        UICommand.copy(components,wires,m).execute();
      }
    };
    doCopy.putValue(AbstractAction.SHORT_DESCRIPTION,"Create a copy of current selection");
    doCopy.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK ));


    /**
     *
     * Add from file Operation
     *
     */
    addFromFile = new AbstractAction("File...")
    {
      public void actionPerformed(ActionEvent e)
      {
        JFileChooser j = new JFileChooser(Preferences.getCircuitsPath());
        j.setFileView( staticUtils.CircuitFileView());
        j.setFileFilter(staticUtils.componentFilter);
        int returnval = j.showOpenDialog(UIMainFrame.currentinstance);
        if(returnval == JFileChooser.APPROVE_OPTION)
        {
          File f = j.getSelectedFile();
          if (!f.exists()) return;
          ComponentFactory thisFileFactory = ComponentFactory.fromFile(m,f);
          m.getView().setFloatingSelection(
              UIActions.GreyBoxSelection(f.getName(),m,thisFileFactory));
        }
      }
    };
    addFromFile.putValue(AbstractAction.SHORT_DESCRIPTION,"Create a component from the files on the disk");
  }


  // Creates an abstract action that adds a component from the supplied factory to the circuit
  private  AbstractAction addComponentAction(final String componentname,final ComponentFactory cf,final CircuitModel m,Icon icon)
  {
    AbstractAction a=  new AbstractAction(componentname,icon)
    {
      public void actionPerformed(ActionEvent e)
      {
        m.getView().setFloatingSelection(
            UIActions.GreyBoxSelection(componentname,m,cf));
      }
    };
    a.putValue(AbstractAction.SHORT_DESCRIPTION,"Add an "+componentname+" gate");
    return a;
  }

  static CircuitViewInterface.FloatingSelection GreyBoxSelection(final String label,final CircuitModel target,final ComponentFactory cf)
  {
    return new CircuitViewInterface.FloatingSelection()
    {
      public ComponentModel getComp()
      {
        return cf.getComp();
      }

      public Icon getIcon()
      {
         return new javax.swing.Icon()
        {
          public int getIconHeight()
          {
            return 70;
          }

          public int getIconWidth()
          {
            return 70;
          }

          public void drawLabel(Graphics g,int x,int y)
          {
            if (label==null) return;
            int w = getIconWidth();
            int h = getIconWidth();
            int s = g.getFontMetrics().stringWidth(label);
            if (s>w-20) {Debug.out("label too long"); return;}
            g.setColor(Color.white);
            String label2;
            g.drawString(label,x+(w/2)-(s/2),y+(h/2));
          }

          public void paintIcon(Component c,Graphics g,int x,int y)
          {
            g.setColor(new Color(0,0,0,80));
            g.fillRect(x,y,70,70);
            drawLabel(g,x,y);
          }
        };
      }
    };
  }

  public File checkExtension(File f,CircuitModel m)
    {

    String name = f.toString();
    if ( name.toLowerCase().endsWith(".xml") ) name=name.substring(0,name.length()-4);
    if ( name.toLowerCase().endsWith(".circuit") ) name=name.substring(0,name.length()-8);
    if ( name.toLowerCase().endsWith(".component") ) name=name.substring(0,name.length()-10);
    if (m.hasIO()) name=name+".component";
    else name=name+".circuit";
    name=name+".xml";
    f = new File(name);
    return f;
    }

    public AbstractAction showAbout = new AbstractAction("About")
    {
      public void actionPerformed(ActionEvent e)
      {
       JOptionPane.showMessageDialog(UIMainFrame.currentinstance,"","DLSIM Beta 2.0 - By Matt Leslie",JOptionPane.WARNING_MESSAGE,UIImages.splash);
      }
    };
}


abstract class ListeningAction extends AbstractAction implements UICommand.CommandListener
{
  ListeningAction(String name,Icon icon)
  {
    super(name,icon);
  }

  ListeningAction(String name)
  {
    super(name);
  }
}