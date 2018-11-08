/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;

public class Notifications {

  private TextArea notifications;

  Notifications(TextArea n) {
    notifications = n;
  }

  void notify(String s) {
    String t = MessageCheckerCommon.timestamp();
    s = t + " " + s + "\n";
    notifications.insert(s, 0); // add to top
  } // notify()

} // class Notifications
