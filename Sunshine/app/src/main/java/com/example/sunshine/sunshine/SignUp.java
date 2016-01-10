package com.example.sunshine.sunshine;

import android.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    String value_username,value_password,value_re_password,value_email;
    Button submit,btnLogin;
    EditText uname,password,re_password,email;
    //String uname,password,email;
    // Progress Dialog
    private ProgressDialog pDialog;
    // url to create new product
    private static String url_create_product = "http://10.10.21.86/Sunshine/create_user.php";
    private static String TAG_SUCCESS = "success";

    JSONParser jsonParser= new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submit = (Button) findViewById(R.id.btnSignup);
        btnLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        uname = (EditText) findViewById(R.id.uname);
        password = (EditText) findViewById(R.id.password);
        re_password = (EditText) findViewById(R.id.re_password);
        email = (EditText) findViewById(R.id.email);

        submit.setOnClickListener(this);

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:
                //register(v);
                new CreateNewProduct().execute(url_create_product);
                break;
            case R.id.btnLinkToLoginScreen:
                startActivity(new Intent(this,DisplayMessageActivity.class));
                break;
        }
    }
    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUp.this);
            pDialog.setMessage("Creating User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


            value_username = uname.getText().toString();
            value_password = password.getText().toString();
            value_email = email.getText().toString();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("value_username", value_username);
            params.put("value_password", value_password);
            params.put("value_email", value_email);

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_create_product,"POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), DisplayMessageActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUp.this);
                    dialogBuilder.setMessage("Something went wrong :( ");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
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
        }
    }
}