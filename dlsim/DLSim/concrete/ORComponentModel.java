
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

public class ORComponentModel  extends ComponentModel {

  public ORComponentModel(CircuitModel c) {
   super(2,1,c);
        ((GenericComponentView)this.getView()).setLabel("OR");
  }

  public boolean[] doLogic(boolean[] in)
  {
        return new boolean[] {(in[0] || in[1])};
  }

  public int getSize()
 {
   return 1;
  }

}