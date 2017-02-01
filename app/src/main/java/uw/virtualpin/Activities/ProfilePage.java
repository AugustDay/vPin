package uw.virtualpin.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import uw.virtualpin.Data.CurrentUser;
import uw.virtualpin.Data.UserLocalStore;
import uw.virtualpin.HelperClasses.ImageManager;
import uw.virtualpin.R;

/*

Authors: Tyler Brent, Shawn M.

This class is responsible for generating the profile page.
The avatar of the user, the email address, full name, username, and number of pins posted is displayed here.
The user can change their avatar by clicking the image.

 */

public class ProfilePage extends AppCompatActivity {

    private static final String UPDATE_AVATAR_URL = "http://cssgate.insttech.washington.edu/~adi1996/info.php?cmd=update_avatar&id=";
    private static final String GET_AVATAR_URL = "http://cssgate.insttech.washington.edu/~adi1996/info.php?cmd=get_userdata&username=";
    private static final String GET_NUMPOSTS_URL = "http://cssgate.insttech.washington.edu/~adi1996/info.php?cmd=pin_history_count&username=";
    UserLocalStore userLocalStore;
    String username;
    String email;
    int postCount;
    String userFullName;
    private ImageView imageView;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView usernameText;
    private TextView emailText;
    private TextView numPostsText;
    private TextView fullNameText;
    private String userid;
    private String newImage;

    /**
     * Overloaded on create to initialize variables and call the async task.
     *
     * @param savedInstanceState the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        usernameText = (TextView) findViewById(R.id.user_profile_username);
        emailText= (TextView) findViewById(R.id.user_profile_email);
        numPostsText = (TextView) findViewById(R.id.user_profile_postCount);
        fullNameText = (TextView) findViewById(R.id.user_profile_name);
        imageView = (ImageView) findViewById(R.id.header_cover_image);
        newImage = "NO_IMAGE";
        userLocalStore = new UserLocalStore(this);

        CurrentUser currentUser = new CurrentUser();
        username = currentUser.username;

        usernameText.setTextSize(16);
        emailText.setTextSize(16);
        fullNameText.setTextSize(16);
        numPostsText.setTextSize(16);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AvatarAsyncTask getAvatarAsyncTask = new AvatarAsyncTask();
        getAvatarAsyncTask.execute(GET_AVATAR_URL + username);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InboxActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }

    /**
     * Opens the phone gallery.
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    /**
     * Gets the resulting image selected from the phone gallery.
     *
     * @param requestCode the request code.
     * @param resultCode the result code.
     * @param data the data.
     */
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ImageManager imageManager = new ImageManager();
            newImage = imageManager.convertBitmapToByteArray(image);

            AvatarAsyncTask updateAvatarAsyncTask = new AvatarAsyncTask();
            updateAvatarAsyncTask.execute(UPDATE_AVATAR_URL + userid);

        }
    }

    /**
     * Parses the json string given by the async task.
     *
     * @param jsonString the json string.
     */
    private void parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            userid = jsonObject.getString("idUser");
            usernameText.setText("Username: " + jsonObject.getString("username"));
            emailText.setText("Email: " + jsonObject.getString("email"));

            fullNameText.setText(jsonObject.getString("Fname") + " "
                + jsonObject.getString("Lname"));

            ImageManager imageManager = new ImageManager();
            Log.e("IMAGE", jsonObject.getString("userImage"));
            Bitmap image = imageManager.convertEncodedImageToBitmap(jsonObject.getString("userImage"));
            imageView.setImageBitmap(image);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * The async task to get the avatar of the user.
     */
    private class AvatarAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {

                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    if(!newImage.equalsIgnoreCase("NO_IMAGE")) {

                        urlConnection.setDoOutput(true);
                        String data = URLEncoder.encode("image", "UTF-8")
                                + "=" + URLEncoder.encode(newImage, "UTF-8");

                        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(data);
                        wr.flush();
                    }

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to complete your request, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            parseJson(result);
            NumPostsAsyncTask numPostsAsyncTask = new NumPostsAsyncTask();
            numPostsAsyncTask.execute(GET_NUMPOSTS_URL + username);
        }
    }

    /**
     * The async task to get the total number of posts the user has made.
     */
    private class NumPostsAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {

                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    if (!newImage.equalsIgnoreCase("NO_IMAGE")) {

                        urlConnection.setDoOutput(true);
                        String data = URLEncoder.encode("image", "UTF-8")
                                + "=" + URLEncoder.encode(newImage, "UTF-8");

                        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                        wr.write(data);
                        wr.flush();
                    }

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to complete your request, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                numPostsText.setText("Number of Posts: " + jsonObject.getString("count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
