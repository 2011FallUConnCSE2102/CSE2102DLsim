package dlsim.DLSim;
import dlsim.DLSim.Util.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
/**
 *  WireModel: The model for a wire. This will update the inputs on 'to' when send() is called
 *  @version .0 1
 */
public class WireModel implements contextSensitiveComponent
{

    private ComponentModel from;


    private ComponentModel to;

    boolean selected;
    boolean lastsentvalue= false;
    int output;
    int input;
    WireView myView;

    public WireModel(ComponentModel from, int output, ComponentModel to, int input,String wiretype)
    {
     this.from=from;
     this.to=to;
     this.input=input;
     this.output=output;
     from.addOutputWire(this);
     to.addInputWire(this);
     myView=WireViewFactory.getView(this,wiretype);
     bundle=new WireBundle(this);
    }

    public WireModel(ComponentModel from, int output, ComponentModel to, int input) {
        this.from=from;
        this.to=to;
        this.input=input;
        this.output=output;
        from.addOutputWire(this);
        to.addInputWire(this);
        myView=WireViewFactory.getView(this);
        bundle=new WireBundle(this);
    }

    public int getInputTerminalNumber()
    {
        return input;
    }

    public int getOutputTerminalNumber()
    {
        return output;
    }

    public void send()
    {
           lastsentvalue=from.getOutput(output);
           to.setInput(input ,lastsentvalue);
    }

    public boolean getValue()
    {
     return lastsentvalue;
    }


    public ComponentModel getTo()
    {
        return to;
    }

    public ComponentModel getFrom()
    {
        return from;
    }

    WireBundle bundle;

    public WireBundle getBundle()
    {
      return bundle;
    }

    public void setBundle(WireBundle wb)
    {
     bundle=wb;
    }



   public static Operation sendAll()
   {
     return new Operation()
        {
            public Object op(Object o)
            {
                                ((WireModel)o).send();
                                return null;
            }
        };
   }

   public WireView getView()
   {
    if (myView==null) myView=new WireViewCurved(this);
    return myView;
   }

   public void setView(WireView w)
   {
    from.getCircuit().getView().remove(myView);
    myView =w ;
    from.getCircuit().getView().add(w);
   }

   public JPopupMenu getMenu()
   {
     JPopupMenu myMenu = new JPopupMenu();
     myMenu.add(deleteMe);
     myMenu.addSeparator();
     myMenu.add(straightWire);
     myMenu.add(curvedWire);
     myMenu.add(routedWire);
     return myMenu;
   }

   final WireModel me = this;

   public AbstractAction deleteMe = new AbstractAction("Delete")
   {
     public void actionPerformed(ActionEvent e)
     {
       Vector c = new Vector(1);
       Vector w = new Vector(2);
       w.add(me);
       UICommand.deleteComponents(c,w,me.getFrom().getCircuit()).execute();
     }
   };

  public AbstractAction straightWire = new AbstractAction("Draw Straight")
  {
    public void actionPerformed(ActionEvent e)
    {
      me.setView(new WireView(me));
    }
   };

  public AbstractAction curvedWire = new AbstractAction("Draw Curved")
  {
    public void actionPerformed(ActionEvent e)
    {
      me.setView(new WireViewCurved(me));
    }
  };

  public AbstractAction routedWire = new AbstractAction("Avoid obstacles (buggy)")
  {
    public void actionPerformed(ActionEvent e)
    {
      me.setView(new WireViewAStar(me));
    }
  };


}

