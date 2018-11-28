/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

public class Users implements Runnable {

    private java.awt.List users; // java.util.List also exists!
    private Notifications notifications;

    private MulticastSocket multicastGroup;
    private String username;
    private int multicastPortNumber = 10101;
    private int yourPortNumber = 21638;
    private String groupIPaddress = "239.42.42.42";
    private LinkedList<String> messageQueue = new LinkedList<>();
    private Hashtable<String, String[][]> userDetails = new Hashtable<>();

    Users(java.awt.List u, Notifications n, String userName) {
        users = u;
        notifications = n;
        username = userName;
    }


    public int getPortNumber() {
        return yourPortNumber;
    }

    /*
     * Runnable method - required.
     * Control plane messages - discovery of other users.
     */
    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(groupIPaddress);
            multicastGroup = new MulticastSocket(multicastPortNumber);
            multicastGroup.joinGroup(group);
            multicastGroup.setLoopbackMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<String> userList = new LinkedList();

        while (true) {
            /**
             * This try catch block sends a beacon containing information about the where the user is hosting a connection.
             */
            try {
                InetAddress group = InetAddress.getByName(groupIPaddress);
                String beacon = "[" + MessageCheckerCommon.timestamp() + "]";
                beacon += "[" + username + "]";
                if (Messages.getOnlineStatus()) {
                    beacon += "[online]";
                } else {
                    beacon += "[offline]";
                }
                String currentAddress = InetAddress.getLocalHost().getHostName();
                beacon += "[" + currentAddress + "]";
                beacon += "[" + yourPortNumber + "]";
                DatagramPacket beaconPacket = new DatagramPacket(beacon.getBytes(), beacon.getBytes().length, group, multicastPortNumber);
                multicastGroup.send(beaconPacket);
                System.out.println(beacon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * For 5 seconds the program continually receives packets from the multicast group
             */
            long timer = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < timer) {
                try {
                    multicastGroup.setSoTimeout(10);
                    byte[] buffer = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, 1024);
                    multicastGroup.receive(datagramPacket);
                    buffer = datagramPacket.getData();
                    String message = new String(buffer, 0, datagramPacket.getLength());
                    if (!MessageDecoder.validBeacon(message)) {
                        System.out.println(message + "<-- bad format");
                        throw new InvalidMessageFormatException();
                    }
                    messageQueue.add(message);
                    System.out.println(message);
                } catch (SocketTimeoutException e) {
                } catch (InvalidMessageFormatException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /**
                 * This while loop goes through the message queue, then adds the
                 * details of the any users online to a hashtable for later access.
                 *
                 * the substring(1) call removes the leading "[" when the message is split.
                 */
                while (messageQueue.size() > 0) {
                    String[] messageParts = messageQueue.poll().split("]");
                    try {
                        if (messageParts[2].substring(1).equals("online") || messageParts[2].substring(1).equals("Online") ) {
                            String user = messageParts[1].substring(1);
                            userList.add(user);
                            String[][] tuple = new String[1][2];
                            String address = messageParts[3].substring(1);
                            String portnumber = messageParts[4].substring(1);
                            tuple[0][0] = address;
                            tuple[0][1] = portnumber;
                            userDetails.put(user, tuple);
                        }
                    } catch (Exception e) {
                        System.out.println("Wrong Format");
                    }
                }
            }
            checkCurrentUsers(userList);
        }

    } // run()

    private synchronized void checkCurrentUsers(LinkedList<String> userList) {
        for (int u = 0; u < users.getItemCount(); ++u) {
            String s_u = users.getItem(u);
            boolean found = false;

            for (int c = 0; c < userList.size(); ++c) {
                String s_c = userList.get(c);
                if (s_u.equals(s_c)) {
                    found = true;
                    userList.remove(c); // finished checking this one
                    break;
                }
            }

            if (!found) { // user has gone offline
                notifications.notify(s_u + " - offline.");
                users.remove(u);
                userDetails.remove(s_u); //Offline users are removed from the hashtable
            }
        } // for (u < users.size())

        /*
         ** If the checklist contains users not on the list of current users,
         ** they must have just come online.
         */
        for (int c = 0; c < userList.size(); ++c) {
            String s_c = userList.get(c);
            notifications.notify(s_c + " - online.");
            users.add(s_c);
        }
        userList.clear(); // not strictly necessary

    }

    public synchronized String getDestinationPortNumber(String userKey) {
        Object[][] tuple = userDetails.get(userKey);
        if (tuple != null) {
            return tuple[0][1].toString();
        } else return null;
    }

    public synchronized String getAddress (String userKey) {
        Object[][] tuple = userDetails.get(userKey);
        if (tuple != null) {
            return tuple[0][0].toString();
        } else return null;
    }

} // class Users
