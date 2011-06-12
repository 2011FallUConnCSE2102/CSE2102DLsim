package dlsim.DLSim;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class WireViewFactory
{

  public WireViewFactory()
  {

  }
  static String type="STRAIGHT";
  public static final String STRAIGHT = "STRAIGHT";
  public static final String ROUTED = "ROUTED";
  public static final String CURVED = "CURVED";

  /**
   * Sets the type of view this will return in future
   * @param ntype one of CURVED,STRAIGHT or ROUTED
   */
  public static void setWireType(String ntype)
  {
    //ignore nonsense values
    if (ntype.equals("CURVED") || ntype.equals("ROUTED") || ntype.equals("STRAIGHT"))
    type=ntype;
  }

  /**
   * return a wireview of the currently selected type
   * @param w the wiremodel for this view to draw
   * @return the view for this wire a straight wire is supplied
   * if the type parameter is not understood
   */
  public static WireView getView(WireModel w)
  {
    if (type.equals("CURVED")) return new WireViewCurved(w);
    if (type.equals("ROUTED")) return new WireViewAStar(w);
    if (type.equals("STRAIGHT")) return new WireView(w);
    // default case
    return new WireView(w);
  }

  /**
   * specify the type of wire
   * @param w the wiremodel for this view to draw
   * @param type one of CURVED,STRAIGHT or ROUTED
   * @return the view for this wire, a straight wire is supplied
   * if the type parameter is not understood
   */
  public static WireView getView(WireModel w,String type)
  {
    Debug.out("Wire factory making a "+type+" wire");
    if (type==null) return new WireView(w);
    if (type.equals("CURVED")) return new WireViewCurved(w);
    if (type.equals("ROUTED")) return new WireViewAStar(w);
    if (type.equals("STRAIGHT")) return new WireView(w);
    //default case
    return new WireView(w);
  }

}