
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import dlsim.DLSim.concrete.*;
public class Main {

  public static JFrame rootframe;
  static CircuitModel rootcircuit;

  public Main()
  {
  }

  public static void main(String[] args)
  {
    Preferences p = new Preferences();
    rootframe = new UIMainFrame();
  }


}