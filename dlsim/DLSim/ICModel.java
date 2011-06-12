package dlsim.DLSim;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.Util.*;
import java.io.*;
import java.net.*;
/* Integreated Circuit model */

public class ICModel extends ComponentModel implements labeledComponent
{

    CircuitModel ic;
    HashMap inputToTerminal = new HashMap();
    HashMap outputToTerminal = new HashMap();
    int inputs;
    int outputs;
    boolean Wopen=false;
    JFrame myFrame;



    private ICModel(CircuitModel IC,CircuitModel target,int inputs, int outputs, HashMap inputToTerminal,HashMap outputToTerminal)
    {
        super(inputs,outputs,target);
        this.ic=IC;
        this.inputs=inputs;
        this.outputs=outputs;
        this.inputToTerminal=inputToTerminal;
        this.outputToTerminal=outputToTerminal;
        this.inValidate();
    }

    /**
     * Customises the context sensitive menu to allow the circuit to be opened
     * @return the customised menu
     */
    public JPopupMenu getMenu()
    {
      JPopupMenu myMenu = super.getMenu();
      myMenu.addSeparator();
      myMenu.add(open);
      return myMenu;
    }


    /**
     *open IC Window
     */
    public void openWindow()
    {
    if(Wopen) return;
    Wopen=true;
    myFrame = new UIInternalCircuitFrame(this);
    }

    /**
     * Close IC Window
     */
    public void closeWindow()
    {
      if (Wopen)
      {
        this.getInternal().setView(new EmptyView());
        myFrame.dispose();
        myFrame=null;
      }
      Wopen=false;
    }

    final ICModel me = this;

    AbstractAction open = new AbstractAction("View Internal")
    {
      public void actionPerformed(ActionEvent e)
      {
        me.openWindow();
      }
    };

    /**
    * Maps given input to internal terminal models, does one timestep, and returns the state of output terminal models
    * @param inputs value to be mapped to internal terminal models
    * @return state of output terminal models
    */
    public boolean[] doLogic(boolean[] inputs)
    {
      boolean[] outputs = new boolean[this.outputs];
      for (int i=0;i < inputs.length;i++)
      {
        InputTerminalModel input = (InputTerminalModel) inputToTerminal.get(new Integer(i));
        input.setValue(inputs[i]);
      }
      ic.doSimulationStep();
      for (int i=0;i < outputs.length;i++)
      {
        OutputTerminalModel output = (OutputTerminalModel) outputToTerminal.get(new Integer(i));
        outputs[i] = output.getValue();
      }
      if (!ic.isValid()) this.inValidate();
      return outputs;
  }

  /**
   * The size of this subcircuit
   * @return number of components in subcircuit
   */
  public int getSize()
 {
   return ic.getNumberComponents();
  }

  /**
   * Used for labeling
   * @param i the terminal number - no bounds checking
   * @return the name of this terminal number
   */
  public String getOutputName(int i)
  {
    return ((TerminalModel)outputToTerminal.get(new Integer(i))).getName();
  }


  /**
   * Used for labeling
   * @param i the terminal number - no bounds checking
   * @return the name of this terminal number
   */
  public String getInputName(int i)
  {
    return ((TerminalModel)inputToTerminal.get(new Integer(i))).getName();
  }

  /**
   * Sets up an ICModel - use this to construct ICModels
   * @param internalURL A file representing a component - This will be the contents of this IC
   * @param target The circuit this component will be added to
   * @return the ICModel Component
   * @throws IOException thrown if the file cannot be opened
   */
  public static ICModel setupIC(URL internalURL,CircuitModel target) throws IOException
  {
    CircuitModel cm = new CircuitMemento(internalURL).createModel();
    // Since URLs are statically linked, there can be no recursion
    if (cm.hasChild(target.getFrom()))
      {
      staticUtils.errorMessage("Cant do this, it would create an infinite recursion!");
      return null;
      }
    String name = staticUtils.getLastNameFromURL(internalURL);
    if (name.endsWith(".component.xml"));
    name = name.substring(0,name.length()-14);
    ICModel m = setupIC(cm,target,name);
    //We statically link web urls - when saved, this circuit will
    //contain a description of the internal circuit rather than a web link to it
    if (internalURL.getProtocol().equals("file"))
      m.getInternal().setFrom(internalURL);
    return m;
  }

  /**
  * Sets up an ICModel - use this to construct ICModels
  * @param LMSLocation The fully qualified location in the LMS of the circuit to be used internally
  * @param target The circuit this component will be added to
  * @return the ICModel Component
  * @throws IOException thrown if the file cannot be opened
  */
 public static ICModel setupIC(String LMSLocation,String name, CircuitModel target) throws IOException
 {
   // do nothing when the LMS isnt active
   if ( (!AppletMain.isapplet) ) return null;
   CircuitModel cm =
   AppletMain.loadMementoFromLMSKEY(LMSLocation).createModel();
   // Since LMS locations are statically linked, no recursion possible ...
   /*if (cm.hasChild(target.getFrom()))
     {
     staticUtils.errorMessage("Cant do this, it would create an infinite recursion!");
     return null;
     }*/
   ICModel m = setupIC(cm,target,name);
   //We statically link web urls - when saved, this circuit will
   //contain a description of the internal circuit rather than a web link to it
   m.getInternal().setFrom(null);
   return m;
  }




  /**
   *  This is used when setting up an IC from a circuit that isnt from a file
   * @param IC A circuitModel representing the internal circuit
   * @param target The circuitModel this ICModel is to be added to
   * @param name The name to be given to this ICModel
   * @return The ICModel component
   */
    public static ICModel setupIC(CircuitModel  IC,CircuitModel target,String name)
    {
     IC.setView(new EmptyView());

     int maxin=-1;
     int maxout=-1;
     HashMap inputToTerminal = new HashMap();
     HashMap outputToTerminal = new HashMap();
     Enumeration e = IC.getComponents().elements();
     while (e.hasMoreElements())
           {
            ComponentModel m = (ComponentModel) e.nextElement();
            if (m instanceof InputTerminalModel)
               {
                int key  = ((InputTerminalModel)m).getID();
                if (key>maxin) maxin=key;
                inputToTerminal.put(new Integer(key),m);
               }
            if (m instanceof OutputTerminalModel)
               {
                int key  = ((OutputTerminalModel)m).getID();
                if (key>maxout) maxout=key;
                outputToTerminal.put(new Integer(key),m);
                }
           }
     ICModel m = new ICModel(IC,target,maxin+1,maxout+1,inputToTerminal,outputToTerminal);
     m.setControl(new ICControl(m));
     ((GenericComponentView)m.getView()).setLabel(name);
     IC.doSimulationStep();
     m.inValidate();
     IC.setIC(m);
     return m;
    }

    /**
     * return name
     */
    public String getName()
    {
    return ((GenericComponentView)getView()).getLabel();
    }


    /**
     * valid only if internal circuit is valid
     */
    public boolean isValid()
    {
     if (ic==null) return false;
     return (ic.isValid() && super.isValid());
    }

    /**
     *  @return internal circuit
     */
    public CircuitModel getInternal()
    {
     return ic;
    }

    /**
     * Closes this window and all associated windows
     */
    public void removed()
    {
      // make sure all subcomponents know they have been removed
      Operation remove = new Operation()
      {
        public Object op(Object in)
        {
          // nasty hack - cant call removed on everything cos it does odd things if we undo
          // really need close windows thing, or something= so dont need icky hardcoded distinction.
          if ((in instanceof ICModel || in instanceof OscilloscopeComponentModel ))
            ((ComponentModel)in).removed();
          return null;
        }
      };
      remove.applyOp(this.getInternal().getComponents());
      this.closeWindow();
    }
}

