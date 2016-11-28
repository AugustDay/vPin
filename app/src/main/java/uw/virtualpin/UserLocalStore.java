package uw.virtualpin;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.View;

/**
 * Created by shawn on 11/22/2016.
 */

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }


    public void storeUserData(Users user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("username", user.getUsername());
        spEditor.putString("email", user.getEmail());
        spEditor.putString("password", user.getPassword());
        spEditor.putString("firstName", user.getEmail());
        spEditor.putString("lasName", user.getLastName());
        spEditor.commit();
    }

    public Users getLoggedinUser(){
        String username = userLocalDatabase.getString("username", "");
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String firstName = userLocalDatabase.getString("firstNa me", "");
        String lastName = userLocalDatabase.getString("lastName", "");

        Users storedUser = new Users(username, password, firstName, lastName, email);

        return storedUser;
    }

    public void setUsersLoggedin(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if (userLocalDatabase.getBoolean("LoggedIn", false)){
            return true;
        }
        else
        {
            return false;
        }
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
