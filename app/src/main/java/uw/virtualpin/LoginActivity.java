package uw.virtualpin;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=select*users";
    public Users mUsers;
    private SharedPreferences mSharedPreferences;
    public static int logInCount = 0;//use to fix rotate losing game states bug

    /**
     * create activity and perform log in function, saving the user information to shared preference
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        Boolean loggedinBoolean = mSharedPreferences.getBoolean("login" , false);
        if (loggedinBoolean){
            String currentUser = mSharedPreferences.getString(getString(R.string.SharedPreference_currentUser), "NoCurrentUser");
            Log.i("SP currentuser: ", currentUser);
        }

        final EditText editText_username = (EditText) findViewById(R.id.editText_email);
        final EditText editText_password = (EditText) findViewById(R.id.editText_password);
        final Button button_login = (Button) findViewById(R.id.button_login);
        final Button button_goToRegister = (Button) findViewById(R.id.button_goToRegister);


        if (loggedinBoolean){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            if (button_login != null) {
                button_login.setOnClickListener(new View.OnClickListener() {

                    /**
                     * restrict the user input. username has to be an email and password has to be at least 6 characters long
                     * if the input is valid, start to download username and password
                     * then check if it matched
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String username = editText_username.getText().toString();
                        String password = editText_password.getText().toString();

                        if (TextUtils.isEmpty(username))  {
                            Toast.makeText(v.getContext(), "Enter Username", Toast.LENGTH_SHORT).show();
                            editText_username.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(password))  {
                            Toast.makeText(v.getContext(), "Enter password", Toast.LENGTH_SHORT).show();
                            editText_password.requestFocus();
                            return;
                        }
                        if (password.length() < 6) {
                            Toast.makeText(v.getContext(), "Enter password of at least 6 characters", Toast.LENGTH_SHORT).show();
                            editText_password.requestFocus();
                            return;
                        }
                        mUsers = new Users(username, password);
                        //storeInSharedPreference(username, password);
                        new LoginTask().execute(new String[]{LOGIN_URL.toString()});

                    }
                });
                if (button_goToRegister != null) {
                    button_goToRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }
    }

//    /**test
//     * store the username and password to shared preference
//     * @param username
//     * @param shawnhpassword
//     */
//    private void storeInSharedPreference(String username, String password) {
////        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
////
////        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
////        if (networkInfo != null && networkInfo.isConnected()) {
////            //Check if the login and password are valid
////            //new LoginTask().execute(url);
//
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(getString(R.string.LOGIN_FILE), Context.MODE_PRIVATE));
//            outputStreamWriter.write("email = " + username + ";");
//            outputStreamWriter.write("password = " + password);
//            outputStreamWriter.close();
////            Toast.makeText(this,"Stored in File Successfully!", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//////        } else {
//////            Toast.makeText(this, "No network connection available. Cannot authenticate user",
//////                    Toast.LENGTH_SHORT).show();
//////            return;
////        }
//
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        String s = getString(R.string.LOGGEDIN);
//        String currentUser = getString(R.string.SharedPreference_currentUser);
//        editor.putBoolean(s, true);
//        editor.putString(currentUser, username);
//        editor.commit();
//    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        /**
         * perform downloading username and password in the background
         * @param urls
         * @return success if download is successful.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            Log.i("444 start: ", response);
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of users, Reason: ";
                    if (e.getMessage().startsWith("Unable to resolve host")){
                        response += "Could not contact remote server.  Check your internet connection.";
                    } else {
                        response += e.getMessage();
                    }

                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * check if download is successful
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Users> usersList = new ArrayList<Users>();
            result = Users.parseUsersJSON(result, usersList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            boolean found = false;
            if (!usersList.isEmpty()) {
                for (Users user : usersList){
                    if (user.getUsername().equals(mUsers.getUsername())
                            && user.getPassword().equals(mUsers.getPassword())){
                        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                        found = true;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("USERNAME", mUsers.getUsername());
                        startActivity(intent);
                        finish();
                    }
                }
            }
            if (!found){
                Toast.makeText(getApplicationContext(), "Login fail, try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
