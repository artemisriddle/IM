/*
 * Lauren LaGrone
 *
 * 9/26/2015
 *
 * Server.java: manages all client connections
 */


package messaging.server;

import messaging.protocol.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.InetAddress;


public class Server {
    public ArrayList<User> user_list = new ArrayList<>();
    public ArrayList<ServerInteraction> connections = new ArrayList<>();

    public static void main(String args[]) throws IOException {
        ServerSocket server_socket = new ServerSocket(0);
        int port_number = server_socket.getLocalPort();
        InetAddress inet = server_socket.getInetAddress();
    	System.out.println(port_number);

        Server s = new Server();

        //I would make it look like it isn't an infinite loop, but sugarcoating feels like a lie
       while(true){
           s.connections.add(new ServerInteraction(server_socket.accept(), s));
           s.connections.get(s.connections.size() - 1).start();
        }


    }
}
