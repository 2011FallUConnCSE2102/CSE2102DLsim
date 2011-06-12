package dlsim.DLSim;
import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIImages
{
  static Icon load;
  static Icon save;
  static Icon delete;
  static Icon newfile;
  static Icon saveAs;
  static Icon undo;
  static Icon redo;
  static Icon copy;
  static Icon startSimulation;
  static Icon stopSimulation;
  static Icon andIcon;
  static Icon notIcon;
  static Icon switchIcon;
  static Icon scopeIcon;
  static Icon inputIcon;
  static Icon outputIcon;
  static Icon plusIcon;
  static Icon onoffIcon;
  static Icon clockIcon;
  static Icon latchIcon;
  static Icon fromfile;
  static Icon orIcon;
  static Icon start;
  static Icon stop;
  static Icon ff;
  static Icon bus;
  static Icon routedWire;
  static Icon straightWire;
  static Icon curvedWire;
  static Icon thinking;
  static Cursor wirecursor;
  static boolean large=true;
  static Icon splash;
  public static Image icon;
  public static Image circuit;
  public UIImages()
  {
  }




  void getIcons()
  {

    String sep="/";

    Class cl = this.getClass();
    icon = Toolkit.getDefaultToolkit().createImage(cl.getResource( "Images/chip.gif"));
    circuit = Toolkit.getDefaultToolkit().createImage(cl.getResource( "Images/circuit.gif"));
    load = new ImageIcon(cl.getResource("Images/load.gif"));
    thinking = new ImageIcon(cl.getResource( "Images"+sep+"thinking.gif"));
    save = new ImageIcon(cl.getResource( "Images"+sep+"save.gif"));
    saveAs = new ImageIcon(cl.getResource( "Images"+sep+"saveas.gif"));
    newfile = new ImageIcon(cl.getResource( "Images"+sep+"new.gif"));
    delete = new ImageIcon(cl.getResource( "Images"+sep+"delete.gif"));
    splash = new ImageIcon((cl.getResource( "Images"+sep+"splash.gif")));
    undo = new ImageIcon(cl.getResource( "Images"+sep+"undo.gif"));
    bus = new ImageIcon(cl.getResource( "Images"+sep+"bus.gif"));
    copy = new ImageIcon(cl.getResource( "Images"+sep+"copy.gif"));
    redo = new ImageIcon(cl.getResource( "Images"+sep+"redo.gif"));
    start =  new ImageIcon(cl.getResource( "Images"+sep+"play-start.gif"));
    stop =  new ImageIcon(cl.getResource( "Images"+sep+"play-stop.gif"));
    ff =  new ImageIcon(cl.getResource( "Images"+sep+"play-fast-forward.gif"));
    andIcon = new ImageIcon(cl.getResource( "Images"+sep+"andiconsmall.gif"));
    orIcon = new ImageIcon(cl.getResource( "Images"+sep+"oriconsmall.gif"));
    notIcon = new ImageIcon(cl.getResource( "Images"+sep+"noticonsmall.gif"));
    switchIcon = new ImageIcon(cl.getResource( "Images"+sep+"switchiconsmall.gif"));
    fromfile =  new ImageIcon(cl.getResource( "Images"+sep+"fromfileiconsmall.gif"));
    scopeIcon=  new ImageIcon(cl.getResource( "Images"+sep+"scopeiconsmall.gif"));
    inputIcon=  new ImageIcon(cl.getResource( "Images"+sep+"inputiconsmall.gif"));
    outputIcon=  new ImageIcon(cl.getResource( "Images"+sep+"outputiconsmall.gif"));
    onoffIcon=  new ImageIcon(cl.getResource( "Images"+sep+"onofficonsmall.gif"));
    clockIcon=  new ImageIcon(cl.getResource( "Images"+sep+"clockiconsmall.gif"));
    latchIcon=  new ImageIcon(cl.getResource( "Images"+sep+"latchiconsmall.gif"));
    plusIcon=  new ImageIcon(cl.getResource( "Images"+sep+"plusiconsmall.gif"));
    routedWire=  new ImageIcon(cl.getResource( "Images"+sep+"routedwire.gif"));
    straightWire=  new ImageIcon(cl.getResource( "Images"+sep+"straightwire.gif"));
    curvedWire=  new ImageIcon(cl.getResource( "Images"+sep+"curvedwire.gif"));
    wirecursor=
        Toolkit.getDefaultToolkit().createCustomCursor(
        Toolkit.getDefaultToolkit().createImage(cl.getResource( "Images"+sep+"wirecursor.gif")),
        new Point(3,1),
        "Wire cursor");

  }




}