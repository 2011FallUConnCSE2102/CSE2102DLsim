package dlsim.DLSim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.File;
import dlsim.DLSim.Util.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIInternalCircuitFrame extends JFrame
{

  public UIInternalCircuitFrame(final ICModel m)
  {
    //give the circuit a proper view
    CircuitView cv = new CircuitView(m.getInternal());
    if ( m.getInternal().getView() instanceof EmptyView )
        { m.getInternal().setView(cv); }
    // add everything to it
    Enumeration enum1 = m.getInternal().getComponents().elements();
    while (enum1.hasMoreElements())
    {
      ComponentModel cm = (ComponentModel)enum1.nextElement();
      cv.add(cm.getView());
    }
    enum1 = m.getInternal().getWires().elements();
    while (enum1.hasMoreElements())
    {
      WireModel wm = (WireModel)enum1.nextElement();
      cv.add(wm.getView());
    }
    //set control
    InternalCircuitControl icc = new InternalCircuitControl(cv,m.getInternal(),m);
    cv.setControl(icc);
    //set layout
    this.getContentPane().setLayout(new BorderLayout());
    //set name
    this.setTitle("Internal Circuit - "+m.getName());
    //add circuit inside scrollpane
    JScrollPane p = new JScrollPane((Component)m.getInternal().getView());
    this.getContentPane().add(p,BorderLayout.CENTER);
    //set up tooblar
    JToolBar myInternalBar = new JToolBar();
    if (m.getInternal().getFrom()!=null)
    {
      myInternalBar.add(new JLabel("This subcircuit is described in a separate file. Click here to edit it"));
      myInternalBar.add(editInternal(m.getInternal()));
    }
    else
    {
      myInternalBar.add(new JLabel("This subcircuit can not be edited."));
    }
    this.getContentPane().add(myInternalBar,BorderLayout.NORTH);
    //set this to be the right size
    this.pack();
    Dimension d = m.getInternal().getView().getSize();
    if (d.width<=800 && d.height <=600) this.setSize(d.width+50,d.height+60);
    else this.setSize(new Dimension(800,600));
    //setup close operation
    this.addWindowListener(
        new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        m.getInternal().setView(new EmptyView());
        m.Wopen=false;
      }
    }
    );
    //make this visible
     setVisible(true);
  }

  public static AbstractAction editInternal(final CircuitModel internalModel)
  {
    return new AbstractAction("Edit This Subcircuit")
    {
      public void actionPerformed(ActionEvent e)
      {
          // save the existing circuit
        URL u = internalModel.getFrom();
        File f = staticUtils.FileFromURL(u);
        if (f==null) {staticUtils.errorMessage("Sorry, This subcircuit is read only."); return;}
        if (UIMainFrame.rootcircuit.getFrom()==null)
       {
         JOptionPane.showMessageDialog(UIMainFrame.currentinstance,"Please save your current root circuit first!");
          UIActions.currentinstance.saveFile.actionPerformed(e);
        }
        // if this is still null, user cancelled save, so stop whole operation
        if (UIMainFrame.rootcircuit.getFrom()==null) return;
        if (!f.exists())
        {
          File g = new File(f.toString()+".component.xml");
          if (g.exists())
          {
            f=g;
          }
          else
          {
            staticUtils.errorMessage("File not found "+f);
            return;
          }
        }
        UICommand.loadFile(UIMainFrame.rootcircuit,f).execute();
      }
    };
  }

}