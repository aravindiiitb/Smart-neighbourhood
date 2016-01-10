package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dell pc on 28/12/2015.
 */
public class AddPollFragment extends Fragment {

    Button addOption,submitPoll;
    EditText descrPoll,descOption;
    LinearLayout containerOptions;

    // Progress Dialog
    private ProgressDialog pDialog;
    // url to create new product
    private static String url_create_poll = "http://10.10.21.86/Sunshine/create_poll.php";
    private static String TAG_SUCCESS = "success";
    ModelUser user;
    JSONParser jsonParser= new JSONParser();
    String[] strings;


    String value_description;
    ArrayList<RadioButton> allRdBtns = new ArrayList<>();
    public AddPollFragment () {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_poll, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descOption = (EditText) view.findViewById(R.id.textDescOptn);
        submitPoll = (Button) view.findViewById(R.id.btnCreatePoll);
        containerOptions = (LinearLayout) view.findViewById(R.id.container);
        addOption = (Button) view.findViewById(R.id.btnAddOption);
        descrPoll = (EditText) view.findViewById(R.id.textDescPoll);

        UserProfile userProfile =  (UserProfile) getActivity();
        user = userProfile.displayUserDetails();


        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                final View addView = layoutInflater.inflate(R.layout.row, null);
                RadioButton textOut = (RadioButton)addView.findViewById(R.id.radioButton);
                allRdBtns.add(textOut);
                textOut.setText(descOption.getText().toString());
                Button buttonRemove = (Button)addView.findViewById(R.id.remove);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                        allRdBtns.remove(addView.getParent());
                    }
                });

                containerOptions.addView(addView);
            }
        });

        submitPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value_description = descrPoll.getText().toString();
                new SubmitPoll().execute(url_create_poll);
            }
        });

    }

    class SubmitPoll extends AsyncTask<String,String,String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Creating Poll...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            strings = new String[allRdBtns.size()];


            for(int i=0; i < allRdBtns.size(); i++){
                strings[i] = allRdBtns.get(i).getText().toString();
            }
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
         * Creating product
         * */
        protected String doInBackground(String... args) {


            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("value_description", value_description);
            params.put("value_user_id",String.valueOf(user.id));
            int i=1;
            for (String btnRd: strings){
                params.put("option_"+ String.valueOf(i),btnRd);
                i++;
            }
            params.put("no_options", String.valueOf(i));

            System.out.println("params length: "+ params.size());

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_create_poll,"POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    //Intent i1 = new Intent(getActivity(), UserProfile.class);
                    //startActivity(i1);

                    // closing this screen
                    //finish();
                } else {
                    // failed to create product
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
            showMessage("Added Poll", "Your poll has been successfully submitted !!");
        }
    }
}
