package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * @version 1.0
 */

public class OnOffComponentModel extends toggleableComponent
{


  boolean on;

  public int getSize()
  {
    return 1;
  }

  public OnOffComponentModel(CircuitModel c)
  {
  super(0,1,c);
  ((GenericComponentView)this.getView()).setLabel("ON");
  on=true;
  this.setControl(new OnOffComponentControl(this));
  this.inValidate();
  }

  public void toggle()
  {
  if (on) {on=false;((GenericComponentView)this.getView()).setLabel("OFF");}
  else {on=true; ((GenericComponentView)this.getView()).setLabel("ON");}
  this.inValidate();
  }

  public boolean[] doLogic(boolean[] in)
  {
   if (on) return new boolean[] {true};
   else return new boolean[] {false};
  }

  }