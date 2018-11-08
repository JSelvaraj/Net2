/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

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

    Messages(String id, Notifications n) {
        super(id + " : messages"); // call the Frame constructor
        this.id = id;

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
        add(p); // to this Frame

        // This is a separate Frame -- appears in a separate OS window
        messages = new TextArea("", 20, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        p = new Panel();
        p.add(new Label(id));
        p.add(messages);
        add(p); // to this Frame

        // This obect handles window events (clicks) ...
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

    /*
     * ActionListener method - required.
     * Text input from user - to transmit on the network.
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

        String m = id + ":" + f[1]; // senders id in outgoing message
        MessageCheckerCommon.tx_message(m);

        String s = "<- tx " + f[0] + " : " + f[1] + "\n"; // mark outgoing messages - for demo purposes
        messages.insert(s, 0); // top of TextArea
        notifications.notify("Message sent to: " + f[0]); // for demo purposes

        input.setText(""); // make sure TextField is empty
    }

    /*
     * Runnable method - required.
     * Incoming messages - received from the network.
     */
    @Override
    public void run() {
        while (true) {
            ArrayList<String> rx = MessageCheckerCommon.rx_messages();

            for (int r = 0; r < rx.size(); ++r) {
                String m = rx.get(r);

                m = m.trim();
                if (m.length() > 0) {

                    // message format is
                    // sender:message
                    String[] f = m.split(":");

                    // This is not the best way to check the message format!
                    // For demo purposes only.
                    if (f == null || f.length != 2 ||
                            f[0].length() < 1 || f[1].length() < 1) {
                        notifications.notify("rx: Bad string received.");
                        continue;
                    }
                    f[0] = f[0].trim();
                    f[1] = f[1].trim();

                    if (f[0].length() < 1 || f[1].length() < 1) {
                        notifications.notify("rx: Bad string received.");
                        continue;
                    }

                    notifications.notify("Received a message from: " + f[0]);
                    String s = "-> rx " + f[0] + " : " + f[1] + "\n";
                    messages.insert(s, 0);

                } //m.length() > 0

            } // for (r < rx.size())

            try {
                Thread.sleep(sleepTime);
            } // do not need to check constantly
            catch (InterruptedException e) {
            } // do not care

        } // while(true)
    }

} // class Messages
