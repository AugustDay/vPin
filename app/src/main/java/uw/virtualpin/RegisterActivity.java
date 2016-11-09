package uw.virtualpin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class RegisterActivity extends AppCompatActivity {

    private final static String COURSE_ADD_URL
            = "http://cssgate.insttech.washington.edu/~_450team8/info.php?cmd=register_user";
    private EditText editText_username;
    private EditText editText_password;

    private EditText editText_email;

    private Button button_register;

    /**
     * create activity and perform register function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);
        editText_email = (EditText) findViewById(R.id.editText_email);

        button_register = (Button) findViewById(R.id.button_register);
        final Button button_backToLogin = (Button) findViewById(R.id.button_backToLogin);

        button_register.setOnClickListener(new View.OnClickListener() {

            /**
             * restrict the user input. username has to be an email and password has to be at least 6 characters long
             * if the input is valid, start to download username and password
             * then check if it matched
             * @param v
             */
            @Override
            public void onClick(View v) {
                final String username = editText_username.getText().toString();
                final String password = editText_password.getText().toString();
                final String email = editText_email.getText().toString();


                //Checks for username
                if (TextUtils.isEmpty(username))  {
                    Toast.makeText(v.getContext(), "Enter username", Toast.LENGTH_SHORT).show();
                    editText_username.requestFocus();
                    return;
                }
                //Check for username length
                if (username.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter username of at least 6 characters", Toast.LENGTH_SHORT).show();
                    editText_password.requestFocus();
                    return;
                }

                //Checks that Email has an @ and . for emails
                if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    editText_email.requestFocus();
                    return;
                }
                //checks for password
                if (TextUtils.isEmpty(password))  {
                    Toast.makeText(v.getContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    editText_password.requestFocus();
                    return;
                }
                //Checks for password length
                if (password.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter password of at least 6 characters", Toast.LENGTH_SHORT).show();
                    editText_password.requestFocus();
                    return;
                }


                String url = buildRegisterURL(v);
                register(url);

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (button_backToLogin != null) {
            button_backToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * build the URL that can perform registration
     * @param v
     * @return the url to register
     */
    private String buildRegisterURL(View v) {
        StringBuilder sb = new StringBuilder(COURSE_ADD_URL);
        try {
            String username = editText_username.getText().toString();
            sb.append("&username=");
            sb.append(URLEncoder.encode(username, "UTF-8"));

            String password = editText_password.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(password, "UTF-8"));

            String email = editText_email.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(email,"UTF-8"));

            Log.i("111 Register", sb.toString());
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        /**
         * perform registration in the background
         * @param urls
         * @return success if registration is successful.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    Log.i("URL is: "+url, "URL");
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                        Log.i("123 String response: ",response);
                    }

                } catch (Exception e) {
                    response = "Unable to add users, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("registration");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Register successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to register: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * create AsyncTask object to register
     * @param url
     */
    public void register(String url){
        RegisterTask task = new RegisterTask();
        task.execute(new String[]{url.toString()});


        // Takes you back to the previous fragment by popping the current fragment out.
        //getSupportFragmentManager().popBackStackImmediate();

    }
}
