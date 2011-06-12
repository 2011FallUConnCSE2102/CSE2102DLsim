/**
 * Title:        DLSim<p>
 * Description:  <p>
 * @version 1.0
 */

package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;

public class ANDComponentModel extends dlsim.DLSim.ComponentModel {

  public ANDComponentModel(CircuitModel c)
  {
        super(2,1,c);
        ((GenericComponentView)this.getView()).setLabel("AND");
  }

  public boolean[] doLogic(boolean[] in)
  {
        return new boolean[] {(in[0] && in[1])};
  }

  public int getSize()
 {
   return 1;
  }
}