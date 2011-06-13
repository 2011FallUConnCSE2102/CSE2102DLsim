package dlsim.DLSim;

import java.applet.Applet;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;
import dlsim.DLSim.Util.staticUtils;
import java.awt.event.ActionEvent;
import netscape.javascript.JSObject;


/**
 * <p>Title: DLSim.AppletMain</p>
 * <p>Description: The main class for running DLSim as an Applet </p>
 * <pre>
 * DLSIM APPLET PARAMETER INFO
 * LMSENABLED - include this tag if you want DLSim to use an LMS to save circuits, etc.
 * AUTOLOAD - set this to the name of a circuit in the circuitsource directory if you
 * want DLSim to load this automatically
 * AUTORUN - set this if you want the simulation to start without the user having to click play
 * DROPIN0 - The name of a component in the circuit directory you want the user to be able to
 * drop into his circuits - it will appear in the combo box
 * DROPINX - The name of the Xth other dropin (i.e DROPIN0, DROPIN1, DROPIN2...)
 *
 * example
 *
 * &lt applet code="DLSim.AppletMain" align="middle" width="100%" height="600" archive="dlsim.jar"
 * MAYSCRIPT>
 *
 *                &lt param name="CIRCUITSOURCE" value="circuits/"> &lt /param>
 *                &lt param name="AUTOLOAD" value="ANDORNOT.circuit.xml"> &lt /param>
 *                &lt param name="DROPIN0" value="NAND.component.xml">&lt /param>
 *                &lt param name="AUTORUN" >&lt /param>
 *                &lt param name="LMSENABLED">&lt /param>
 *  &lt /applet>
 * </pre>
 * @author Matthew Leslie
 * @version 1.0
 */

public class AppletMain extends JApplet
{
/** True if the program is being run as an applet*/
public static boolean isapplet=false;
/** True is the applet is being run as part of an LMS*/
public static boolean LMSEnabled=false;

/** The current root circuit. If this is being run as an applet, this is the
 * same as UIMainFrame.rootcircuit
 */
public static CircuitModel rootcircuit;
private static JToolBar toolBar;
private static Applet currentinstance;
private static UIComponentComboBox uicc;
/** The current user actions. If this is being run as an applet, this is the
 * same as UIMainFrame.actions
 */
static UIActions actions;

private static JSObject jso;

  public AppletMain()
  {
    super();
    if (!Debug.applet)
   {
     Debug.graphics=false;
     Debug.performance=false;
     Debug.debug=false;
    }
    isapplet=true;
    currentinstance=this;
    setUpInterface();
  }

  /** Short string describing this applet */
  public String getAppletInfo() {
    return "DLSim by Matt Leslie";
  }

  private void parseParameters()
  {
    // find out if there is a real LMS, or if we will fake one.
    if (this.getParameter("LMSENABLED")!=null)
    {
      LMSEnabled=true;
      readHashMap();
    }
    else
    {
      JOptionPane.showMessageDialog(this," The applet can not find anywhere to save files\n"+
                                         " you may save files into memory, be aware these\n"+
                                         " will be lost when you close the Applet!\n");

    }
    try {
		LMSPut("DLSim.core.savedcircuits.names[0]","Empty Circuit");
	} catch (Exception e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
    try {
		LMSPut("DLSim.core.savedcircuits.mementos[0]",
		       "<CIRCUITMEMENTO><COMPONENTS></COMPONENTS><WIRES></WIRES></CIRCUITMEMENTO>");
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

    // find the source directory for circuits
    URL circuitsource=this.getDocumentBase();

    // parse circuit source directory

    try
    {
      System.out.println("Document base = "+circuitsource);
      String source = circuitsource.toString();
      int index = source.lastIndexOf("/");
      String search = source.substring(0,index)+"/";
      circuitsource = new URL(search);
      String sourcedir = this.getParameter("CIRCUITSOURCE");
      if (sourcedir!=null)
      {
        circuitsource = new URL(circuitsource,sourcedir);
      }
      System.out.println("Search for circuits in "+circuitsource);
      Preferences.setDocumentsURL(circuitsource);
    }
    catch (MalformedURLException e)
    {
      System.err.println("CIRCUITSOURCE could not be parsed");
    }

    Debug.out("circuitsource = "+circuitsource);



    // parse dropins
    int ndropins=0;
    Vector dropins = new Vector();
    String dropin=this.getParameter("DROPIN0");
    if (circuitsource!=null)
    {
      while(dropin!=null)
      {
        try
        {
          URL u= new URL(circuitsource,dropin);
          Debug.out("Trying dropin "+ndropins+" := "+u);
          dropins.add(u);
        }
        catch (MalformedURLException e)
        {
          System.err.println("Dropin circuit "+(ndropins-1)+"could not be parsed");
          continue;
        }
        ndropins++;
        dropin=this.getParameter("DROPIN"+ndropins);
      }
    }
    Debug.out("Number of dropins from parameters = "+ndropins);

    Vector lmsDropins = new Vector();

    //parse and add LMS circuits
    String[] circuits = this.getLMSCircuitNames();
    for (int i=0;i<circuits.length;i++)
    {
      CircuitMemento cm = loadMementoFromLMS(circuits[i]);
      if (cm.hasIO()) lmsDropins.add(new Integer(i));
    }

    //set the doc dir
    if (circuitsource!=null)
    {
      Preferences.setDocumentsURL(circuitsource);
    }


    // add the dropins
    if (!dropins.isEmpty() || !lmsDropins.isEmpty())
    {
      uicc = new UIComponentComboBox(rootcircuit);
      Enumeration dropinsEnum = dropins.elements();
      while (dropinsEnum.hasMoreElements())
      {
        uicc.add((URL)dropinsEnum.nextElement());
      }
      Enumeration lmsEnum = lmsDropins.elements();
      while (lmsEnum.hasMoreElements())
      {
        Integer i = (Integer) lmsEnum.nextElement();
        uicc.add(circuits[i.intValue()],"DLSim.core.savedcircuits.mementos["+i+"]");
      }
      toolBar.add(uicc);
    }

    // parse auto load  circuit
    String autoLoadString = this.getParameter("AUTOLOAD");
    URL autoload=null;
    if (autoLoadString!=null)
    {
      try
      {
        autoload = new URL(circuitsource,autoLoadString);
      }
      catch (MalformedURLException e)
      {
        System.err.println("Autoload could not be parsed");
      }
    }

    Debug.out("Autoload = "+autoload);
    //load the initial circuit
    if (autoload!=null)
    {
      UICommand.loadFile(rootcircuit,autoload).execute();
    }


    if (this.getParameter("AUTORUN")!=null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          UIActions.currentinstance.startSimulation.actionPerformed(new ActionEvent(this,0,"RUN"));
        }
      });
    }
  }

  private void setUpInterface()
  {
    UIImages ui = new UIImages();
    ui.getIcons();
    // hack - this is where things look for current root container
    UIMainFrame.currentinstance=this;
    rootcircuit = new CircuitModel();
    rootcircuit.setView(new CircuitView(rootcircuit));
    // hack - this is where things look for current root circuit
    UIMainFrame.rootcircuit=rootcircuit;
    actions = new UIActions(rootcircuit);
    JToolBar outerToolBar = new JToolBar();
    toolBar = new JToolBar();
    JScrollPane outerScroll = new JScrollPane(toolBar);
    outerScroll.setPreferredSize(new Dimension(800,60));
    toolBar.setBorderPainted(false);
    toolBar.setMargin(new Insets(0,0,0,0));
    toolBar.setFloatable(false);
    toolBar.add(actions.addAnd);
    toolBar.add(actions.addOr);
    toolBar.add(actions.addNot);
    toolBar.add(actions.addPlus);
    toolBar.add(actions.addOnOff);
    toolBar.add(actions.addSwitch);
    toolBar.add(actions.addInput);
    toolBar.add(actions.addOutput);
    toolBar.add(actions.addClock);
    toolBar.add(actions.addLatch);
    toolBar.add(actions.addOscilloscope);
    JToolBar buttonBar = new UIToolBar(actions);
    this.setJMenuBar(new UIMenuBar(actions));
    JScrollPane jsp = new JScrollPane((Component)rootcircuit.getView());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(jsp,BorderLayout.CENTER);
    this.getContentPane().add(buttonBar,BorderLayout.NORTH);
    this.getContentPane().add(outerScroll,BorderLayout.SOUTH);
    validate();
    //shutdown on window close
    setVisible(true);
  }


  public void init()
  {
    super.init();
    jso = JSObject.getWindow(this);
    parseParameters();
  }

  static HashMap LMSSimulator = new HashMap();

  /**
   * Encode and put things in the LMS. If it is disabled, this does nothing.
   * @param key where to put 'value'
   * @param value what to put at 'key'
   */
  public static void LMSPut(String key,String value) throws /*JS tms*/Exception
  {
    Debug.out("LMSPUT: "+key+","+value);
      LMSSimulator.put(key,value);
      if (!LMSEnabled)
    {
      return;
    }
    writeHashMap();
  }


  //read from lms - we use write through caching so this should be ok
  private static void readHashMap()
  {
    Object v = jso.eval("loadAppletEvent(\"cmi.suspend_data\")");
    if (v==null) {LMSSimulator = new HashMap(); return;}
    String s= v.toString();

    try {
      s = URLDecoder.decode(s,"UTF-8");
      InputStream is = new StringBufferInputStream(s);
      InflaterInputStream iis = new InflaterInputStream(is);
      ObjectInputStream ois = new ObjectInputStream(iis);
      LMSSimulator  = (HashMap) ois.readObject();
    }
    catch (Exception e)
    {
      LMSSimulator = new HashMap();
      e.printStackTrace();
      staticUtils.errorMessage("Couldnt read circuits from LMS");
    }

  }

  // we use write through caching
  private static void writeHashMap()
  {

  try{
    // write to a string
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // compress
    DeflaterOutputStream dos = new DeflaterOutputStream(bos);
    // serialize
    ObjectOutputStream oos = new ObjectOutputStream(dos);
    oos.writeObject(LMSSimulator);
    dos.finish();
    oos.flush(); dos.flush(); bos.flush();
    String s = bos.toString();
    //encode
    String hashmap =  URLEncoder.encode(s,"UTF-8");
    //write to LMS
    Debug.out("Evaluating: storeAppletEvent(\"cmi.suspend_data\",\""+hashmap+"\")");
    jso.eval("storeAppletEvent(\"cmi.suspend_data\",\""+hashmap+"\")");
  }
  catch (Exception e)
    {
      e.printStackTrace();
      LMSSimulator = new HashMap();
      staticUtils.errorMessage("Couldnt write circuit to LMS");
    }
  }

  /**
   * Get and decode things from the LMS. If it is disabled, this return null.
   * @param key Where to look for a value
   * @return The value at the supplied key, null if no LMS, or if communication fails
   */
  public static String LMSGet(String key) throws /*JS tms*/Exception
  {
      Debug.out("LMSGET: "+key);
      String ret = (String) LMSSimulator.get(key);
      if (ret==null) return "null";
      Debug.out("LMSGET returns: "+ret);
      return ret;
  }

 static int ncircuits=1;

  private static int getNumberCircuits()
  {
   Iterator i = LMSSimulator.keySet().iterator();
   int n=0;
   while(i.hasNext())
   {
    String s =  i.next().toString();
    if (s.startsWith("DLSim.core.savedcircuits.names["))
        n++;
   }
   return n;
  }

  public static String[] getLMSCircuitNames()
  {
   try
    {
     int ncircuits = getNumberCircuits();
     Vector names = new Vector();
     for (int i=0; i<ncircuits; i++)
     {
      names.add( LMSGet("DLSim.core.savedcircuits.names["+i+"]") );
     }
     String[] namesarr = new String[names.size()];
     names.copyInto(namesarr);
     return namesarr;
    }
    catch (/*JS tms*/Exception e)
    {
      System.err.println("Error getting saved circuits names");
      e.printStackTrace();
      return new String[] {};
    }
  }

  /**
   * Save the current root circuit to the LMS
   * @param name what to save as.
   */
  public static void saveCircuitToLMS(String name)
  {
    try
    {
      // find the number to save at
      String[] names = getLMSCircuitNames();
      int i;
      for (i=0; i<names.length; i++)
      {
        if (names[i].equals(name)) break;
      }
      CircuitMemento cm = new CircuitMemento(rootcircuit);
      LMSPut("DLSim.core.savedcircuits.names["+i+"]",name);
      LMSPut("DLSim.core.savedcircuits.mementos["+i+"]",cm.writeToString());
      ncircuits++;
      if (rootcircuit.hasIO())
      {
        // check the box is there
        if (uicc==null)
        {
          uicc = new UIComponentComboBox(rootcircuit);
          toolBar.add(uicc);
        }
        uicc.add(name,"DLSim.core.savedcircuits.mementos["+i+"]");
      }
    }
    catch (/*JS tms*/Exception e)
    {
      JOptionPane.showMessageDialog(currentinstance,"Communication with LMS failed! Circuit not saved \n"+e.getMessage());
      System.err.println("Error saving circuit");
      e.printStackTrace();
    }
  }

  /**
   * Load a circuit from the LMS
   * @param LMSKEY A *fully qualified* LMS key (e.g. DLSim.core.coursecircuits[10])
   */
  public static void loadCircuitFromLMSKEY(String LMSKEY)
  {

    Debug.out("try and load "+LMSKEY);
    rootcircuit.clear();
    CircuitMemento cm = loadMementoFromLMSKEY(LMSKEY);
    cm.addToCircuit(rootcircuit);
  }


  /**
  * Load a circuit memento from the LMS
  * @param LMSKEY A *fully qualified* LMS key (e.g. DLSim.core.coursecircuits[10])
  */
  public static CircuitMemento loadMementoFromLMSKEY(String LMSKEY)
  {
    try
   {
     Debug.out("try and load "+LMSKEY);
     String memento = LMSGet(LMSKEY);
     try
     {
       CircuitMemento cm = new CircuitMemento(memento);
       return cm;
     }
     catch (Exception e)
     {
     JOptionPane.showMessageDialog(currentinstance,"Circuit could not be parsed! Circuit not loaded \n"+e.getMessage());
     System.err.println("Error parsing circuit");
     e.printStackTrace();
     return null;
     }
   }
   catch (/*JS tms*/Exception e)
   {
     JOptionPane.showMessageDialog(currentinstance,"Communication with LMS failed! Circuit not loaded \n"+e.getMessage());
     System.err.println("Error loading circuit");
     e.printStackTrace();
     return null;
    }
  }
  /**
   * Load a circuit from the LMS - not case sensitive
   * @param name The name of a circuit that has been saved - this looks up
   * the name in the default location
   */
  public static void loadCircuitFromLMS(String name)
  {
    name = name.toLowerCase();
    Debug.out("try and load "+name);
    String[] names = AppletMain.getLMSCircuitNames();
    if (names.length==0)
    {
       JOptionPane.showMessageDialog(currentinstance,"No files found! \n");
       return;
    }
    int i;
    for (i=0;i<names.length;i++)
    {
     if (names[i].toLowerCase().equals(name)) break;
    }
    if (i<names.length)
    {
     loadCircuitFromLMSKEY("DLSim.core.savedcircuits.mementos["+i+"]");
    }
    else
    {
    JOptionPane.showMessageDialog(currentinstance,"File not found! \n");
    }
  }
  /**
   * Load a circuit from the LMS - not case sensitive
   * @param name The name of a circuit that has been saved - this looks up
   * the name in the default location
   * @return the memento, or null if not found
   */
  public static CircuitMemento loadMementoFromLMS(String name)
  {
    name = name.toLowerCase();
    Debug.out("try and load "+name);
    String[] names = AppletMain.getLMSCircuitNames();
    if (names.length==0)
    {
      return null;
    }
    int i;
    for (i=0;i<names.length;i++)
    {
      if (names[i].toLowerCase().equals(name)) break;
    }
    if (i<names.length)
    {
      return loadMementoFromLMSKEY("DLSim.core.savedcircuits.mementos["+i+"]");
    }
    else
    {
      return null;
    }
  }

  /**
   * This is to be called by the javascript. You can supply a st
   * @param s
   */
  public static void loadCircuit(String s)
  {

  }

}