/*
 * Lauren LaGrone
 *
 * created: 9/29/2015
 *
 * ClientListenerjava: open socket for client.
 *                     waits for connection and spawns off new interaction.
 */

package messaging.protocol;

import messaging.client.Client;

import java.io.IOException;
import java.net.ServerSocket;

public class ClientListener extends Thread {
    Client c;
    ServerSocket ss;

    public ClientListener(ServerSocket ss, Client c) {
        this.ss = ss;
        this.c = c;
    }

    @Override
    public void run() {
        super.run();

        try {

            while (true) {
                ClientInteraction new_connection = new ClientInteraction(ss.accept(), c);
                new_connection.start();
                //c.connections.put(new_connection.cli.connect_name, new_connection);
            }
        } catch (IOException ioe) {
            //System.out.println(ioe.getMessage());
            return;
        }
    }
}
