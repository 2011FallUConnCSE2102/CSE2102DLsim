package dlsim.DLSim;
import java.awt.Graphics;
import java.awt.Shape;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public interface PaintableObject
{
  public void paint(Graphics g);
  public Shape getShape();
}