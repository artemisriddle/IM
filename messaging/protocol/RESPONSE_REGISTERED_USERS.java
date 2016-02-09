/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * RESPONSE_REGISTERED_USERS.java: returns a list of all connected users.
 *                                 transfers data from client to server.
 */

package messaging.protocol;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.IOException;

//Message layout provided in project specifications
public class RESPONSE_REGISTERED_USERS {
    int num_users;
    ArrayList<User> user_list;

    //for unmarshalling
    public RESPONSE_REGISTERED_USERS(byte[] userList) throws IOException{
        ByteArrayInputStream byte_input = new ByteArrayInputStream(userList);
        DataInputStream data_input = new DataInputStream(new BufferedInputStream(byte_input));

        num_users = data_input.readInt();
        user_list = new ArrayList<>(num_users);

        for(int i = 0; i < num_users; i++){
            int name_length = data_input.readInt();
            byte[] name_byte = new byte[name_length];
            data_input.readFully(name_byte);
            String name = new String(name_byte);

            int inet_length = data_input.readInt();
            byte[] inet_byte = new byte[inet_length];
            data_input.readFully(inet_byte);

            int port = data_input.readInt();

            User addition = new User(name, inet_byte, port);
            user_list.add(addition);
        }

        byte_input.close();
        data_input.close();

    }

    //manual instantiation
    public RESPONSE_REGISTERED_USERS(ArrayList<User> user_list){
        num_users = user_list.size();
        this.user_list = user_list;
    }

    //marshalling for send
    public byte[] getBytes() throws IOException{
        byte[] marshalledbytes = null;
        ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
        DataOutputStream data_output = new DataOutputStream(new BufferedOutputStream(byte_output));

        data_output.writeInt(num_users);

        for(int i = 0; i < num_users; i++){
            User current = user_list.get(i);

            String name = current.getID();
            byte[] name_bytes = name.getBytes();
            data_output.writeInt(name_bytes.length);
            data_output.write(name_bytes, 0, name_bytes.length);

            byte[] inet = current.getInet();
            data_output.writeInt(inet.length);
            data_output.write(inet, 0, inet.length);

            int port = current.getPort();
            data_output.writeInt(port);
        }

        data_output.flush();
        byte_output.close();
        data_output.close();

        marshalledbytes = byte_output.toByteArray();
        return marshalledbytes;
    }

    public void print(){
        System.out.println("Current Users:");
        for(int i = 0; i < num_users; i++){
            System.out.println(user_list.get(i).getID());
        }
    }

    public ArrayList<User> getUsers(){
        return user_list;
    }
}
