/* 
 * Lauren LaGrone
 *
 * created: 9/29/2015
 *
 * CLIENT_GOODBYE: close connection handshake.
 *                 transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;

//Message layout provided in project specifications
public class CLIENT_GOODBYE {
    int message_size;
    String message;

    //for unmarshalling
    public CLIENT_GOODBYE(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        message_size = data_input.readInt();
        byte[] message_bytes = new byte[message_size];
        data_input.readFully(message_bytes);
        message = new String(message_bytes);

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public CLIENT_GOODBYE(String message){
        this.message = message;
        byte[] temp = this.message.getBytes();
        message_size = temp.length;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(message_size);
        byte[] message_bytes = message.getBytes();
        data_output.write(message_bytes, 0, message_bytes.length);

        data_output.flush();
        byte_output.close();
        data_output.close();
        marshalledBytes = byte_output.toByteArray();
        return marshalledBytes;
    }

    public String getMessage(){
        return message;
    }
}
