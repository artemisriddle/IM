/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * REQUEST_REGISTERD_USERS.java: query for all users currently connected.
 *                               transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;

//Message layout provided in project specifications
public class REQUEST_REGISTERED_USERS {
    int user_id_size;
    String user_id;

    //for unmarshalling
    public REQUEST_REGISTERED_USERS(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        user_id_size = data_input.readInt();
        byte[] user_id_bytes = new byte[user_id_size];
        data_input.readFully(user_id_bytes);
        user_id = new String(user_id_bytes);

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public REQUEST_REGISTERED_USERS(String user_id){
        this.user_id = user_id;
        byte[] temp = user_id.getBytes();
        user_id_size = temp.length;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(user_id_size);
        byte[] user_id_toBytes = user_id.getBytes();
        data_output.write(user_id_toBytes, 0, user_id_size);

        data_output.flush();
        byte_output.close();
        data_output.close();

        marshalledBytes = byte_output.toByteArray();
        return marshalledBytes;
    }
}
