package dlsim.DLSim;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import com.sun.java.swing.plaf.motif.*;
import com.sun.java.swing.plaf.windows.*;
import javax.swing.plaf.metal.*;


/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIMainFrame extends JFrame
{
static CircuitModel rootcircuit;
static JToolBar toolBar;
static UIActions actions;

public static Component currentinstance;

  public UIMainFrame()
  {
    super("Logic Simulator");
    this.currentinstance=this;
    UIImages ui = new UIImages();
    ui.getIcons();
    try {
      String lf = Preferences.getValue("LOOKANDFEEL","METAL");
      if (lf.equals("WINDOWS"))
        UIManager.setLookAndFeel(new WindowsLookAndFeel());
      if (lf.equals("METAL"))
        UIManager.setLookAndFeel(new MetalLookAndFeel());
      if (lf.equals("MOTIF"))
        UIManager.setLookAndFeel(new MotifLookAndFeel());
       SwingUtilities.updateComponentTreeUI(this);
    }
    catch (UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }

    this.setIconImage(UIImages.icon);
    currentinstance=this;
    rootcircuit = new CircuitModel();
    rootcircuit.setView(new CircuitView(rootcircuit));
    setSize(800,600);
    actions = new UIActions(rootcircuit);
    JToolBar outerToolBar = new JToolBar();
    JToolBar toolBar = new JToolBar();
    JScrollPane outerScroll = new JScrollPane(toolBar);
    outerScroll.setPreferredSize(new Dimension(800,60));
    toolBar.setBorderPainted(false);
    toolBar.setMargin(new Insets(0,0,0,0));
    toolBar.setFloatable(false);
    toolBar.add(actions.addAnd);
    toolBar.add(actions.addOr);
    toolBar.add(actions.addNot);
    toolBar.add(actions.addPlus);
    if (!AppletMain.isapplet) toolBar.add(new UIComponentComboBox(rootcircuit));
    toolBar.add(actions.addOnOff);
    toolBar.add(actions.addSwitch);
    toolBar.add(actions.addInput);
    toolBar.add(actions.addOutput);
    toolBar.add(actions.addClock);
    toolBar.add(actions.addLatch);
    toolBar.add(actions.addOscilloscope);
    JToolBar buttonBar = new UIToolBar(actions);
    this.setJMenuBar(new UIMenuBar(actions));
    JScrollPane jsp = new JScrollPane((Component)rootcircuit.getView());
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(jsp,BorderLayout.CENTER);
    getContentPane().add(buttonBar,BorderLayout.NORTH);
    getContentPane().add(outerScroll,BorderLayout.SOUTH);
    validate();
    //shutdown on window close
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e)
       {
        int i = JOptionPane.showConfirmDialog(UIMainFrame.currentinstance,"Are you sure you want to quit?");
        if (i!=JOptionPane.YES_OPTION) return;
        else
        System.exit(0);
      }
    });
    setLocation(256,256);
    setVisible(true);

  }



}