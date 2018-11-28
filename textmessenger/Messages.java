/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Messages
        extends Frame
        implements ActionListener,
        WindowListener,
        Runnable {
    String id;

    // Where you will type messages.
    private TextField input;

    // Where you will see incoming messages
    private TextArea messages;

    // Notifications for the application
    private Notifications notifications;

    private final static int sleepTime = 2000; // ms, 2s between checks

    private Socket socket;
    private static Checkbox onlineStatus;
    Users users;

    Messages(String id, Notifications n, Users u) {
        super(id + " : messages"); // call the Frame constructor
        this.id = id;
        this.users = u;

        notifications = n;


    /*
     * The AWT code below lays out the widgets as follows.

     +------------------- Frame --------------------+
     |                                              |
     |  +---------- Panel (Type here) -----------+  |
     |  | +-- Label --+ +------ TextField -----+ |  |
     |  | |           | |                      | |  |
     |  | +-----------+ +----------------------+ |  |
     |  +----------------------------------------+  |
     |                                              |
     |  +----------- Panel (Messages) -----------+  |
     |  | +-- Label --+ +------ TextArea ------+ |  |
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
        setBounds(0, 0, 800, 425); // size of Frame

        Panel p;

        input = new TextField(80);
        p = new Panel();
        p.add(new Label("Type here: "));
        p.add(input);
        onlineStatus = new Checkbox("Online");
        p.add(onlineStatus);
        add(p); // to this Frame

        // This is a separate Frame -- appears in a separate OS window
        messages = new TextArea("", 20, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        p = new Panel();
        p.add(new Label(id));
        p.add(messages);
        add(p); // to this Frame

        // This object handles window events (clicks) ...
        addWindowListener(this);
        // ... and actions for input (typing)
        input.addActionListener(this);
    }


    /*
     * These are required for WindowListener, but we are
     * not interested in them, so they are empty methods.
     */
    @Override
    public void windowClosing(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    /**
     * ActionListener method - required.
     * When the user enters text into the textbox of the messaging GUI, this method checks that the text is in the
     * correct format, then attempts to send the message to the user specified.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String t = input.getText();

        if (t == null) {
            return;
        }
        t = t.trim();
        if (t.length() < 1) {
            return;
        }

        // message format is
        // sender:message
        String[] f = t.split(":");

        // This is not the best way to check the message format!
        // For demo purposes only.
        if (f == null || f.length != 2 ||
                f[0].length() < 1 || f[1].length() < 1) {
            notifications.notify("tx: Bad message format.");
            return;
        }


        String s = "<- tx " + f[0] + " : " + f[1] + "\n"; // mark outgoing messages
        messages.insert(s, 0); // top of TextArea
        sendMessage(f[0].trim(), f[1]);

        input.setText(""); // make sure TextField is empty
    }

    /**
     * This is the runnable method required by the runnable interface.
     *
     * It listens on a ServerSocket for incoming connections, checks the message to see that it is coming from a user
     * that has been discovered by the users class and then displays it on the GUI
     */
    @Override
    public void run() {

        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(users.getPortNumber());
                Socket socket = serverSocket.accept();
                int oldSoTimeout = socket.getSoTimeout();
                socket.setSoTimeout(100);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = reader.readLine();
                socket.setSoTimeout(oldSoTimeout);
                if (!MessageDecoder.validMessage(message)) {
                    System.out.println(message);
                    throw new InvalidMessageFormatException();
                }
                String[] messagebits = message.split("]");
                String user = messagebits[1].substring(1);
                String text = messagebits[3].substring(1) + "\n";
                if (users.getDestinationPortNumber(user) != null) { // Users are only stored if they are online
                    notifications.notify("Message received from " + user);
                    messages.insert(user + ": " + text, 0);
                } else {
                    System.out.println("User that is not online has sent you a message, message was discarded.");
                }
                    socket.close();
                    socket = null;

            } catch (InvalidMessageFormatException e) {
                System.out.println("Incoming message had bad format");
            } catch (IOException e) {
                socket = null;
            }
        }


    }

    /**
     * This is used by the user class determine whether offline or online should be sent in the beacon.
     * @return Whether the checkbox is currently checked or not.
     */
    public static boolean getOnlineStatus() {
        return onlineStatus.getState();
    }


    /**
     * Uses the username to get the associated address and port number and send a string to that socket.
     * @param username
     * @param text
     */
    private void sendMessage (String username, String text) {
        try {
            socket = new Socket(InetAddress.getByName(users.getAddress(username)), Integer.parseInt(users.getDestinationPortNumber(username)));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message = "[" + MessageCheckerCommon.timestamp() +"][" + id + "][text][" + text.trim() + "]";
            System.out.println(message);
            writer.println(message);
            writer.flush();
            notifications.notify("Message sent to: " + username);
        } catch ( IOException e) {
            notifications.notify("Message failed to be sent to: " + username);
            e.printStackTrace();
        }
    }

} // class Messages
