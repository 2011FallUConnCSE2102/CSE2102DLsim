package dlsim.DLSim;
import dlsim.DLSim.Util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
/**
 * DLPainter:
 * Deals with the graphical part of a DLComponent.
 * @version 1.0
 * @author M. Leslie
 */
public abstract class ComponentView extends Paintable  {

    private ComponentModel myModel;
    public static Color pink=new Color(255,192,255,150);
    public static Color green=new Color(10,192,10,150);

    public ComponentView(ComponentModel m)
    {
    myModel=m;
    }

    public ComponentModel getModel()
    {
    return myModel;
    }


    public abstract void refreshTerminals(Graphics g);

    /** All positions are relative to the circuit */
    public abstract Point getPositionOfInput(int i);

    /** All positions are relative to the circuit */
    public abstract Point getPositionOfOutput(int i);

    /** Get the area contanining this output
     *  All positions are relative to the circuit
     */
    public abstract Shape getAreaOfOutput(int i);

    /** Get the area contanining this input
     *  All positions are relative to the circuit
     */
    public abstract Shape getAreaOfInput(int i);

    /**
     * @returns -1 if not found
     */
    public abstract int getOutputAt(Point p);

    /**
     * @returns -1 if not found
     */
    public abstract int getInputAt(Point p);

    public JPopupMenu getMenu()
    {
      return myModel.getMenu();
    }

    /**
     * @param p the point to check for terminals
     */
    public boolean isTerminalAt(Point p)
    {
     int i = getOutputAt(p);
     if (i!=-1) return true;
     i = getInputAt(p);
     return (i!=-1);
    }


    public void clicked(MouseEvent e)
    {
      this.getModel().getControl().Clicked(e);
    }

}
