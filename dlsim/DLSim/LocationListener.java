package dlsim.DLSim;

/**
 * <p>Title: LocationListener </p>
 * <p>Description: An interface you need to fulfill to be notified when a
 * paintable changes location </p>
 *
 * @author M.Leslie
 * @version 1.0
 */

public interface LocationListener {

  /**
   * Called when a paintable you are listening to changes position
   * @param p the paintable that has moved
   */
  public void locationChanged(Paintable p);

}