
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;

public class NOTComponentModel extends ComponentModel{

  public NOTComponentModel(CircuitModel c) {
  super(1,1,c);
        ((GenericComponentView)this.getView()).setLabel("NOT");
        this.inValidate();
  }

   public boolean[] doLogic(boolean[] in)
  {
        return new boolean[] {!in[0]}; //return (NOT A)
  }

    public void addOutputWire(WireModel w)
    {
               super.addOutputWire(w);
               w.send();
    }

    public int getSize()
 {
   return 1;
  }

}