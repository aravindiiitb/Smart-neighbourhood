package com.example.sunshine.sunshine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class DisplayMessageActivity extends AppCompatActivity implements View.OnClickListener {

    Button login,btnRegister;
    EditText uname,password;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String value_uname, value_pass;
    UserLocalStore userLocalStore;

    private static String url_check_user = "http://10.10.21.86/Sunshine/check_user_details.php";
    private static String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        login = (Button) findViewById(R.id.button);
        btnRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        uname = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        userLocalStore = new UserLocalStore(this);
        login.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                //authenticate();
                new CheckUserDetails().execute(url_check_user);
                break;
            case R.id.btnLinkToRegisterScreen:
                startActivity(new Intent(this,SignUp.class));
                break;
        }
    }

    class CheckUserDetails extends AsyncTask<String, String,String> {

        Boolean noUser = false;
        Boolean notVerified = false;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayMessageActivity.this);
            pDialog.setMessage("Checking Details ..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            value_uname = uname.getText().toString();
            value_pass = password.getText().toString();
        }

        /**
         * Checking user
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("value_username", value_uname);

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_check_user,
                        "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    JSONArray userObj = json.getJSONArray("user"); // JSON Array
                    // get first product object from JSON Array
                    JSONObject user = userObj.getJSONObject(0);
                    if(user != null) {
                        String rcvd_username = user.getString("username");
                        String rcvd_password = user.getString("password");

                        if (rcvd_password.equals(value_pass) && rcvd_username.equals(value_uname)) {
                            ModelUser returnedUser = new ModelUser();
                            returnedUser.id = user.getInt("id");
                            returnedUser.username = user.getString("username");
                            returnedUser.password = user.getString("password");
                            returnedUser.email = user.getString("email");
                            returnedUser.is_admin = user.getBoolean("is_admin");
                            returnedUser.occupation = user.getString("occupation");
                            returnedUser.is_verified = user.getInt("is_verified");

                            if(returnedUser.is_verified == 1){
                                userLocalStore.storeUserData(returnedUser);
                                userLocalStore.setUserLoggedIn(true);

                                Intent i = new Intent(getApplicationContext(), UserProfile.class);
                                startActivity(i);
                                // closing this screen
                                finish();
                            }
                            else{
                                notVerified = true;
                            }
                        } else {
                            // failed to Authenticate
                            noUser = true;
                        }
                    }

                } else {
                    // failed to Authenticate
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(noUser){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DisplayMessageActivity.this);
                dialogBuilder.setMessage("No user exist with that username");
                dialogBuilder.setPositiveButton("Ok", null);
                dialogBuilder.show();
            }
            if(notVerified){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DisplayMessageActivity.this);
                dialogBuilder.setMessage("Your details are yet to be verified by admin !!");
                dialogBuilder.setPositiveButton("Ok", null);
                dialogBuilder.show();
            }
        }
    }
}
