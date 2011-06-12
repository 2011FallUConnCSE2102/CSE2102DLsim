
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim;
import java.sql.Time;
public class Debug {

  public Debug() {}
  public static boolean applet=true;
  public static boolean graphics=false;
  public static boolean debug=true;
  public static boolean performance=false;

  //methods declared final to allow runtime code inlining

  public static final void out(String message)
         {
          if (!debug) return;
          Time d = new Time(System.currentTimeMillis());
          System.out.println(d+" "+message);
          System.out.flush();
         }

  public static final void graphics(String message)
         {
           if (!graphics) return;
           Time d = new Time(System.currentTimeMillis());
           System.out.println(d+" "+message);
           System.out.flush();
         }

   public static final void performance(String message)
         {
          if (!performance) return;
          Time d = new Time(System.currentTimeMillis());
          System.out.println(d+" "+message);
         }

}