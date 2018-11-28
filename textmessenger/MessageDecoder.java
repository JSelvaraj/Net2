import java.net.ServerSocket;
import java.util.regex.*;

public class MessageDecoder {

    private static final String TIMESTAMP= "\\[[0-9]{8}-[0-9]{6}.[0-9]{3}";

    private static final String USERNAME = "\\[[a-z0-9]+";

    private static final String TYPE = "\\[[a-zA-Z0-9]+";

    private static final String FQDN = "[\\[][a-zA-Z0-9._-]+";

    private static final String PORTNUMBER = "\\[[0-9]+";

    static private final String TEXTBODY = "[^\\[\\]]+";




    public static boolean isValidHeader(String message) {
        try {
            String[] messagebits = message.split("]");
            if (!messagebits[0].matches(TIMESTAMP) || !messagebits[1].matches(USERNAME) || !messagebits[2].matches(TYPE)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Message header bad format");
            return false;
        }
    }

    public static boolean validBeacon (String message) {
        if (!isValidHeader(message)) {
            return false;
        }
        try {
            String[] messagebits = message.split("]");
            if (!messagebits[3].matches(FQDN) || !messagebits[4].matches(PORTNUMBER)) {
                return false;
            } else if (Integer.parseInt(messagebits[4].substring(1)) < 1024 && Integer.parseInt(messagebits[4].substring(1)) > 65535) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validMessage (String message) {
        if (!isValidHeader(message)) {
            return false;
        }
        try {
            String[] messagebits = message.split("]");
            if (messagebits[3].matches(TEXTBODY)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

    }




}
