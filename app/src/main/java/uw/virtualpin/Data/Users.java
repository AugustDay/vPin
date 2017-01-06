package uw.virtualpin.Data;

/**
 * Created by shawn on 11/8/2016.
 */

public class Users {

    public String mUsername;
    public String mPassword;
    public String mFirstName;
    public String mLastName;
    public String mEmail;

    public static final String USERNAME = "username", PASSWORD = "password",
            FIRSTNAME = "firstname", LAStNAME = "lastname", EMAIL ="email";



    public Users(String password, String username){
        this.mUsername = username;
        this.mPassword = password;
    }

    /**
     * constructor of user
     * @param username
     * @param password
     */
    public Users(String username, String password, String firstName, String lastName, String email){
        this.mUsername = username;
        this.mPassword = password;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mEmail = email;
    }

    /**
     * get username
     * @return String username
     */
    public String getUsername() {
        return mUsername;
    }
    /**
     * get passowrd
     * @return String password
     */
    public String getPassword(){
        return mPassword;
    }
    /**
     * get firstName
     * @return String firstName
     */
    String getFirstName(){
        return mFirstName;
    }
    /**
     * get lastName
     * @return String lastName
     */
    String getLastName(){ return mLastName;  }
    /**
     * get email
     * @return String email
     */
    String getEmail(){ return mEmail;  }
}