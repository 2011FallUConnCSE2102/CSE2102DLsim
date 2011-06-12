
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim.Util;
import dlsim.DLSim.*;
import java.awt.Point;
import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.io.*;
import java.net.*;

public class staticUtils {

  public static boolean snapToGrid()
      {
        return Preferences.getValue("SNAPTOGRID","TRUE").equals("TRUE");
      }






  public static void errorMessage(String s)
  {
   System.err.println(s);
   JOptionPane.showMessageDialog(UIMainFrame.currentinstance,s);
  }

  public static Rectangle boundingRect(Point p1, Point p2)
  {
    int x1,x2,y1,y2;
    x1 = java.lang.Math.min(p1.x,p2.x);
    x2 = java.lang.Math.max(p1.x,p2.x);
    y1 = java.lang.Math.min(p1.y,p2.y);
    y2 = java.lang.Math.max(p1.y,p2.y);
    return new Rectangle(x1,y1,(x2-x1),(y2-y1));
  }

  public static Rectangle bigBoundingRect(Point p1, Point p2)
{
  int x1,x2,y1,y2;
  x1 = java.lang.Math.min(p1.x,p2.x);
  x2 = java.lang.Math.max(p1.x,p2.x);
  y1 = java.lang.Math.min(p1.y,p2.y);
  y2 = java.lang.Math.max(p1.y,p2.y);
  return new Rectangle(x1-5,y1-5,(x2-x1)+10,(y2-y1)+10);
  }
  public static FileFilter xmlFilter = new FileFilter()
        {
         public boolean accept(File f)
         {
           if (f.getName().endsWith(".xml"))  return true;
           return false;
         }

         public String getDescription()
         {
           return "XML filter";
         }
        };

  public static FileFilter componentFilter = new FileFilter()
             {
              public boolean accept(File f)
              {
                if (f.getName().endsWith(".component.xml"))  return true;
                return false;
              }

              public String getDescription()
              {
                return "Component Filter";
              }
        };

  public static String getLastNameFromURL(URL u)
  {
    if (u==null) return "";
    String url = u.toString();
    // strip ending '/'
    if (url.endsWith("/")) url=url.substring(0,url.length()-1);
    int i = url.lastIndexOf("/");
    if (i==-1) return u.toString();
    // strip all upto and including last '/'
    String s = url.substring(i+1);
    return s;
  }

  public static File FileFromURL(URL u)
  {
    if (u==null) return null;
    if (u.getProtocol().equals("file"))
    {
      // convert into file
      try{
      URI uri = new URI(u.toString());
      File f = new File(uri);
      return f;
      }
      catch (Exception e) {e.printStackTrace();return null;}
    }
    else
    {
      return null;
    }
  }

  public static URL URLFromFile(File f)
 {
   try{
     return f.toURL();
   }
   catch (Exception e)
   {
    e.printStackTrace();
    errorMessage(e.getMessage());
    return null;
   }
  }





  public static FileView CircuitFileView()
  {
    return new CircuitFileView();
  }



  public static boolean[] decimalToBinary(int i)
  {
    boolean[] bools = new  boolean[8];
    int p=7;
    if (i>511) throw new Error("Overflow in binary to dec conversion");
    while (i>1 && p>0)
    {
      int j = i % 2;
      double a = (i/2.0);
      if (j==1) a=a-0.5;
      i=(int)Math.round(a);
      bools[p]=((j==1));
      p--;
    }
    bools[p]=(i==1);
    return bools;
  }


}

class CircuitFileView extends FileView
{
  public  String getName(File f)
       {
         if (f.getName().endsWith(".circuit.xml"))
          return f.getName().substring(0,f.getName().length()-12);
         if (f.getName().endsWith(".component.xml"))
          return f.getName().substring(0,f.getName().length()-14);
         return f.getName();
       }
       public String getDescription(File f)
       {
         if (f.getName().endsWith(".circuit.xml")) return "A circuit";
         if (f.getName().endsWith(".component.xml")) return "A sub-component";
         return "";
       }

       public  String getTypeDescription(File f)
       {
         if (f.getName().endsWith(".circuit.xml")) return "A circuit";
         if (f.getName().endsWith(".component.xml")) return "A sub-component";
         return "";
       }

       public  Icon getIcon(File f)
       {
         if (f.getName().endsWith(".circuit.xml")) return new ImageIcon(dlsim.DLSim.UIImages.circuit);
         if (f.getName().endsWith(".component.xml")) return new ImageIcon(dlsim.DLSim.UIImages.icon);
         return null;
       }

       public  Boolean isTraversable(File f)
       {
         return new Boolean(f.isDirectory());
       }

}
abstract class FileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter
{

}
