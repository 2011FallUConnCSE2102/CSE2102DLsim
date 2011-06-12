package dlsim.DLSim.concrete;

/*import dlsim.DLSim.Util.LogicOperation;*/
import dlsim.DLSim.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class SwitchComponentModel extends toggleableComponent
{

  boolean on=true;

  public SwitchComponentModel(CircuitModel c)
  {
  super(1,1,c);
  ((GenericComponentView)this.getView()).setLabel("===");
  on=true;
  this.setControl(new OnOffComponentControl(this));
  this.inValidate();
  }

  public int getSize()
 {
   return 1;
  }
  public void toggle()
  {
  if (on) {on=false;((GenericComponentView)this.getView()).setLabel("=/=");}
  else {on=true; ((GenericComponentView)this.getView()).setLabel("===");}
  this.inValidate();
  }

  public boolean[] doLogic(boolean[] in)
  {
    if (on) return in;
    else return new boolean[] {false};
  }
}