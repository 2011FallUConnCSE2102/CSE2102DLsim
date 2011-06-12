package dlsim.DLSim;
import javax.swing.*;
import java.util.Hashtable;
import java.awt.event.*;
import javax.swing.event.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UISimulationControlBox extends JPanel implements ChangeListener
{
  JButton step = new JButton();
  JButton run = new JButton();
  SimulationRunner sim;
  boolean running=false;
  JSlider speed = new JSlider();
  JLabel label = new JLabel();

  public UISimulationControlBox(CircuitModel m)
  {
    sim = new SimulationRunner(m);
    sim.start();
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    step.setText("Step");
    step.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        step_actionPerformed(e);
      }
    });
    run.setText("Run");
    run.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        run_actionPerformed(e);
      }
    });
    label.setText("Simulation Speed:");
    this.add(step, null);
    this.add(run, null);
    this.add(label, null);


  }

  public void stateChanged(ChangeEvent e)
  {
   sim.setSpeed(speed.getValue());
  }

  void step_actionPerformed(ActionEvent e)
  {
    sim.runOneStep();
  }

  void run_actionPerformed(ActionEvent e)
  {
    if (running)
    {
     run.setText("Run");
     running=false;
     sim.stopRunning();
    }
    else
    {
     run.setText("Stop");
     running=true;
     sim.setRunning();
    }

  }
}