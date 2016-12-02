package uw.virtualpin;

/**
 * Created by shawn on 11/8/2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


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
    String getUsername () {
        return mUsername;
    }
    /**
     * get passowrd
     * @return String password
     */
    String getPassword(){
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



    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param usersJSON
     * @return reason or null if successful.
     */
    public static String parseUsersJSON(String usersJSON, List<Users> usersList) {
        String reason = null;
        if (usersJSON != null) {
            try {
                JSONArray arr = new JSONArray(usersJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Users CurUser = new Users(obj.getString(Users.PASSWORD), obj.getString(Users.USERNAME));
                    usersList.add(CurUser);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    /**
     * Created by Tyler on 11/20/2016.
     */

    public static class ImageManager {
    }
}