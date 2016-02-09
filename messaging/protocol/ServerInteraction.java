/* Lauren LaGrone
 *
 * created: 9/28/2015
 *
 * ServerInteraction.java: thread for server.
 *                         holds each connection with open socket.
 */

package messaging.protocol;

import messaging.server.Server;

import java.net.Socket;
import java.io.IOException;

public class ServerInteraction extends Thread{
    TCPReceiver receiver;
    TCPSender sender;
    Socket sock;
    Server serve;

    public ServerInteraction (Socket sock, Server serve) throws IOException{
        this.serve = serve;
        this.sock = sock;
        receiver = new TCPReceiver(sock);
        sender = new TCPSender(sock);

    }

    @Override
    public void run() {
        super.run();
        try {
            while (sock != null) {

                receiver.run();

                switch (receiver.getMessageType()) {
                    //received registration message
                    case 3:
                        REGISTRATION_MESSAGE register = new REGISTRATION_MESSAGE(receiver.getMessage());
                        User test = register.makeUser();
                        REGISTRATION_RESPONSE response;

                        boolean duplicate = false;

                        for(int i = 0; i < serve.user_list.size(); i++){
                            if(serve.user_list.get(i).getID().equals(test.getID())){
                                duplicate = true;
                            }
                        }

                        if(duplicate){
                            response = new REGISTRATION_RESPONSE(false, "This username is already in use");
                            sender.sendData(response.getBytes(), 4);
                        }
                        else {
                            serve.user_list.add(test);
                            response = new REGISTRATION_RESPONSE(true, ("You are now registered, " + serve.user_list.get(serve.user_list.size() - 1).getID()));
                            sender.sendData(response.getBytes(), 4);
                        }
                        break;
                    //received client-query
                    case 7:
                        REQUEST_REGISTERED_USERS request = new REQUEST_REGISTERED_USERS(receiver.getMessage());
                        RESPONSE_REGISTERED_USERS rru = new RESPONSE_REGISTERED_USERS(serve.user_list);
                        sender.sendData(rru.getBytes(), 7);
                        break;
                    //received deregistration message
                    case 5:
                        DEREGISTRATION_MESSAGE deregister = new DEREGISTRATION_MESSAGE(receiver.getMessage());
                        User user_out = deregister.makeUser();
                        REGISTRATION_RESPONSE de_response = new REGISTRATION_RESPONSE(false, "Something went wrong.");
                        for(int i = 0; i < serve.user_list.size(); i++) {
                            User rm = serve.user_list.get(i);
                            if (rm.getID().equals(user_out.getID()) && rm.getPort() == user_out.getPort()) {
                                serve.user_list.remove(i);
                                de_response = new REGISTRATION_RESPONSE(true, "You have been removed");
                            }
                        }
                        sender.sendData(de_response.getBytes(),4);
                        break;
                }
            }
            serve.connections.remove(this);
        } catch (IOException ioe){
            ioe.getMessage();
        }
    }
}
