/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class Users implements Runnable {

  private java.awt.List users; // java.util.List also exists!
  private Notifications notifications;

  private final static int sleepTime = 5000; // ms, 5s between checks
  private MulticastSocket address;
  private String username;
  private int portNumber = 21638;

  Users(java.awt.List u, Notifications n, String userName) {
    users = u;
    notifications = n;
    username = userName;
  }

  /*
   * Runnable method - required.
   * Control plane messages - discovery of other users.
   */
  @Override
  public void run()
  {
    while (true) { // forever

      try {
        InetAddress group = InetAddress.getByName("239.0.84.134");
        address = new MulticastSocket(21638);
        address.joinGroup(group);
        String beacon = "[" + MessageCheckerCommon.timestamp() +"]";
        beacon += "[" + username +"]";
        beacon += "[online]";
        String currentAddress = InetAddress.getLocalHost().getHostName();
        beacon += "[" + currentAddress + "]";
        beacon += "[" + portNumber + "]";
        //System.out.println("It probably worked." + beacon);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Check the list of users
      ArrayList<String> checklist = MessageCheckerCommon.users_list();

      /*
      ** If any of the currently listed users are no longer on the checklist,
      ** they have now gone offline.
      */
      for (int u = 0; u < users.getItemCount(); ++u) {
        String s_u = users.getItem(u);
        boolean found = false;

        for (int c = 0; c < checklist.size(); ++c) {
          String s_c = checklist.get(c);
          if (s_u.equals(s_c)) {
            found = true;
            checklist.remove(c); // finished checking this one
            break;
          }
        }

        if (!found) { // user has gone offline
          notifications.notify(s_u + " - offline.");
          users.remove(u);
        }
      } // for (u < users.size())

      /*
      ** If the checklist contains users not on the list of current users,
      ** they must have just come online.
      */
      for (int c = 0; c < checklist.size(); ++c) {
        String s_c = checklist.get(c);
        notifications.notify(s_c + " - online.");
        users.add(s_c);
      }
      checklist.clear(); // not strictly necessary

      try { Thread.sleep(sleepTime); } // do not need to check constantly
      catch (InterruptedException e) { } // do not care

    } // while(true)

  } // run()

} // class Users
