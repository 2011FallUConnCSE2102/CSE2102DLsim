package dlsim.DLSim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import dlsim.DLSim.concrete.OscilloscopeComponentModel;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class UIOscilloscope extends JPanel
{
  int N=50;
  boolean[] values=new boolean[N];
  boolean[] pausedvalues=new boolean[N];
  boolean ispaused;
  int start;
  int length;
  int pausedstart;
  int pausedlength;
  String name="Oscilloscope";
  JPopupMenu contextmenu;
  OscilloscopeComponentModel ocm;

  public UIOscilloscope(OscilloscopeComponentModel m)
  {
    ocm=m;
    this.setMaximumSize(new Dimension(1000,60));
    this.setPreferredSize(new Dimension(800,60));
    this.setMinimumSize(new Dimension(600,60));
    final UIOscilloscope uios=this;
    contextmenu = new JPopupMenu();
    contextmenu.add(this.changeSize);
    contextmenu.add(this.changeName);
    contextmenu.add(this.pauseAction);
    contextmenu.add(this.clearAction);
    this.add(contextmenu);

    this.addMouseListener(new MouseAdapter()
                          {
      public void mousePressed(MouseEvent e) {
       maybeShowPopup(e);
   }

   public void mouseReleased(MouseEvent e) {
     maybeShowPopup(e);
   }
      public void mouseClicked(MouseEvent e)
      {
        uios.putValue(Math.random()<0.5);
        maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e)
      {
        if (e.isPopupTrigger())
        {
          contextmenu.show(e.getComponent(),
                           e.getX(), e.getY());
        }
      }
    });

  }

  final UIOscilloscope o =this;

  AbstractAction changeSize =  new AbstractAction("Change size")
  {
    public void actionPerformed(ActionEvent e)
    {
      int maxsteps = (int)((o.getSize().getWidth())/3)-1;
      Object ret = JOptionPane.showInputDialog((Component)o,"How many steps would you like to be visible? (2-"+maxsteps+")",
          "Change size",JOptionPane.QUESTION_MESSAGE,
          (Icon)null,(Object[])null,""+o.getNumberPoints());
      if (ret!=null)
      {
        int steps = Integer.parseInt((String)ret);
        if  ((steps>maxsteps) || (steps<2)  )
        {
          JOptionPane.showMessageDialog(o,"Steps must be between 2 and "+maxsteps);
          return;
        }
        o.setNumberPoints(steps+1);
      }
    }
  };

  AbstractAction changeName =  new AbstractAction("Change name")
 {
   public void actionPerformed(ActionEvent e)
   {
     Object ret = JOptionPane.showInputDialog((Component)o,"Enter new name?",
         "Rename",JOptionPane.QUESTION_MESSAGE,
         (Icon)null,(Object[])null,o.getName());
     if (ret!=null)
     {
       o.setName((String)ret);
       ocm.setName((String)ret);
     }
   }
  };



  AbstractAction pauseAction =
      new AbstractAction("Pause")
   {
     public void actionPerformed(ActionEvent e)
     {
       if (o.ispaused)
       {
         this.putValue(AbstractAction.NAME,"Pause");
         o.pause();
         o.clearAction.setEnabled(true);
         o.changeSize.setEnabled(true);
       }
       else
       {
         this.putValue(AbstractAction.NAME,"UnPause");
         o.pause();
         o.clearAction.setEnabled(false);
         o.changeSize.setEnabled(false);
       }
     }
   };


  AbstractAction clearAction = new AbstractAction("Clear")
  {
    public void actionPerformed(ActionEvent e)
     {
      o.clear();
     }
  };


  public int getMaxNumberTicks()
  {
   return (int)((o.getSize().getWidth())/3)-1;
  }

  /** Toggle paused*/
  public void pause()
  {
    ispaused=!ispaused;
    pausedstart=start;
    pausedlength=length;
    pausedvalues= new boolean[N];
    System.arraycopy(values,0,pausedvalues,0,N);
    this.repaint();
  }

  /** Clear this oscilloscope*/
  public void clear()
  {
    this.values = new boolean[N];
    this.length=0;
    this.start=0;
    this.repaint();
  }

  /** set the name*/
  public void setName(String name)
  {
    this.name=name;
    this.repaint();
  }

   /** set the name*/
  public String getName()
  {
    return name;
  }

  /** Supply a new value to the trace*/
  public void putValue(boolean value)
  {
      values[ (start+length) % N]=value;
      if (length<(N)) length++;
      else
         start=start+1;
    this.repaint();
  }


  /** The number of time points to plot*/
  public void setNumberPoints(int p)
  {
   boolean[] newvalues=new boolean[p];
   for(int i=0; ( (i<N) && (i<p) && (i<length) );i++)
   {
     newvalues[i] = values[(start+i) % N];
   }
   N=p;
   values=newvalues;
   length = Math.min(length,p);
   start=0;
   repaint();
  }

  /** The number of time points being plotted*/
  public int getNumberPoints()
  {
    return N;
  }

  //difference between graph points
  private int getDT()
  {
    return java.lang.Math.max((int)(((double)getSize().getWidth()-20)/N),5);
  }

  public void paint(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setBackground(CircuitView.background);
    g2d.clearRect(0,0,this.getWidth(),this.getHeight());
     g2d.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
    //draw axes
    double xstart=10;
    double xend=this.getWidth()-10;
    double ystart=10;
    double yend=this.getHeight()-10;
    double ypeak=ystart+5;
    double ytrough=yend-5;
    double middle=(yend+ystart)/2;
    double dt = this.getDT();

    //axes
    g2d.setPaint(CircuitView.grid);
    g2d.drawLine((int)xstart,(int)middle,(int)xend,(int)middle); // draw time axis
    g2d.drawLine((int)10,(int)ystart,(int)10,(int)yend); //draw value axis

    //draw ticks
    for(double x=10;x<xend;x+=dt)
    {
      g2d.drawLine((int)x,(int)middle-5,(int)x,(int)middle+5);
    }


    // draw name
    g2d.setColor(new Color(255,255,255,100));
    g2d.setFont(new Font("Arial",Font.BOLD,12));
    int width = g2d.getFontMetrics().stringWidth(name);
    g2d.fillRect(0,0,width,10);
    g2d.setColor(new Color(0,0,0));
    g2d.drawString(name,0,10);

    int dstart = start;
    if (ispaused)  dstart = this.pausedstart;
    boolean[] dvalues = this.values;
    if (ispaused)  dvalues = this.pausedvalues;
    int dlength = length;
    if (ispaused)  dlength = pausedlength;

    Point p;
    if (values[dstart%N])
       p = new Point((int)10,(int)ypeak);
    else
       p= new Point((int)10,(int)ytrough);

    // draw square toothed graph. |___|
    g2d.setPaint(new Color(255,0,0,150));
    g2d.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
    java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
    // choose whether to used paused values or not

    for(int i=1;i<N && i<dlength ;i++)
    {
      int newx = p.x+(int)dt;
      java.awt.geom.Line2D.Double l = new java.awt.geom.Line2D.Double(p.x,p.y,newx,p.y);
      path.append(l,true);
      Point n;
      if (dvalues[(dstart+i)%N])
       n = new Point(newx,(int)ypeak);
      else
       n= new Point(newx,(int)ytrough);
      java.awt.geom.Line2D.Double l2 = new java.awt.geom.Line2D.Double(newx,p.y,n.x,n.y);
      path.append(l2,true);
      p=n;
    }
    g2d.draw(path);
  }

}