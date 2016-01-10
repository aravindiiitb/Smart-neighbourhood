package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dell pc on 06/01/2016.
 */
public class AccountSettingsFragment extends Fragment {

    public AccountSettingsFragment () {}

    Button update;
    EditText username,email,password;
    private static String url_update_details = "http://10.10.21.86/Sunshine/update_user_details.php";
    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser= new JSONParser();

    private ProgressDialog pDialog;

    ModelUser userDetails = new ModelUser();

    String value_username ;
    String value_email ;
    String value_password ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        update = (Button) view.findViewById(R.id.update);

        username = (EditText) view.findViewById(R.id.username);
        email = (EditText) view.findViewById(R.id.email_id);
        password = (EditText) view.findViewById(R.id.password);

        UserLocalStore userLocalStore= new UserLocalStore(getActivity());
        userDetails = userLocalStore.getLoggedInUser();


        username.setText(userDetails.username);
        email.setText(userDetails.email);
        password.setText(userDetails.password);



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateDetails().execute(url_update_details);
            }
        });
    }

    class UpdateDetails extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Updating Details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            value_username = username.getText().toString();
            value_email = email.getText().toString();
            value_password = password.getText().toString();

        }


        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();

            params.put("value_username", value_username);
            params.put("value_email", value_email);
            params.put("value_password", value_password);
            params.put("value_userid", String.valueOf(userDetails.id));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_update_details, "POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    // closing this screen
                } else {
                    // failed to create product

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void showMessage(String title,String Message){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(Message);
            builder.setPositiveButton("Ok", null);		//Adde this line Aravind
            builder.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            showMessage("Updated", "Your details are successfully updated. Please logout and login again !!");
        }
    }

}
