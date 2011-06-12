package dlsim.DLSim.concrete;

import dlsim.DLSim.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class OscilloscopeComponentModel extends ComponentModel
{
  public UIOscilloscope myOscilloscope;
  private static UIOscilloscopeFrame scopeFrame = new UIOscilloscopeFrame();
  public boolean open=false;
  final OscilloscopeComponentModel me =this;

  public OscilloscopeComponentModel(CircuitModel m)
  {
  super(1,0,m);
  myOscilloscope= new UIOscilloscope(this);
  this.setControl(new Ocontrol(this));
  ((GenericComponentView)this.getView()).setLabel("'SCOPE");
  this.openWindow();
  }

  public JPopupMenu getMenu()
  {
    JPopupMenu mm = super.getMenu();
    mm.addSeparator();
    mm.add(openAction);
    mm.add(changeName);
    return mm;
  }

  public void removed()
  {
   closeWindow();
  }

  public void added()
 {
    if (me!=null)
  openWindow();
  }


  public void openWindow()
  {
    if(me.open) {scopeFrame.setVisible(true);return;}
    me.open=true;
    scopeFrame.addScope(this);
  }

  public void closeWindow()
  {
    if (me.open)
    {
      scopeFrame.removeScope(this);
      open=false;
    }
  }

  public AbstractAction openAction = new AbstractAction("Open Trace")
  {
   public void actionPerformed(ActionEvent e)
   {
     me.openWindow();
   }
  };

  public AbstractAction changeName = new AbstractAction("Rename")
{
  public void actionPerformed(ActionEvent e)
  {
    Object ret = JOptionPane.showInputDialog(UIMainFrame.currentinstance,"Enter new name?",
        "Rename",JOptionPane.QUESTION_MESSAGE,
        (Icon)null,(Object[])null,me.getName());
    if (ret!=null)
    {
      UICommand.changeTerminalName(me,(String)ret).execute();
    }
  }
  };

  public int getSize()
  {
   return 1;
  }

  public boolean[] doLogic(boolean[] in)
  {
    myOscilloscope.putValue(in[0]);
    this.inValidate();
    return null;
  }

  String name="'SCOPE";

  public String getName()
  {
    return name;
  }

  public void setName(String n)
  {
    if (name!=null)
    {
      name=n;
      myOscilloscope.setName(n);
     ((GenericComponentView)this.getView()).setLabel(n);
     this.getView().repaint();
    }
  }


}

class Ocontrol extends ComponentControl
{
  public final OscilloscopeComponentModel mymodel;
  public Ocontrol(OscilloscopeComponentModel  m)
  {
   super(m);
   mymodel=m;
  }

  public void Clicked(MouseEvent e)
  {
   super.Clicked(e);
   if   (e.getClickCount()==2)
        {
           mymodel.openWindow();
        }
  }
}
