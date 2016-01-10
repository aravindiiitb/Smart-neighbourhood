package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dell pc on 28/12/2015.
 */
public class RequestServiceFragment extends Fragment {

    EditText comments,service_date;
    Button btnSubmit,btnDate;
    private ProgressDialog pDialog;
    Spinner staticSpinner;
    // url to create new product
    private static String url_create_service_request = "http://10.10.21.86/Sunshine/create_service_request.php";
    private static String url_send_sms = "http://10.10.21.86/Sunshine/send_sms.php";
    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser = new JSONParser();
    ModelUser user = new ModelUser();
    String value_comments, value_staticSpinner,value_date;
    public RequestServiceFragment () {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_request_service, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        comments = (EditText) view.findViewById(R.id.commentsBox);
        btnSubmit = (Button) view.findViewById(R.id.submit);
        btnDate = (Button) view.findViewById(R.id.button2);
        staticSpinner = (Spinner) view.findViewById(R.id.spinner);
        service_date = (EditText) view.findViewById(R.id.setDate);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.occupations,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateRequest().execute(url_create_service_request);
            }
        });

        UserProfile userProfile =  (UserProfile) getActivity();
        user = userProfile.displayUserDetails();

    }

    class CreateRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Creating Request..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            value_comments = comments.getText().toString();
            value_staticSpinner = staticSpinner.getSelectedItem().toString();
            value_date = service_date.getText().toString();
        }

        private void showMessage(String title,String Message){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(Message);
            builder.setPositiveButton("Ok", null);		//Adde this line Aravind
            builder.show();

        }

        /**
         * Creating request
         * */
        protected String doInBackground(String... args) {


            // Building Parameters
            HashMap<String, String> params = new HashMap<>();

            params.put("value_comments",value_comments);
            params.put("value_spinner", value_staticSpinner);
            params.put("value_date", value_date);
            params.put("user_id", String.valueOf(user.id));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_create_service_request, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("value_username",user.username);
                    params1.put("value_body",value_comments);
                    JSONObject json1 = jsonParser.makeHttpRequest(url_send_sms, "POST", params1);

                    // closing this screen
                } else {
                    // failed to create product
                    showMessage("Error","Something went wrong :( please try again later !!");
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
            showMessage("Added Request", "Your request has been successfully filed");
        }
    }

}
