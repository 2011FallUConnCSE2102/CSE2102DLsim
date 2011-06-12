package dlsim.DLSim;
import dlsim.DLSim.xml.*;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.Util.*;
import javax.swing.JOptionPane;
import java.awt.Point;
import java.io.*;
import java.net.URL;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class ComponentMemento
{

  String memxml;

  int id;
  String type;
  Point position;
  CircuitMemento ic;
  String icURL;
  String terminalname="";
  int terminalid=0;
  int clockspeed;
  static boolean dynamicLinking = true;

  // the tab level for componentmementos to write at - changed when printing circuits within circuits!
  public static int tab=2;

  /** @link dependency */
  /*#WireMemento lnkWireMemento;*/

  public ComponentMemento(ComponentModel m,int id)
  {

   this.id=id;
   position=m.getView().getLocation();
   if (m instanceof ANDComponentModel)
   {
   type="AND";
   }
   else if (m instanceof ORComponentModel)
   {
   type="OR";
   }
   else if (m instanceof PLUSComponentModel)
   {
   type="PLUS";
   }
   else if (m instanceof NOTComponentModel)
   {
   type="NOT";
   }
   else if (m instanceof SwitchComponentModel)
   {
   type="SWITCH";
   }
    else if (m instanceof OnOffComponentModel)
   {
   type="ONOFF";
   }
   else if (m instanceof OscilloscopeComponentModel)
  {
  type="OSCILLOSCOPE";
  terminalname=((OscilloscopeComponentModel)m).getName();
   }
   else if (m instanceof OutputTerminalModel)
   {
   type="OUTPUT";
   terminalid=((OutputTerminalModel)m).getID();
   terminalname=((OutputTerminalModel)m).getName();
   }
   else if (m instanceof InputTerminalModel)
   {
   type="INPUT";
   terminalid=((InputTerminalModel)m).getID();
   terminalname=((InputTerminalModel)m).getName();
   }
   else if (m instanceof ClockComponentModel)
   {
     type="CLOCK";
     clockspeed=((ClockComponentModel)m).clkp;
   }
   else if (m instanceof LatchComponentModel)
   {
     type="LATCH";
   }
   else if (m instanceof ICModel)
   {
   type="IC";
   terminalname = ((ICModel)m).getName();
   URL file = ((ICModel)m).getInternal().getFrom();
   ic = new CircuitMemento(((ICModel)m).getInternal());
   if (dynamicLinking && file!=null )
   icURL = staticUtils.getLastNameFromURL(file);
   Debug.out("Creating a component memento for"+icURL);
   }
   else
   {
   type="UNKNOWN";
   }
   memxml = makeMementoString(position,type,id);
  }

  public ComponentMemento(Element e)
  {
   this.id = Integer.parseInt(e.getAttribute("ID"));
   this.type = e.getAttribute("TYPE");
   // first child is position
   this.position = new Point(
                   Integer.parseInt(e.getElementAt(0).getAttribute("X")),
                   Integer.parseInt(e.getElementAt(0).getAttribute("Y")));

   // If it is a clock, get the clockspeed
   if (type.equals("CLOCK"))
   {
     this.clockspeed=Integer.parseInt(e.getElementAt(1).getAttribute("CLOCKSPEED"));
   }

   // If it is a OSCILLOSCOPE, get the name
   if (type.equals("OSCILLOSCOPE"))
  {
    this.terminalname=e.getElementAt(1).getAttribute("NAME");
   }

   // If it is a IO component, get the name and ID
   if (type.equals("INPUT") || type.equals("OUTPUT") )
   {
    this.terminalid=Integer.parseInt(e.getElementAt(1).getAttribute("ID"));
    this.terminalname=e.getElementAt(2).getAttribute("NAME");
   }

   // If it is an IC, get the internal circuit filename/XML tag
   if (type.equals("IC") )
   {
    String file=e.getElementAt(1).getAttribute("FILE");
    if (file==null)
    {
    Debug.out("Creating an ICcomponent memento from XML, statically link");
    this.ic=new CircuitMemento(e.getElementAt(1));
    this.terminalname=e.getElementAt(2).getAttribute("NAME");
    }
    else
    {
      Debug.out("Creating an IC component memento from XML, dynamically link");
      this.icURL=file;
      this.terminalname=e.getElementAt(1).getAttribute("NAME");
      if (AppletMain.isapplet)
        // load from url
      {
        try
        {
          // try and load it from the net
          // check we have a web location to look for files at
          // if we do, load it
          this.ic=new CircuitMemento(new URL(Preferences.getDocumentsURL(),icURL));
        }
        catch (IOException e3)
        {
          staticUtils.errorMessage("Could not find a file linked to by this circuit \n"+e3.getMessage());
          return;
        }
      }
      else
        // load from file
      {
        try
        {
          // try and load it from a file
          this.ic=new CircuitMemento(new File(Preferences.getCircuitsPath(),icURL));
        }

        catch (IOException e3)
        {
          staticUtils.errorMessage("Could not find a file linked to by this circuit \n"+e3.getMessage());
          return;
        }
      }

    }
   }
   makeMementoString(position,type,id);
  }

  public int getID()
  {
   return id;
  }

  private String makeMementoString(Point pos, String type, int id)
  {
   tab--;
   String s =tab(tab)+"<COMPONENT ID=\""+id+"\" TYPE=\""+type+"\">\n";

   tab++;
   s+=tab(tab)+("<POSITION X=\""+pos.x+"\" Y=\""+pos.y+"\"/>\n");
   if ( (type.equals("INPUT")) || (type.equals("OUTPUT")) )
   {
    s+=tab(tab)+"<TERMINALID ID=\""+terminalid+"\"/>\n";
    s+=tab(tab)+"<TERMINALNAME NAME=\""+terminalname+"\"/>\n";
   }
   if ( type.equals("CLOCK")  )
  {
   s+=tab(tab)+"<CLOCK CLOCKSPEED=\""+clockspeed+"\"/>\n";
   }
   if ( type.equals("OSCILLOSCOPE")  )
 {
  s+=tab(tab)+"<TERMINALNAME NAME=\""+terminalname+"\"/>\n";
   }

   if ( type.equals("IC"))
   {
     if(icURL==null) //static link
     {
       s+=tab(tab)+ic.writeToString();
       s+=tab(tab)+"<TERMINALNAME NAME=\""+terminalname+"\"/>\n";
     }
     else
     {
       s+=tab(tab)+"<TERMINALNAME NAME=\""+terminalname+"\" FILE=\""+icURL+"\"/>\n";
     }

   }
   tab--;
   s+=tab(tab)+"</COMPONENT>";
   return s;
  }

  public String tab(int tab)
  {
   String s="";
   for (int i=0;i<tab;i++)
   {
    s+="  ";
   }
   return s;
  }

  public String getMementoString()
  {
    return memxml;
  }



  public ComponentModel getComponent(CircuitModel target)
  {
    ComponentModel c = null;
    if (type.equals("AND"))
          {
            c = new ANDComponentModel(target);
          }
    else if (type.equals("OR"))
         {
            c = new ORComponentModel(target);
         }
    else if (type.equals("NOT"))
         {
            c = new NOTComponentModel(target);
         }
    else if (type.equals("PLUS"))
         {
            c = new PLUSComponentModel(target);
         }
    else if (type.equals("INPUT"))
         {
            c = new InputTerminalModel(target,terminalid);
            if (((TerminalModel)c).getID()==terminalid)
            ((TerminalModel)c).setName(terminalname);
         }
    else if (type.equals("SWITCH"))
         {
            c = new SwitchComponentModel(target);
         }
    else if (type.equals("ONOFF"))
         {
            c = new OnOffComponentModel(target);
         }
    else if (type.equals("OUTPUT"))
         {
            c = new OutputTerminalModel(target,terminalid);
            if (((TerminalModel)c).getID()==terminalid)
            ((TerminalModel)c).setName(terminalname);
         }
    else if (type.equals("LATCH"))
              {
                 c = new LatchComponentModel(target);
         }
   else if (type.equals("IC"))
         {
            CircuitModel IC = ic.createModel();
            c = ICModel.setupIC(IC,target,terminalname);
            if (icURL!=null)
            {
            Debug.out("Creating an ICcomponent from memento, dynamic link");
            }
            else
            {
              Debug.out("Creating an ICcomponent from memento, no file so static link");
            }
         }
  else if (type.equals("CLOCK"))
  {
    c = new ClockComponentModel(target);
    ((ClockComponentModel)c).setClockPeriod(clockspeed);
  }
  else if (type.equals("OSCILLOSCOPE"))
 {
   c = new OscilloscopeComponentModel(target);
   ((OscilloscopeComponentModel)c).setName(terminalname);
  }
    else if (type.equals("UNKNOWN"))
         {
            staticUtils.errorMessage("This circuit contains an unknown component type");
         }
    c.getView().setLocation(position);
    return c;
  }

}