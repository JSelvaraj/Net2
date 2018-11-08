/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;

/*
 * For convenience, a list of relevant documentation for AWT widgest used.
 * Frame https://docs.oracle.com/javase/8/docs/api/java/awt/Frame.html
 * WindowListener https://docs.oracle.com/javase/8/docs/api/java/awt/event/WindowListener.html
 * ActionListener https://docs.oracle.com/javase/8/docs/api/java/awt/event/ActionListener.html
 * FlowLayout https://docs.oracle.com/javase/8/docs/api/java/awt/FlowLayout.html
 * Label https://docs.oracle.com/javase/8/docs/api/java/awt/Label.html
 * List https://docs.oracle.com/javase/8/docs/api/java/awt/List.html
 * Panel https://docs.oracle.com/javase/8/docs/api/java/awt/Panel.html
 * TextField https://docs.oracle.com/javase/8/docs/api/java/awt/TextField.html
 * TextArea https://docs.oracle.com/javase/8/docs/api/java/awt/TextArea.html
 */

public class MessageCheckerGUI
  extends     Frame
  implements  ActionListener,
              WindowListener, Runnable
{
  private String currentUser;
  // Label for the GUI
  private String name;

  // For outgoing and incoming messages.
  private Messages messages;

  // Where new users will be listed.
  private Users users;

  // Where general information and notifications are displayed.
  private Notifications notifications;
  private MulticastSocket address;

  public MessageCheckerGUI(String name)
  {
    super(name + " : notifications and users"); // call the Frame constructor

    currentUser = name;
    /*
     * The AWT code below lays out the widgets as follows.

     +------------------ Frame ---------------------+
     |                                              |
     |  +-------- Panel (Notifications) ---------+  |
     |  | +-- Label --+ +------ TextArea ------+ |  |
     |  | |           | |                      | |  |
     |  | +-----------+ +----------------------+ |  |
     |  +----------------------------------------+  |
     |                                              |
     |  +------------ Panel (Users) -------------+  |
     |  | +-- Label --+ +-------- List --------+ |  |
     |  | |           | |                      | |  |
     |  | +-----------+ +----------------------+ |  |
     |  +----------------------------------------+  |
     |                                              |
     +----------------------------------------------+

     * The Frame and Panel objects are not visible -- they
     * form part of the GUI construction.
     *
     */

    /*
     * Simple GUI layout - FlowLayout.
     * GridBagLayout would be better, giving more precise control
     * over layout, but would require a lot more code.
     */
    setLayout(new FlowLayout());
    setBounds(0, 0, 800, 225); // Size of Frame

    Panel p; // tmp variable

    p = new Panel();
    p.add(new Label("Notifications"));
    TextArea n = new TextArea("", 4, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
    p.add(n);
    add(p); // to this Frame
    notifications = new Notifications(n);

    p = new Panel();
    p.add(new Label("Users"));
    java.awt.List u = new List(4, false);
    p.add(u);
    add(p);  // to this Frame
    users = new Users(u, notifications, currentUser);
    Thread t = new Thread(users); // let it look after itself
    t.start();

    // This is a separate Frame
    messages = new Messages(name, notifications);
    messages.setVisible(true);
    t = new Thread(messages); // let it look after itself
    t.start();

  } // MessageCheckerGUI()


  @Override
  public void windowClosing(WindowEvent we)
  {
    Window w = we.getWindow();
    w.dispose();
    System.exit(0);
  }

  /*
   * These are required for WindowListener, but we are
   * not interested in them, so they are empty methods.
   */
  @Override
  public void windowClosed(WindowEvent we) { }
  @Override
  public void windowActivated(WindowEvent we) { }
  @Override
  public void windowDeactivated(WindowEvent we) { }
  @Override
  public void windowIconified(WindowEvent we) { }
  @Override
  public void windowDeiconified(WindowEvent we) { }
  @Override
  public void windowOpened(WindowEvent we) { }

  /*
   * ActionListener method - required.
   */
  @Override
  public void actionPerformed(ActionEvent ae) { } // empty

  @Override
  public void run() {

  }
} // class MessageCheckerGUI
