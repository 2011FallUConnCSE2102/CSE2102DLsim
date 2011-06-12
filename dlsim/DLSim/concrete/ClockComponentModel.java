package dlsim.DLSim.concrete;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class ClockComponentModel extends dlsim.DLSim.ComponentModel {

  public int clk=0;
  public int clkp=25;
  boolean value = true;

  public ClockComponentModel(CircuitModel c)
  {
        super(0,1,c);
        ((GenericComponentView)this.getView()).setLabel("Clock 25");
        this.setControl(new ClockControl(this));
  }

  public void setClockPeriod(int p)
  {
  clkp=p;
  ((GenericComponentView)this.getView()).setLabel("Clock "+clkp);
  this.getView().repaint();
  }

  public boolean[] doLogic(boolean[] in)
  {
        this.inValidate();
        if (clk>=clkp)
        {
          clk=0;
          value=!value;
          return new boolean[] {value};
        }
        clk++;
        return new boolean[] {value};
  }

  public int getSize()
 {
   return 1;
  }

  public JPopupMenu getMenu()
  {
    JPopupMenu myMenu = super.getMenu();
    myMenu.addSeparator();
    myMenu.add(changeClock);
    return myMenu;
  }

final ClockComponentModel me = this;

public AbstractAction changeClock = new AbstractAction("Change Period")
{
  public void actionPerformed(ActionEvent e)
  {
    Object ret = JOptionPane.showInputDialog(UIMainFrame.currentinstance,"Enter new Clockspeed?",
        "Adjust clockspeed",JOptionPane.QUESTION_MESSAGE,
        (Icon)null,(Object[])null,""+me.clkp);
    if (ret!=null)
    {
      try {
        int i = Integer.parseInt((String)ret);
        if (i!=0) UICommand.changeClockSpeed(me,i).execute();
      }
      catch (Exception e2) {staticUtils.errorMessage("Please enter a correctly formed value");}
    }
  }
};
}

class ClockControl extends dlsim.DLSim.ComponentControl
{
  ClockComponentModel m;
  ClockControl(ClockComponentModel m)
  {
    super(m);
    this.m=m;
  }

  public void Clicked(MouseEvent e)
  {
   super.Clicked(e);
     if (e.getClickCount()==2)
     {
       Object ret = JOptionPane.showInputDialog(UIMainFrame.currentinstance,"Enter new Clockspeed?",
         "Rename",JOptionPane.QUESTION_MESSAGE,
        (Icon)null,(Object[])null,""+m.clkp);
       if (ret==null) return;
       try {
         int i = Integer.parseInt((String)ret);
         if (i!=0) UICommand.changeClockSpeed(m,i).execute();
       }
       catch (Exception e2) {staticUtils.errorMessage("Please enter a correctly formed value");}

   }

  }

}
