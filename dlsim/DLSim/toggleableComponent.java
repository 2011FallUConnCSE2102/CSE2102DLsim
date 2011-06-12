package dlsim.DLSim;

/*import dlsim.DLSim.Util.LogicOperation;*/
import javax.swing.JPopupMenu;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public abstract class toggleableComponent extends ComponentModel
{
  public toggleableComponent(int in, int out,CircuitModel c)
  {
   super(in,out,c);
  }
  public abstract void toggle();

  public JPopupMenu getMenu()
  {
    JPopupMenu j = super.getMenu();
    final toggleableComponent c = this;
    AbstractAction toggle = new AbstractAction("Toggle")
    {
      public void actionPerformed(ActionEvent e)
      {
        c.toggle();
      }
    };
    j.addSeparator();
    j.add(toggle);
    return j;
  }
}