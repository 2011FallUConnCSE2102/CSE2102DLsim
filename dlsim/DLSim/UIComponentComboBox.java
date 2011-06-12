package dlsim.DLSim;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import dlsim.DLSim.Util.*;
import java.io.*;
import java.net.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: A combobox listing the user defined circuits in the circuits directory</p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIComponentComboBox extends JComboBox implements ActionListener
{
   CircuitModel m;
   boolean lock=false;

  public UIComponentComboBox(CircuitModel m)
  {
    super();
    this.m=m;
    this.setMaximumSize(new java.awt.Dimension(150,46));
    refresh();
    this.addActionListener(this);

    ((BasicComboBoxRenderer)this.getRenderer()).setIcon(UIImages.fromfile);
    // refresh every 10 seconds
    Thread refresher = new Thread()
    {
      public void run()
      {
         while (true)
          {
           try {sleep(10000);
           SwingUtilities.invokeLater(
               new Runnable()
             {
               public void run() {refresh();}
             }

             );
             } catch(InterruptedException e) {}
           }
      }
    };
    refresher.start();
  }

  public void add(URL u)
  {
    lock =true;
    urlMenuItem umi = new urlMenuItem(u,false);
    this.addItem(umi);
    lock=false;
  }

  public void add(String name,String LMSKEY)
 {
    lock=true;
    lmsMenuItem umi = new lmsMenuItem(name,LMSKEY);
    this.addItem(umi);
    lock=false;
  }

  private void refresh()
  {
    lock=true;
    // remove all the files
    Vector items = new Vector();
    for (int i=0; i< this.getItemCount(); i++)
    {
      items.add(this.getItemAt(i));
    }
    if (!AppletMain.isapplet) // applet mode cant access files
    {
      // put any files not already 'in'
      File dir = Preferences.getCircuitsPath();
      File[] components = dir.listFiles(staticUtils.componentFilter);
      for ( int i=0;i<components.length;i++)
      {
        urlMenuItem urmi = new urlMenuItem(staticUtils.URLFromFile(components[i]),true);
        if (!items.contains(urmi))
          this.addItem(urmi);
      }
    }
    lock=false;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (lock) return;
    Debug.out(e.getActionCommand());
    Object o = this.getSelectedItem();
    if (o instanceof urlMenuItem)
    {
      urlMenuItem umi=((urlMenuItem)o);
      URL u = umi.f;
      if (u!=null)
      {
        ComponentFactory thisURLFactory = ComponentFactory.fromURL(m,u);
        m.getView().setFloatingSelection(UIActions.GreyBoxSelection(umi.toString(),m,thisURLFactory));
      }
    }
    if (o instanceof lmsMenuItem)
    {
      lmsMenuItem lms = ((lmsMenuItem)o);
      ComponentFactory lmsFactory = ComponentFactory.fromLMS(m,lms.lmskey,lms.name);
      m.getView().setFloatingSelection(UIActions.GreyBoxSelection(lms.name,m,lmsFactory));
    }
  }



}

class urlMenuItem
{
  public URL f;
  boolean cd;
  urlMenuItem(URL f,boolean isFromCircuitDir)
  {
    this.f=f;
    cd = isFromCircuitDir;
  }

  public boolean isFromCircuitDir()
  {
    return cd;
  }

  public String toString()
  {
      String name = staticUtils.getLastNameFromURL(f);
      name = name.substring(0,name.length()-14);
      return name;
  }

  public boolean equals(Object o)
  {
    if (o instanceof urlMenuItem)
    {
     return ((urlMenuItem )o).f.equals(f);
    }
    return false;
  }
}

class lmsMenuItem
{
  public String name;
  public String lmskey;

  lmsMenuItem(String name,String lmskey)
  {
    this.name=name;
    this.lmskey=lmskey;
  }

  public String toString()
  {
      return name;
  }
}

