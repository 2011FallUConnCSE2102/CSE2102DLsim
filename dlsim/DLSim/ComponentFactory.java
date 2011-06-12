package dlsim.DLSim;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.Util.*;
import java.net.*;
import java.io.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public abstract class ComponentFactory
{


  public abstract ComponentModel getComp();

  public static ComponentFactory andFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new ANDComponentModel(m);}
      };
   }

   public static ComponentFactory oscilloscopeFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new OscilloscopeComponentModel(m);}
      };
   }

   public static ComponentFactory orFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new ORComponentModel(m);}
      };
   }

   public static ComponentFactory onOffFactory(final CircuitModel m)
  { return new ComponentFactory ()
     {
       public ComponentModel getComp() {return new OnOffComponentModel(m);}
     };
   }

   public static ComponentFactory notFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new NOTComponentModel(m);}
      };
   }

   public static ComponentFactory plusFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new PLUSComponentModel(m);}
      };
   }

   public static ComponentFactory clockFactory(final CircuitModel m)
  { return new ComponentFactory ()
     {
       public ComponentModel getComp() {return new ClockComponentModel(m);}
     };
   }

   public static ComponentFactory latchFactory(final CircuitModel m)
{ return new ComponentFactory ()
   {
     public ComponentModel getComp() {return new LatchComponentModel(m);}
   };
   }

   public static ComponentFactory switchFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new SwitchComponentModel(m);}
      };
   }

   public static ComponentFactory inFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new InputTerminalModel(m);}
      };
   }

   public static ComponentFactory outFactory(final CircuitModel m)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {return new OutputTerminalModel(m);}
      };
   }

   public static ComponentFactory fromFile(final CircuitModel m,final File f)
   {
     try{
       URL u = f.toURL();
       return fromURL(m,u);
     }
     catch (Exception e)
     {
       e.printStackTrace();
       staticUtils.errorMessage(e.getMessage());
       return null;
     }
   }

   public static ComponentFactory fromLMS(final CircuitModel m,final String LMSKey,final String name)
   {
     return new ComponentFactory ()
     {

       public ComponentModel getComp()
       {
         try
         {
           return ICModel.setupIC(LMSKey,name,m);
         }
         catch (IOException e)
         {
           staticUtils.errorMessage("Couldnt load file for component");
           return null;
         }
       }

     };
   }

   public static ComponentFactory fromURL(final CircuitModel m,final URL u)
   { return new ComponentFactory ()
      {
        public ComponentModel getComp() {
          if (m.hasParent(u))
                 {staticUtils.errorMessage("This would create recursion");return null;}
            try {
                 return ICModel.setupIC(u,m);
                  }
            catch (IOException e) {staticUtils.errorMessage("Couldnt load file for component");
                                    return null;}

        }
      };
   }
}