package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import dlsim.DLSim.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class TerminalControl extends ComponentControl
{
  TerminalModel m;

  public TerminalControl(TerminalModel m)
  {
    super(m);
    this.m=m;
  }

  public void Clicked(MouseEvent e)
  {
    super.Clicked(e);
    if (e.getClickCount()==2) {
      Object ret = JOptionPane.showInputDialog(UIMainFrame.currentinstance,"Enter new name?",
            "Rename",JOptionPane.QUESTION_MESSAGE,
          (Icon)null,(Object[])null,m.getName());
    String name = (String) ret;
    if (name==null) return;
    if (!name.equals(""))
       UICommand.changeTerminalName(m,name).execute();
    }

  }
}