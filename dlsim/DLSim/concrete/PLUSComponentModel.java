
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
public class PLUSComponentModel extends ComponentModel{

  public PLUSComponentModel(CircuitModel c)
  {
        super(0,1,c);
        ((GenericComponentView)this.getView()).setLabel("+V");
        validate();
  }


  public boolean[] doLogic(boolean[] in)
  {
        return new boolean[] {true};
  }

  public int getSize()
 {
   return 1;
  }

}