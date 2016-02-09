/* 
 * Lauren LaGrone
 *
 * created: 9/26/2015
 *
 * User.java: basic User class to store contact information.
 */

package messaging.protocol;

public class User {
    private String user_id;
    private byte[] inet_ad;
    private int port;

    public User(String user_id, byte[] inet_ad, int port){
        this.user_id = user_id;
        this.inet_ad = inet_ad;
        this.port = port;
    }

    public String getID(){
        return user_id;
    }

    public byte[] getInet(){
        return inet_ad;
    }

    public int getPort(){
        return port;
    }
}
