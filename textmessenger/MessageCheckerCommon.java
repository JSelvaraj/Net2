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

  public static String timestamp() {
    final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
    return s.format(new Date());
  }


} // class MessageCheckerCommon
