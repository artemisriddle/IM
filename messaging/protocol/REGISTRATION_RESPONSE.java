/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * REGISTRATION_RESPONSE.java: server confirmation of user registration.
 *                             transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;

//Message layout provided in project specifications
public class REGISTRATION_RESPONSE {
    private boolean success;
    private int message_size;
    private String message;

    //for unmarshalling
    public REGISTRATION_RESPONSE(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        success = data_input.readBoolean();

        message_size = data_input.readInt();
        byte[] message_bytes = new byte[message_size];
        data_input.readFully(message_bytes);
        message = new String(message_bytes);

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public REGISTRATION_RESPONSE(boolean success, String message){
        this.success = success;

        this.message = message;
        byte[] message_bytes = message.getBytes();
        message_size = message_bytes.length;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeBoolean(success);

        data_output.writeInt(message_size);
        byte[] message_bytes = message.getBytes();
        data_output.write(message_bytes, 0, message_size);

        data_output.flush();
        byte_output.close();
        data_output.close();

        marshalledBytes = byte_output.toByteArray();
        return marshalledBytes;
    }

    public boolean getSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }
}
