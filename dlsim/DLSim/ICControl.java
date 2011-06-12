package dlsim.DLSim;
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

public class ICControl extends ComponentControl
{
  public ICModel mymodel;


  public ICControl(ICModel  m)
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