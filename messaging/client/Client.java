/* 
 * Lauren LaGrone
 *
 * 9/26/1015
 *
 * Client.java: main interface for client; includes all navigation
 */


package messaging.client;

import messaging.protocol.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.net.InetAddress;
import java.util.Set;

public class Client {
    public ArrayList<User> user_list = new ArrayList<>();
    public Hashtable<String, ClientInteraction> connections = new Hashtable<>();
    public InetAddress client_inet;
    public String client_name;
    public int client_port;
    public String connect_name;

    public static void main(String args[]) throws IOException, UnknownHostException {

        Client c = new Client();

        c.client_name = args[0].substring(0, args[0].indexOf('@'));
        String string_machine = args[0].substring(args[0].indexOf('@') + 1, args[0].indexOf(':'));
        InetAddress server_inet = InetAddress.getByName(string_machine);
        int portNumber = Integer.parseInt(args[0].substring(args[0].indexOf(':') + 1, args[0].length()));

        c.client_inet = InetAddress.getLocalHost();

        //sets up thread to listen for incomming connections
        ServerSocket contact = new ServerSocket(0);
        c.client_port = contact.getLocalPort();

        ClientListener listener = new ClientListener(contact, c);
        listener.start();

        //if original connection was successful
        if (c.initialConnection(server_inet, portNumber)) {
            //user greeting
            System.out.println("\nWelcome!\n\"client-menu\" to see your options");
            String user_option;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            //amazing
            while (true) {
                user_option = br.readLine();

                //print menu
                if (user_option.equals("client-menu")) {
                    c.printMenu();
                }
                //deregister
                else if (user_option.equals("client-deregister")) {
                    c.deregistration(server_inet, portNumber);
                    contact.close();
                    return;
                }

                //get users
                else if (user_option.equals("client-query")) {
                    c.queryUsers(server_inet, portNumber);
                }

                //connect to client
                else if (user_option.contains("client-connect")) {
                    //find the username specified
                    String[] parse = user_option.split("\\s");
                    c.connect_name = parse[1];
                    if (parse.length == 1) {
                        System.out.println("Please try again");
                    } else {
                        c.connectClient(parse[1]);
                    }
                }

                else if (user_option.contains("client-disconnect")) {
                    String[] parse = user_option.split("\\s");
                    if(parse.length > 1)
                       c.disconnectClient(parse[1], false);
                }

                else if (user_option.contains("@ALL")){
                    String user_message = user_option.substring(user_option.indexOf(' ') + 1);

                    Set<String> keys = c.connections.keySet();
                    for(String key: keys){
                        c.messageUser(key,user_message);
                    }
                }

                else if (user_option.contains("@")){
                    String user_info = user_option.substring(user_option.indexOf('@') + 1, user_option.indexOf(' '));
                    String user_message = user_option.substring(user_option.indexOf(' ') + 1);
                    if(c.connections.containsKey(user_info)){
                        c.messageUser(user_info,user_message);
                    }else{
                        System.out.println("You have not connected to this user.");
                    }
                }

                else if (user_option.equals("see-connections")) {
                    System.out.println(c.connections.toString());
                }
            }
        }
        else {
            contact.close();
        }
    }

    //runs on startup
    public boolean initialConnection(InetAddress machine, int portNumber) throws IOException {
        //makes temporary connection to server
        Socket server_connect = new Socket(machine, portNumber);
        TCPSender temp_send = new TCPSender(server_connect);
        TCPReceiver temp_receive = new TCPReceiver(server_connect);

        //registration
        REGISTRATION_MESSAGE rm = new REGISTRATION_MESSAGE(client_name, client_inet.getAddress(), client_port);
        temp_send.sendData(rm.getBytes(), 3);
        temp_receive.run();
        REGISTRATION_RESPONSE response = new REGISTRATION_RESPONSE(temp_receive.getMessage());
        System.out.println(response.getMessage());

        //if registration worked, print users and update userlist
        if (response.getSuccess()) {
            REQUEST_REGISTERED_USERS ru = new REQUEST_REGISTERED_USERS(client_name);
            temp_send.sendData(ru.getBytes(), 7);
            temp_receive.run();
            RESPONSE_REGISTERED_USERS rru = new RESPONSE_REGISTERED_USERS(temp_receive.getMessage());
            rru.print();
            user_list = rru.getUsers();
        }

        server_connect.close();

        //return status
        return response.getSuccess();
    }

    public void deregistration(InetAddress machine, int portNumber) throws IOException {

        //temporary connection to server
        Socket server_connect = new Socket(machine, portNumber);
        TCPSender temp_send = new TCPSender(server_connect);
        TCPReceiver temp_receive = new TCPReceiver(server_connect);

        //deregister
        DEREGISTRATION_MESSAGE de = new DEREGISTRATION_MESSAGE(client_name, client_inet.getAddress(), client_port);
        byte[] bleh = de.getBytes();
        temp_send.sendData(bleh, 5);
        temp_receive.run();
        REGISTRATION_RESPONSE response = new REGISTRATION_RESPONSE(temp_receive.getMessage());
        System.out.println(response.getMessage());

        //server_connect.close();

        Set<String> keys = connections.keySet();
        for(String key: keys){
            disconnectClient(key, true);
        }
    }

    public void queryUsers(InetAddress machine, int portNumber) throws IOException {
        //temporary connection
        Socket server_connect = new Socket(machine, portNumber);
        TCPSender temp_send = new TCPSender(server_connect);
        TCPReceiver temp_receive = new TCPReceiver(server_connect);

        //query users
        REQUEST_REGISTERED_USERS request = new REQUEST_REGISTERED_USERS(client_name);
        temp_send.sendData(request.getBytes(), 7);
        temp_receive.run();
        RESPONSE_REGISTERED_USERS rru = new RESPONSE_REGISTERED_USERS(temp_receive.getMessage());
        rru.print();
        //update userlist
        user_list = rru.getUsers();
    }

    public void connectClient(String connect_name) throws IOException {
        //find the user requested
        byte[] no = new byte[0];
        User temp = new User("", no, 0);
        boolean found = false;
        for (int i = 0; i < user_list.size(); i++) {
            if (user_list.get(i).getID().equals(connect_name)) {
                temp = user_list.get(i);
                found = true;
            }
        }

        //connect
        if (found) {
            connections.put(connect_name, new ClientInteraction(temp, client_name, this));
            connections.get(connect_name).start();
        }
        //error
        else
            System.out.println("User not found");
    }

    public void disconnectClient(String name, boolean deregister) throws IOException {
        String message = "This user has disconnected";

        if(!deregister) {
            System.out.println("Would you like to leave a goodbye message? y/n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String response = br.readLine();
            if (response.equals("y")) {
                System.out.print(">>");
                message = br.readLine();
            }
        }
        CLIENT_GOODBYE goodbye = new CLIENT_GOODBYE(message);
        connections.get(name).sender.sendData(goodbye.getBytes(), 12);
        connections.get(name).sock = null;
        connections.get(name).receiver = null;
        connections.remove(name);
    }

    public void messageUser (String name, String message) throws IOException{
        CLIENT_MESSAGE mail = new CLIENT_MESSAGE(client_name, message);
        connections.get(name).sender.sendData(mail.getBytes(), 11);
    }

    public void printMenu() {
        //print the user menu
        System.out.println("client-menu: display this menu");
        System.out.println("client-deregister: remove yourself from the contact list");
        System.out.println("client-query: see list of connected users");
        System.out.println("see-connections: see who you are talking to");
        System.out.println("client-connect<username>: connect to a registered user");
        System.out.println("client-disconnect<username>: disconnect from a registered user");
        System.out.println("@<username>: send a message to a connected user");
        System.out.println("@ALL: send a message to all connected users");
    }

    public String toString() {
        return "name: " + client_name + " port " + client_port;
    }

}
