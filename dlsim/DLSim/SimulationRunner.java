package dlsim.DLSim;

/**
 * <p>Title: DLSim</p>
 * @version 1.0
 */

public class SimulationRunner extends Thread {

  private CircuitModel c;
  private static int RUN=10;
  private static int STOP=20;
  private int mode=STOP;
  private long delay = 200;
  public static int totalwires=0;
  public static int totalcomponents=0;
  public static int components_conscidered=0;
  public static int wires_conscidered=0;
  public static int totalcomponentscons=0;
  public static int totalwirescons=0;
  public static int totaltime=0;
  public static int totalsteps=0;

  public SimulationRunner(CircuitModel c)
  {
    this.c=c;
    super.setPriority(Thread.MIN_PRIORITY);
  }

  public long runOneStep()
  {
   long starttime=System.currentTimeMillis();
   components_conscidered=0;
   wires_conscidered=0;
   totalwires=0;
   totalcomponents=0;
   totalsteps++;
     c.doSimulationStep();
   long runtime=starttime-System.currentTimeMillis();
    try{
     Debug.performance("Performance Information");
      Debug.performance("------------------------------------------");
      Debug.performance("Components validated :"+this.components_conscidered+" of "+this.totalcomponents+" conscidered = "+
      (100*this.components_conscidered/this.totalcomponents)+"%");
      Debug.performance("Wires Sending data :"+this.wires_conscidered+" of "+this.totalwires+" conscidered = "+
      (100*this.wires_conscidered/this.totalwires)+"%");
      Debug.performance("Time taked = "+(-runtime));
      totalcomponentscons+=this.components_conscidered;
      totalwirescons+=this.wires_conscidered;
      totaltime-=runtime;
      if (totaltime!=0)
      {
         Debug.performance("Overall Average per component = "+(1.0*totaltime/totalcomponentscons));
         Debug.performance("Overall Average per wire = "+(1.0*totaltime/totalwirescons));
         Debug.performance("Overall Average per step = "+(1.0*totaltime/totalsteps));
      }
      Debug.performance("------------------------------------------");
    }
    catch (ArithmeticException e) {}
   return (System.currentTimeMillis()-starttime);
  }

  public void run()
  {
   int runtime=0;
   while (true)
   {
    if (mode==RUN)
     {
      if (!c.isValid())
          runOneStep();

     }
     try {this.sleep(java.lang.Math.max(delay,1));}
     catch (Exception e) {e.printStackTrace();}
   }
  }

  public void setRunning()
  {
   mode=RUN;
  }
  public void stopRunning()
  {
   mode=STOP;
  }

  public void setSpeed(long delay)
  {
  this.delay=delay;
  }
}