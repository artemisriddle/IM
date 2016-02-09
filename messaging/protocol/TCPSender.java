/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * TCPSender.java: sends data to connected client or server.
 */

package messaging.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
    //the socket we'll be using
    private Socket sock;

    //for socket output stream
    private DataOutputStream data_output;

    //public constructor
    public TCPSender(Socket sock) throws IOException {
        this.sock = sock;
        data_output = new DataOutputStream(sock.getOutputStream());
    }

    public void sendData (byte[] dataToSend, int message_id) throws IOException {
        int dataLength = dataToSend.length;

        data_output.writeInt(dataLength);

        data_output.writeInt(message_id);

        //send data to the output stream; Data, starting spot, length of array
        data_output.write(dataToSend,0,dataLength);

        //ensure that everything is pushed out of the output stream
        data_output.flush();
    }


}
