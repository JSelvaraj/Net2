/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 *
 * This "back-end" is purely for demonstrating to the class the GUI that
 * will be used as the starting point, the kind of behaviour the application
 * is to have overall. This class will need to be taken out completely from
 * the code. Taking this class out and replacing it with one that instead
 * uses network communication might be a good starting point.
 */

import java.io.*;
import java.text.*;
import java.time.*;
import java.util.*;

public class MessageCheckerCommon
{
  public static final String tx_messages = "tx_messages.txt";
  public static final String rx_messages = "rx_messages.txt";
  public static final String users_list = "users_list.txt";

  public static String timestamp() {
    final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
    return s.format(new Date());
  }

  // for demo only -- write "message" to file
  public static void tx_message(String s)
  {
    try {
      FileWriter f = new FileWriter(tx_messages, true);
      f.write(s + "\n");
      f.flush();
      f.close();
    }
    catch (IOException e) {
      System.err.println("tx_message() : " + e.getMessage());
    }
  } // tx_message()

  // for demo only -- read "message" from file
  public static ArrayList<String> rx_messages()
  {
    ArrayList<String> messages = new ArrayList<String>();

    File f = new File(rx_messages);
    if (!f.exists()) { return messages; }

    try {
      BufferedReader r = new BufferedReader(new FileReader(rx_messages));
      String s;
      while ((s = r.readLine()) != null) { // multiple messages?
        s = s.trim();
        if (s.length() > 0) { messages.add(s); }
      }
      r.close();
      r = null; // remove possible reference to file
      f.delete();
      f = null; // remove possible reference to file
    }
    catch (IOException e) {
      System.err.println("rx_messages() : " + e.getMessage());
    }

    return messages;
  } // rx_messages()

  // for demo only -- "discover" users from file
  public static ArrayList<String> users_list()
  {
    ArrayList<String> users = new ArrayList<String>();

    File f = new File(users_list);
    if (!f.exists()) { return users; }

    try {
      BufferedReader u = new BufferedReader(new FileReader(users_list));
      String s;
      while ((s = u.readLine()) != null) {
        s = s.trim();
        if (s.length() > 0) {
          users.add(s);
        }
      }
      u.close();
    }
    catch (IOException e) {
      System.err.println("users_list() : " + e.getMessage());
      return null;
    }

    return users;
  } // users_list()

} // class MessageCheckerCommon
