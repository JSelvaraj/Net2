/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

public class Main {

  public static void main(String[] args)
  {
    String id;

    if (args.length == 1) { id = args[0]; }
    else { id = System.getProperty("user.name"); }

    System.out.println("Using id: " + id);

    MessageCheckerGUI m = new MessageCheckerGUI(id);
    m.setVisible(true); // Make GUI visible.
  }
}
