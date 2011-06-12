package dlsim.DLSim;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class LMSFileChooser extends JDialog {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JLabel fileNameLabel = new JLabel();
  JTextField jTextField1 = new JTextField();
  JButton loadButton = new JButton();
  JButton cancelButton = new JButton();
  private JList jList;
  private JLabel jLabel1 = new JLabel();
  private final boolean load;


  public LMSFileChooser(Frame owner,final boolean load) throws HeadlessException
  {
    super(owner);
    this.load=load;
    this.setTitle("LMS File Browser");
    this.setSize(500,300);
    this.getContentPane().setLayout(new BorderLayout());
    String[] files = AppletMain.getLMSCircuitNames();
    jList = new JList(files);
    jList.getSelectionModel().addListSelectionListener(
        new ListSelectionListener()
    {
       public void valueChanged(ListSelectionEvent e)
       {
         if (e.getValueIsAdjusting()) return;
         jTextField1.setText(jList.getSelectedValue().toString());
       }
    }
    );
    jList.addMouseListener(
        new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if (e.getClickCount()==2)
        {
          if (load)
          {
            String name = jTextField1.getText();
            AppletMain.loadCircuitFromLMS(name);
            dispose();
          }
          else
          {
            String name = jTextField1.getText();
            AppletMain.saveCircuitToLMS(name);
            dispose();
          }
        }
      }
    }
    );
    jbInit();
    this.validate();
    this.setVisible(true);
  }



  private void jbInit()  {
    this.getContentPane().setLayout(borderLayout1);
    fileNameLabel.setText("File name:");
    jTextField1.setColumns(20);
    if (load) loadButton.setText("Load");
    else
    loadButton.setText("Save");
    loadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jList.setToolTipText("List Of Files in LMS");
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jLabel1.setText("File List:");
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(fileNameLabel, null);
    jPanel1.add(jTextField1, null);
    jPanel1.add(loadButton, null);
    jPanel1.add(cancelButton, null);
    this.getContentPane().add(jList,  BorderLayout.CENTER);
    this.getContentPane().add(jLabel1, BorderLayout.NORTH);
  }

  void loadButton_actionPerformed(ActionEvent e)
  {
    if (load)
    {
    String name = jTextField1.getText();
    AppletMain.loadCircuitFromLMS(name);
    this.dispose();
    }
    else
    {
    String name = jTextField1.getText();
    AppletMain.saveCircuitToLMS(name);
    this.dispose();
    }
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();

  }
}