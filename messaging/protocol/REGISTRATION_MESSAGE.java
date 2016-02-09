/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * REGISTRATION_MESSAGE.java: client registratoin message.
 *                            transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;


//Message layout provided in project specifications
public class REGISTRATION_MESSAGE {
    private int size_of_id;
    private String user_id;
    private int size_of_ip;
    private byte[] ip_address;
    private int port;

    //for unmarshalling
    public REGISTRATION_MESSAGE(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byte_input = new ByteArrayInputStream(marshalledBytes);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        size_of_id = data_input.readInt();
        byte[] user_id_bytes = new byte[size_of_id];
        data_input.readFully(user_id_bytes);
        user_id = new String(user_id_bytes);

        size_of_ip = data_input.readInt();
        byte[] ip_bytes = new byte[size_of_ip];
        data_input.readFully(ip_bytes);
        ip_address = ip_bytes;

        port = data_input.readInt();

        byte_input.close();
        data_input.close();
    }

    //manual instantiation
    public REGISTRATION_MESSAGE(String user_id, byte[] ip_address, int port){
        this.user_id = user_id;
        byte[] id = user_id.getBytes();
        size_of_id = id.length;

        this.ip_address = ip_address;
        size_of_ip = ip_address.length;

        this.port = port;

    }

    //marshalling for send
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(size_of_id);
        byte[] userToBytes = user_id.getBytes();
        data_output.write(userToBytes, 0, size_of_id);

        data_output.writeInt(size_of_ip);
        data_output.write(ip_address, 0, size_of_ip);

        data_output.writeInt(port);

        data_output.flush();
        byte_output.close();
        data_output.close();


        marshalledBytes = byte_output.toByteArray();

        return marshalledBytes;
    }


    public User makeUser(){
        return new User(user_id, ip_address, port);
    }
}
