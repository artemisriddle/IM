/*
 * Lauren LaGrone
 *
 * created: 9/29/2015
 *
 * CLIENT_REQUEST_CONNECT.java: standard contact request.
 *                              transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;

//Message format provided in project specifications
public class CLIENT_REQUEST_CONNECT {
    int id_size;
    String user_id;

    //for unmarshalling
    public CLIENT_REQUEST_CONNECT(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        id_size = data_input.readInt();
        byte[] user_bytes = new byte[id_size];
        data_input.readFully(user_bytes);
        user_id = new String(user_bytes);

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public CLIENT_REQUEST_CONNECT(String user_id){
        this.user_id = user_id;
        byte[] temp = this.user_id.getBytes();
        id_size = temp.length;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException{
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(id_size);
        byte[] user_bytes = user_id.getBytes();
        data_output.write(user_bytes, 0, user_bytes.length);

        data_output.flush();
        byte_output.close();
        data_output.close();
        marshalledBytes = byte_output.toByteArray();
        return marshalledBytes;
    }

    //for easy access to the userID
    public String getUser_id(){
        return user_id;
    }
}
