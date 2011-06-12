package dlsim.DLSim;

import java.awt.*;
import javax.swing.*;
import dlsim.DLSim.Util.*;
import java.util.*;
import java.awt.event.*;
import dlsim.DLSim.concrete.OscilloscopeComponentModel;

/**
 * <p>Title: DLSim</p>
 * <p>Description: A window to keep circuit views in</p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIOscilloscopeFrame extends JFrame implements ActionListener
{

  JScrollPane jsp;
  Vector scopes = new Vector();
  JPanel scopepanel;
  JPanel buttonpanel;
  boolean buttonsadded=false;
  JButton resize;
  JButton clear;
  JButton pause;
  /** Creates an empty frame. Won't be visible untill you add some scopes */
  public UIOscilloscopeFrame()
  {
    //set layout
    this.getContentPane().setLayout(new BorderLayout());
    //set name
    this.setTitle("Oscilloscope Window");

    // create panels

         // scope
        scopepanel = new JPanel();
        scopepanel.setLayout(new GridLayout(1,1,0,1));
        // scrollpane
        jsp = new JScrollPane(scopepanel);
        // buttons
        buttonpanel = new JPanel();
        resize = new JButton("Resize All");
        clear = new JButton("Clear All");
        pause = new JButton("Pause All");
        pause.addActionListener(this);
        clear.addActionListener(this);
        resize.addActionListener(this);
        buttonpanel.add(pause);
        buttonpanel.add(clear);
        buttonpanel.add(resize);

    // add panels
    this.getContentPane().add(jsp,BorderLayout.CENTER);
    // set up window closed operation
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    this.setSize(new Dimension(800,80));
  }

  /** Add this scope to the frame */
  public void addScope(OscilloscopeComponentModel ocm)
  {
    // add to vector
    scopes.add(ocm);
    // set number of rows correctly
    ((GridLayout)scopepanel.getLayout()).setRows(scopes.size());
    // add to panel
    scopepanel.add(ocm.myOscilloscope);
    // check for buttons
    doButtons();
    pack();
    setVisible(true);
  }

  /** Remove this scope from the frame */
  public void removeScope(OscilloscopeComponentModel ocm)
  {
    // remove from vector
    scopes.remove(ocm);
    // remove from screen
    scopepanel.remove(ocm.myOscilloscope);
    // set invisible if theres nothing here
    if (scopes.size()==0) {setVisible(false);return;}
    // otherwise set number of rows correctly
    ((GridLayout)scopepanel.getLayout()).setRows(scopes.size());
    // check for buttons
    doButtons();
    pack();
  }

  boolean pausedall=false;
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource()==pause)
    {

      if (!pausedall)
      {
          pause.setText("Unpause All");
          pauseop.mapOp(scopes);
          pausedall=true;
      }
      else
      {
         pause.setText("Pause All");
         unpauseop.applyOp(scopes);
         pausedall=false;
      }
    }
    if (e.getSource()==resize)
    {
      int currentsteps = ((OscilloscopeComponentModel)scopes.firstElement()).myOscilloscope.getNumberPoints();
      int maxsteps = ((OscilloscopeComponentModel)scopes.firstElement()).myOscilloscope.getMaxNumberTicks();
      Object ret = JOptionPane.showInputDialog(this,"How many steps would you like to be visible? (2-"+maxsteps+")",
          "Change size",JOptionPane.QUESTION_MESSAGE,
          (Icon)null,(Object[])null,""+currentsteps);
      if (ret!=null)
      {
        int steps = Integer.parseInt((String)ret);
        if  ((steps>maxsteps) || (steps<2)  )
        {
          JOptionPane.showMessageDialog(this,"Steps must be between 2 and "+maxsteps);
          return;
        }
        sizeop(steps+1).applyOp(scopes);
      }
    }

    if (e.getSource()==clear)
    {
      clearop.applyOp(scopes);
    }

  }

  Operation clearop = new Operation()
  {
    public Object op(Object in)
    {
      UIOscilloscope ui = ((OscilloscopeComponentModel)in).myOscilloscope;
      ui.clear();
      return null;
    }
  };


  Operation sizeop(final int size)
  {
  return  new Operation()
  {
    public Object op(Object in)
    {
      UIOscilloscope ui = ((OscilloscopeComponentModel)in).myOscilloscope;
      ui.setNumberPoints(size);
      return null;
    }
  };
  }

  Operation pauseop = new Operation()
  {
    public Object op(Object in)
    {
      UIOscilloscope ui = ((OscilloscopeComponentModel)in).myOscilloscope;
      if (!ui.ispaused)
      ui.pause();
      return null;
    }
  };

  Operation unpauseop = new Operation()
 {
   public Object op(Object in)
   {
     UIOscilloscope ui = ((OscilloscopeComponentModel)in).myOscilloscope;
     if (ui.ispaused)
     ui.pause();
     return null;
   }
  };

  // take care of whether the button panel should be there
  private void doButtons()
  {
    if (scopes.size()>1) addButtons();
    else hideButtons();
  }

  private void hideButtons()
  {
    if (buttonsadded)
     {
      this.getContentPane().remove(buttonpanel);
      buttonsadded=false;
     }
  }

  private void addButtons()
  {
    if (!buttonsadded)
    {
     this.getContentPane().add(buttonpanel,BorderLayout.SOUTH);
     buttonsadded=true;
    }
  }
}