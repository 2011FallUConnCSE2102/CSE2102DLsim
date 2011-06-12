package dlsim.DLSim;
import java.io.*;
import java.net.*;
import java.util.*;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.xml.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class CircuitMemento
{

  Vector components = new Vector(); // a vector of componentmementos
  Vector wires = new Vector();      // a vector of wiremementos
  HashMap componentindex = new HashMap(); // an id for each componentmemento
  URL fromURL=null;

  /** Recreate a memento from this file
   * @deprectaed - use URLS */
  public CircuitMemento(File f) throws IOException
  {
    // read the memento from this file
    fromURL=f.toURL();
    readURL(fromURL);
  }

  /**
   * Recreate a memento from this string
   * @param s The String representing the circuitmemento
   * @throws IOException
   */
  public CircuitMemento(String s) throws IOException
  {
    // read the memento from this URL
    Debug.out("Trying to load a circuit from a string \n" + s);
    readString(s);
  }

  /**
   * Recreate a memento from this URL
   * @param u the URL
   * @throws IOException if anything goes wrong reading from this URL
   */
  public CircuitMemento(URL u) throws IOException
  {
    // read the memento from this URL
    Debug.out("trying to load a circuit from " + u);
    fromURL=u;
    readURL(u);
  }

  /** Create a memento of these wires and components */
  public CircuitMemento(Vector wi,Vector co)
  {
    HashMap componentToMemento = new HashMap();
    // make sure the terminals are in order - if we are copying, we want to add
    // them in ascending order so the new IDs they get are right
    dlsim.DLSim.Util.Sort s = new dlsim.DLSim.Util.Sort(o);
    co=s.sortVector(co);

    Enumeration e = co.elements();
    int id=0;
    //create and store a componentmemento for each component
    //giving each memento an index, stored in the hashmap
    while (e.hasMoreElements())
    {
     ComponentModel c = (ComponentModel) e.nextElement();
     ComponentMemento m = new ComponentMemento(c,id);
     componentToMemento.put(c,m);
     componentindex.put(new Integer(id),m);
     components.add(m);
     id++;
    }
    //create and store a wirememento for each wire
    //referring to the componentmementos at each terminal
    e=wi.elements();
    HashMap bundleToID = new HashMap();
    int maxbundleid=0;
    while (e.hasMoreElements())
    {
     WireModel w = (WireModel) e.nextElement();
     // give each bundle a unique id
     WireBundle wb = w.getBundle();
     int bundleid;
     if(wb==null)
     {
       Debug.out("No bundle - New Bundle ID Allocated");
       bundleid=maxbundleid++;
     }
       else
     {
         if (!bundleToID.containsKey(wb))
         { // new bundle - new id
           bundleid=maxbundleid;
           Debug.out("New Bundle ID Allocated");
           bundleToID.put(wb,new Integer(bundleid));
           maxbundleid++;
         }
         else
         { //old bundle, retrieve ID
           Debug.out("Bundle ID reused");
           bundleid=((Integer)bundleToID.get(wb)).intValue();
         }
     }
     WireMemento m = new WireMemento(
          (ComponentMemento)componentToMemento.get(w.getTo()),
          (ComponentMemento)componentToMemento.get(w.getFrom()),
                     w.getInputTerminalNumber(),
                     w.getOutputTerminalNumber(),
                     bundleid,
                     w.getView().getType());
     wires.add(m);
    }
  }

  /** Create a memento of this entire circuit */
  public CircuitMemento(CircuitModel m)
  {
  this(m.getWires(),m.getComponents());
  }

  public boolean hasIO()
  {
    Enumeration e = components.elements();
    while (e.hasMoreElements())
    {
      ComponentMemento cmem = (ComponentMemento)e.nextElement();
      if ( (cmem.type.equals("INPUT")) || (cmem.type.equals("OUTPUT")) )
        return true;
    }
    return false;
  }

  public CircuitMemento(Element circuitmemento)
  {
  readFromElement(circuitmemento);
  }

  PrintStream output;
  int tab=0;


  /** Write this memento to the given file */
  public void writeToFile(File f) throws Exception
  {
   FileOutputStream s = new FileOutputStream(f);
   output = new PrintStream(s);
   writeToStream(output);
  }

  /** write to the given printStream */
  public void writeToStream(PrintStream s)
  {
   output=s;
   write("<CIRCUITMEMENTO>");
   tab++;
   write("<COMPONENTS>");
   tab++;
   writeComponents();
   tab--;
   write("</COMPONENTS>");
   write("<WIRES>");
   tab++;
   writeWires();
   tab--;
   write("</WIRES>");
   tab--;
   write("</CIRCUITMEMENTO>");
  }

  String xmlstring;
  boolean writetostring=false;

 public String writeToString()
  {
   xmlstring="";
   writetostring=true;
   write("<CIRCUITMEMENTO>");
   tab++;
   write("<COMPONENTS>");
   tab++;
   writeComponents();
   tab--;
   write("</COMPONENTS>");
   write("<WIRES>");
   tab++;
   writeWires();
   tab--;
   write("</WIRES>");
   tab--;
   write("</CIRCUITMEMENTO>");
   writetostring=false;
   return xmlstring;
  }

  private void write(String s)
  {
   if (writetostring)
   {
   for (int i=0;i<tab;i++) {
    xmlstring+=" ";}
    xmlstring+=s+"\n";
   }
   else
   {
    for (int i=0;i<tab;i++) {
    output.print(" ");}
    output.println(s);
   }
  }

  private void writeComponents()
  {
   Enumeration e = components.elements();
   while (e.hasMoreElements())
    {
      ComponentMemento m = (ComponentMemento) e.nextElement();
      write(m.getMementoString());
    }
  }



  private void writeWires()
  {
    //loop through all the wires, looking up the ids for to and from in the
    //hashmap
    Enumeration e = wires.elements();
    while (e.hasMoreElements())
    {
     write(((WireMemento)e.nextElement()).getMementoString());
    }
  }



  /** Get the circuit this memento defines */
  public CircuitModel createModel()
  {
   CircuitModel m = new CircuitModel();
   this.addToCircuit(m);
   if (this.fromURL!=null) m.setFrom(fromURL);
   return m;
  }

  static dlsim.DLSim.Util.Sort.Order o = new dlsim.DLSim.Util.Sort.Order()
  {
    public boolean lessThan(Object a,Object b)
    {
      if ( (a instanceof TerminalModel) && !(b instanceof TerminalModel) )
      {
        return false;
      }
      if ( !(a instanceof TerminalModel) && (b instanceof TerminalModel) )
      {
        return true;
      }

      if ( !(a instanceof TerminalModel) && !(b instanceof TerminalModel) )
      {
        return false;
      }
      if ( (a instanceof TerminalModel) && (b instanceof TerminalModel) )
      {
        return (((TerminalModel)a).getID()<((TerminalModel)b).getID());
      }
      return true;
    }

    public boolean equals(Object a,Object b)
    {
      if ( !(a instanceof TerminalModel) && !(b instanceof TerminalModel) )
      {
        return true;
      }
      if ( (a instanceof TerminalModel) && (b instanceof TerminalModel) )
      {
       return (((TerminalModel)a).getID()==((TerminalModel)b).getID());
      }
      return false;
    }
  };

  /** Adds a copy of this memento to the target
   *  @return A vector containing the components and wires that were added
   */
  public Vector addToCircuit(CircuitModel target)
  {
   //map to find model created by memento
   HashMap IDToCModel = new HashMap();
   Vector added = new Vector();

   // Decode and add components
   Enumeration componentmems = components.elements();
   while (componentmems.hasMoreElements())
         {
           ComponentMemento c = (ComponentMemento) componentmems.nextElement();
           ComponentModel m = c.getComponent(target);
           added.add(m);
           IDToCModel.put(new Integer(c.getID()),m);
         }
   //Decode and add wires
   Enumeration wiremems = wires.elements();
   HashMap IDToBundle = new HashMap();
   while (wiremems.hasMoreElements())
    {
     WireMemento w = (WireMemento) wiremems.nextElement();
     //add to wires
     WireModel wire = new WireModel(
                      (ComponentModel)IDToCModel.get(new Integer(w.getFrom())),
                      w.getOutputTerminalNumber(),
                      (ComponentModel)IDToCModel.get(new Integer(w.getTo())),
                      w.getInputTerminalNumber(),w.getWireStyle());
     added.add(wire);
     //associate with components
     ((ComponentModel)IDToCModel.get(new Integer(w.getFrom()))).addOutputWire(wire);
     ((ComponentModel)IDToCModel.get(new Integer(w.getTo()))).addInputWire(wire);
     target.addWire(wire);
     //do bundle 'thang'
     Integer ID =new Integer(w.bundleid);
     if (IDToBundle.containsKey(ID) && (ID.intValue()!=-1) ) // -1 means legacy file, with no bus defined
     {
       Debug.out("Added to existing bundle");
       WireBundle wb = (WireBundle) IDToBundle.get(ID);
       wb.addBundle(wire.getBundle());
     }
     else
     {
       Debug.out("New bundleID recognised");
       IDToBundle.put(ID,wire.getBundle());
     }
    }
    return added;
  }

  private void readString(String s) throws IOException
  {
    InputStream is = new StringBufferInputStream(s);
    Document DOM = new Document(is);
    readFromElement(DOM.getElement());
    is.close();
  }

  private void readURL(URL u) throws IOException
  {
    InputStream is = u.openStream();
    Document DOM = new Document(is);
    readFromElement(DOM.getElement());
    is.close();
  }

  private void readFromElement(Element top)
  {
    // must contain at least one components tag
    Element c = top.getElementAt(0);
    // must contain at least one wires tag
    Element w = top.getElementAt(1);
    // components  contains * components
    for (int i=0; i<c.getElementCount();i++)
        {
         ComponentMemento cmem = new ComponentMemento(c.getElementAt(i));
         components.add(cmem);
         componentindex.put(new Integer(cmem.getID()),cmem);
        }

    //  wires contains * wires
    for (int i=0; i<w.getElementCount();i++)
        {
         WireMemento wmem = new WireMemento(w.getElementAt(i));
         wires.add(wmem);
        }

  }

}

class WireMemento
{
 int inputnumber,outputnumber;
 int to,from;
 int bundleid;
 String style;

 public WireMemento(ComponentMemento to, ComponentMemento from,int inputnumber, int outputnumber, int bundleid,String style)
 {
  this.to=to.getID();
  this.from=from.getID();
  this.bundleid=bundleid;
  this.style=style;
  this.inputnumber=inputnumber;
  this.outputnumber=outputnumber;
 }

 public WireMemento(Element e)
 {
  this.to = Integer.parseInt(e.getAttribute("TO"));
  this.from = Integer.parseInt(e.getAttribute("FROM"));
  this.inputnumber = Integer.parseInt(e.getAttribute("TOTERMINAL"));
  this.outputnumber = Integer.parseInt(e.getAttribute("FROMTERMINAL"));
  try {
  this.bundleid = Integer.parseInt(e.getAttribute("BUNDLEID"));
  }
  catch(NumberFormatException ex) {bundleid=-1;}
    this.style = e.getAttribute("STYLE");
 }

  public String getMementoString()
  {
    return("<WIRE TO=\""+getTo()
         +"\" FROM=\""+getFrom()
         +"\" TOTERMINAL=\""+getInputTerminalNumber()
         +"\" FROMTERMINAL=\""+getOutputTerminalNumber()
         +"\" STYLE=\""+getWireStyle()
         +"\" BUNDLEID=\""+getBundleID()
         +"\"/>");
  }

  public int getTo() {return to;}

  public int getFrom(){return from;}

  public int getInputTerminalNumber(){return inputnumber;}

  public int getOutputTerminalNumber(){return outputnumber;}

  public int getBundleID() {return bundleid;}

  public String getWireStyle() {return style;}
}