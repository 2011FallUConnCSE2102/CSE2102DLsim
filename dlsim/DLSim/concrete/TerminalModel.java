package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public abstract class TerminalModel extends ComponentModel
{
  public TerminalModel(int in,int out,CircuitModel m)
  {super( in, out,m);}

  public JPopupMenu getMenu()
    {
      JPopupMenu myMenu = super.getMenu();
      myMenu.addSeparator();
      myMenu.add(changeName);
      return myMenu;
    }

  final TerminalModel me = this;

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

  public abstract String getName();
  public abstract void setName(String s);

  public abstract int getID();
}