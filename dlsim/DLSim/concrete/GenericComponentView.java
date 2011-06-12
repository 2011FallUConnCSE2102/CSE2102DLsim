package dlsim.DLSim.concrete;

import java.awt.*;
import java.awt.geom.Area;
import javax.swing.*;
import dlsim.DLSim.concrete.*;
import dlsim.DLSim.Util.*;
import dlsim.DLSim.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class GenericComponentView extends ComponentView
{

   int x=0;
   int y=0;
   int w;
   int h;
   int inputs;
   int outputs;
   Point[] inputpos;
   Point[] outputpos;
   ComponentModel m;
   String label;
   boolean selected=false;

   public Color highTerminal = Color.red;
   public Color lowTerminal = Color.black;
   public Color borderColor = new Color(128,128,255);
   public Color mainColor = new Color(223,223,255);
   public Color shadowColor = new Color(191,191,255);
   public Color terminalShadowColor = new Color(0,0,0,150);
   public Color Terminal = Color.blue;
   public Color selectedTerminal = Color.white;
   public Font labelFont = new Font("Arial",Font.PLAIN,12);

   /** Creates a generic 'chip' view for the supplied model */
   public GenericComponentView(ComponentModel m)
   {
     super(m);
     this.m=m;
     this.inputs=m.getNumberInputs();
     this.outputs=m.getNumberOutputs();
     inputpos = new Point[inputs];
     outputpos = new Point[outputs];
     int max = java.lang.Math.max(inputs,outputs);
     double DT=26;
     if (m.getNumberInputs()>10 || m.getNumberOutputs()>10) DT=16;
     h=(int)java.lang.Math.max(DT*max,60);
     double c=h/2;
     w=80;
     double w=this.w;
     double inputs = this.inputs;
     double outputs= this.outputs;
     double x=(double)this.x;
     double y=c-(((outputs-1)/2)*DT);
     for (int i=0;i<outputs;i++)
     {
       outputpos[i] = new Point((int)w-10,(int)y-5);
       y+=DT;
     }

     y=c-(((inputs-1)/2)*DT);
     for (int i=0;i<inputs;i++)
     {
       inputpos[i] = new Point((int)0,(int)y-5);
       y+=DT;
     }


   }



     public void setLocationImpl(Point p)
     {
       this.x=p.x;
       this.y=p.y;
     }

     public void setSelectedImpl(boolean b)
     {
       if (b==selected) return;
       selected=b;
       repaint();
     }

     public boolean getSelected()
     {
     return selected;
     }

     public void drawLabel(Graphics g)
     {
       String label=this.label;
     if (label==null) return;
     int s = g.getFontMetrics().stringWidth(label);
     if (s>w-20) {label=label.substring(0,5)+"...";s = g.getFontMetrics().stringWidth(label);}
     g.setColor(Color.black);
     g.setFont(labelFont);
     g.drawString(label,x+(w/2)-(s/2),y+(h/2));
     drawInputLabels(g);
     }


  private int maxOutputLabelLength=0;
  private int maxInputLabelLength=0;
  public void drawInputLabels(Graphics g)
     {

       if (this.m instanceof labeledComponent)
       {
         g.setColor(new Color(255,255,255));
         g.setFont(new Font("Courier",Font.PLAIN,8));
         FontMetrics fm = g.getFontMetrics();
         labeledComponent ic = ((labeledComponent)this.m);
         for(int i=0; i<outputs; i++)
         {
           String label = ic.getOutputName(i);
           int w = fm.stringWidth(label);
           if (w>maxOutputLabelLength) maxOutputLabelLength=w;
           Point p = getPositionOfOutput(i);
           g.drawString(label,x+this.w,p.y+5);
         }
         for(int i=0; i<inputs; i++)
         {
           String label = ic.getInputName(i);
           int w = fm.stringWidth(label);
           if (w>maxInputLabelLength) maxInputLabelLength=w;
           Point p = getPositionOfInput(i);
          g.drawString(label,(x-w),p.y+5);

         }
       }
     }

     public void setLabel(String s)
     {
     label=s;
     repaint();
     }

     public String getLabel()
     {
     return label;
     }


     public void drawIconCenteredAtHeight(Icon i,int height,Graphics g)
     {
       int iconw = i.getIconWidth();
       if (iconw>=w)
       {
         System.err.println("Icon too big");
         return;
       }
       else
       {
         int x=this.x+(int)(1.0*(w-iconw)/2.0);
         i.paintIcon(UIMainFrame.currentinstance,g,x,y+height);
       }
     }



     public void paint(Graphics g)
      {
       //draw shadows
       //terminal shadows
       for(int i=0; i<outputs; i++)
       {
         Point p = getPositionOfOutput(i);
         g.setColor(this.terminalShadowColor);
         g.fillOval(p.x+2,p.y+2,10,10);
         g.setColor(new Color(0,0,0));
         g.drawOval(p.x+2,p.y+2,10,10);
       }
       for(int i=0; i<inputs; i++)
       {
         Point p = getPositionOfInput(i);
         g.setColor(this.terminalShadowColor);
         g.fillOval(p.x+2,p.y+2,10,10);
         g.setColor(new Color(0,0,0));
         g.drawOval(p.x+2,p.y+2,10,10);
       }
       //main shadow
       g.setColor(this.shadowColor);
       g.fillRect(x+14,y+4,w-20,h);
       g.setColor(this.borderColor);
       g.drawRect(x+14,y+4,w-20,h);
       //draw top layer
       g.setColor(this.mainColor);
       g.fillRect(x+10,y,w-20,h);
       g.setColor(this.borderColor);
       g.drawRect(x+10,y,w-20,h);
       if (m.isValid())
       g.setColor(this.borderColor);
       drawLabel(g);
       refreshTerminals(g);
      }

      public void refreshTerminals(Graphics g)
      {
        //Draw outputs
        for(int i=0; i<outputs; i++)
        {
          Point p = getPositionOfOutput(i);
          if (m.getOutput(i))
            g.setColor(highTerminal);   //High
          else
            g.setColor(lowTerminal);    //Low
          g.fillOval(p.x,p.y,10,10);
          if (m.getControl().outputIsSelected(i))
            g.setColor(selectedTerminal); //Selected
          else
            g.setColor(Terminal); //Deselected
          g.drawOval(p.x,p.y,10,10);
        }
        //Draw inputs
        for(int i=0; i<inputs; i++)
        {
          Point p = getPositionOfInput(i);
          if (m.getInput(i))
            g.setColor(highTerminal);   //High
          else
            g.setColor(lowTerminal);    //Low
          g.fillOval(p.x,p.y,10,10);

          if (m.getControl().inputIsSelected(i))
            g.setColor(selectedTerminal); //Selected
          else
            g.setColor(Terminal); //Deselected
          g.drawOval(p.x,p.y,10,10);
        }
        if (selected)
        {
          // draw selection box
          g.setColor(Color.BLACK);
          // top left
          g.drawLine(x-5,y-5,x,y-5);
          g.drawLine(x-5,y-5,x-5,y);
          //bottom left
          g.drawLine(x-5,y+h+5,x,y+h+5);
          g.drawLine(x-5,y+h+5,x-5,y+h);
          //bottom right
          g.drawLine(x+w+5,y+h+5,x+w,y+h+5);
          g.drawLine(x+w+5,y+h+5,x+w+5,y+h);
          //top right
          g.drawLine(x+w+5,y-5,x+w,y-5);
          g.drawLine(x+w+5,y-5,x+w+5,y);
        }
      }
      public Point getPositionOfInput(int i)
      {
      return new Point(inputpos[i].x+this.x,inputpos[i].y+this.y);
      }

      public Point getPositionOfOutput(int i)
      {
      return new Point(outputpos[i].x+this.x,outputpos[i].y+this.y);
      }

      public Shape getAreaOfInput(int i)
      {
       Point p = getPositionOfInput(i);
       return new java.awt.Rectangle(p.x,p.y,10,10);
      }

      public Shape getAreaOfOutput(int i)
      {
       Point p = getPositionOfOutput(i);
       return new java.awt.Rectangle(p.x,p.y,10,10);
      }

      /**
       * @returns -1 if not found
       */
      public  int getOutputAt(Point p)
      {
      for(int i=0; i<outputs; i++)
              {
               if (getAreaOfOutput(i).contains(p)) return i;
              }
      return -1;
      }

      /**
       * @returns -1 if not found
       */
      public int getInputAt(Point p)
      {
      for(int i=0; i<inputs; i++)
              {
               if (getAreaOfInput(i).contains(p)) return i;
              }
      return -1;
      }




      public Point getLocation()
      {
      return new Point(x,y);
      }

      public  Shape getShape()
      {
      Rectangle s =  new Rectangle(x-maxInputLabelLength-1,y-1,w+(maxInputLabelLength+maxOutputLabelLength)+6,h+5);      if (!selected) return s;
      else return new Rectangle(s.x-6,s.y-6,s.width+12,s.height+12);
      }


}