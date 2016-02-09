/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * TCPReceiver.java: waits for messages to come in.
 */

package messaging.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class TCPReceiver extends Thread{
    private Socket sock;
    private DataInputStream data_input;
    private int message_type;
    private byte[] byte_message;

    public TCPReceiver(Socket sock) throws IOException {
        this.sock = sock;
        data_input = new DataInputStream(sock.getInputStream());
    }

    @Override
    public void run() {
        super.run();
        int data_length;

        while (sock != null) {
            try {
                //reads the data and sorts it into pretty boxes
                data_length = data_input.readInt();
                message_type = data_input.readInt();
                byte_message = new byte[data_length];
                data_input.readFully(byte_message);
                break;
        }catch(SocketException se){
            //       System.out.println(se.getMessage());
            break;
        }catch(IOException ioe){
            // System.out.println(ioe.getMessage());
            break;
        }
    }
}

    //stores message type (I love it)
    public int getMessageType(){
        return message_type;
    }

    //quick access to message received (I love this one more)
    public byte[] getMessage(){
        return byte_message;
    }
}
