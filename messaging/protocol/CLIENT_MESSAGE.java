/* 
 * Lauren LaGrone
 *
 * created: 9/29/2015
 *
 * CLIENT_MESSAGE.java: client instant message.
 *                      transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;

//Message layout provided in project specifications
public class CLIENT_MESSAGE {
    String sender_id;
    int sender_id_size;
    String message;
    int message_size;

    //for unmarshalling
    public CLIENT_MESSAGE(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        sender_id_size = data_input.readInt();
        byte[] sender_bytes = new byte[sender_id_size];
        data_input.readFully(sender_bytes);
        sender_id = new String(sender_bytes);

        message_size = data_input.readInt();
        byte[] message_bytes = new byte[message_size];
        data_input.readFully(message_bytes);
        message = new String(message_bytes);

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public CLIENT_MESSAGE(String sender_id, String message){
        this.sender_id = sender_id;
        byte[] sender = this.sender_id.getBytes();
        sender_id_size = sender.length;

        this.message = message;
        byte[] message_byte = this.message.getBytes();
        message_size = message_byte.length;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(sender_id_size);
        byte[] sender_bytes = sender_id.getBytes();
        data_output.write(sender_bytes, 0, sender_bytes.length);

        data_output.writeInt(message_size);
        byte[] message_bytes = message.getBytes();
        data_output.write(message_bytes, 0, message_bytes.length);

        data_output.flush();
        byte_output.close();
        data_output.close();
        marshalledBytes = byte_output.toByteArray();
        return marshalledBytes;
    }

    public String getSender(){
        return sender_id;
    }

    public String getMessage(){
        return message;
    }
}
