/* 
 * Lauren LaGrone
 *
 * created: 9/28/2015
 *
 * ClientInteraction.java: thread for client.
 *                         holds each connection to the client.
 */

package messaging.protocol;

import messaging.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;

 /* Thread Interaction for client
  * Does basic i/o, but does not idle for accept new connections
  */
public class ClientInteraction extends Thread {
    public TCPReceiver receiver;
    public TCPSender sender;
    public Client cli;
    private String client_name;
    private User contact;
    public Socket sock;
    private String connection_name;
    //determines whether initializing a conversation
    private boolean initialize;
    //for adding a new connection to the table
    private boolean accepted = false;

    //if the conversation is started by someone else
    public ClientInteraction(Socket sock, Client c) throws IOException {
        this.cli = c;
        this.sock = sock;
        receiver = new TCPReceiver(sock);
        sender = new TCPSender(sock);
        client_name = cli.client_name;
    }

    //if the conversation is started by the creator of the interaction
    public ClientInteraction(User contact, String my_name, Client c) throws IOException{
        this.initialize = true;
        this.contact = contact;
        connection_name = contact.getID();
        cli = c;
        client_name = my_name;
        sock = new Socket(InetAddress.getByAddress(contact.getInet()), contact.getPort());
        receiver = new TCPReceiver(sock);
        sender = new TCPSender(sock);
    }

    @Override
    public void run() {
        super.run();

        try {
            //request connection if connection is not already made
            if(initialize) {
                CLIENT_REQUEST_CONNECT request = new CLIENT_REQUEST_CONNECT(client_name);
                sender.sendData(request.getBytes(), 9);
                receiver.run();
                CLIENT_CONNECT_RESPONSE response = new CLIENT_CONNECT_RESPONSE(receiver.getMessage());
                //if connection is rejected, return
                if(!response.getSuccess()){
                    System.out.println(response.getMessage());
                    cli.connections.remove(contact.getID());
                    return;
                }else {
                    //cli.connections.put(contact.getID(), this);
                    System.out.println("added");
                }
            }

            //enter interaction loop
            while(sock != null){

                    // listen
                    receiver.run();

                if(receiver != null){
                    //receive a message
                    switch (receiver.getMessageType()) {
                        case 9:
                            CLIENT_REQUEST_CONNECT request = new CLIENT_REQUEST_CONNECT(receiver.getMessage());
                            connection_name = request.getUser_id();
                            System.out.println(request.getUser_id() + " wants to connect!!");
                            System.out.println("Do you accept? yes/no");
                            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                            String choice = br.readLine();
                            if (choice.equals("yes")) {
                                System.out.println(request.getUser_id() + " added.");
                                CLIENT_CONNECT_RESPONSE response = new CLIENT_CONNECT_RESPONSE(true, "");
                                accepted = true;
                                sender.sendData(response.getBytes(), 10);
                                cli.connections.put(request.getUser_id(), this);
                            } else if (choice.equals("no")) {
                                System.out.println("Why not?");
                                String reason = br.readLine();
                                CLIENT_CONNECT_RESPONSE response = new CLIENT_CONNECT_RESPONSE(false, reason);
                                sender.sendData(response.getBytes(), 10);
                            }
                            break;
                        case 11:
                            CLIENT_MESSAGE message = new CLIENT_MESSAGE(receiver.getMessage());
                            System.out.println(message.getSender() + ": " + message.getMessage());
                            break;
                        case 12:
                            CLIENT_GOODBYE bye_bye = new CLIENT_GOODBYE(receiver.getMessage());
                            System.out.println(bye_bye.getMessage());
                            sock.close();
                            cli.connections.remove(connection_name);
                            System.out.println("Conversation closed");
                            return;
                    }
                }
                else
                    return;
            }
        } catch (IOException ioe) {
           // System.out.println(ioe.getMessage());
            return;
        }
    }

    public String toString(){
        return client_name;
    }
}
