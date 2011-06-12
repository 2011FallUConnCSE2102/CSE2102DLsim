package dlsim.DLSim.concrete;
import dlsim.DLSim.ComponentControl;
import dlsim.DLSim.toggleableComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * @version 1.0
 */

public class OnOffComponentControl extends ComponentControl
{
 toggleableComponent mymodel;
  public OnOffComponentControl(toggleableComponent  m)
  {
   super(m);
   mymodel=m;
  }

  public void Clicked(MouseEvent e)
  {
   super.Clicked(e);
   if   (e.getClickCount()==2)
        {
          mymodel.toggle();
        }
  }
}
