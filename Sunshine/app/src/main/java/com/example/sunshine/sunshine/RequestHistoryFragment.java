package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dell pc on 31/12/2015.
 */
public class RequestHistoryFragment extends Fragment {

    // Progress Dialog
    private ProgressDialog pDialog;
    // url to create new product
    private static String url_get_requests = "http://10.10.21.86/Sunshine/get_user_service_requests.php";
    private static String TAG_SUCCESS = "success";
    ModelUser user;
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    ArrayList<HashMap<String, String>> requestList = new ArrayList<>();
    ListView listView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_request_history, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);
        UserProfile userProfile =  (UserProfile) getActivity();
        user = userProfile.displayUserDetails();

        new GetAllRequests().execute(url_get_requests);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("In this click history fragment block");
            }
        });
    }

    class GetAllRequests extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
            params.put("value_user_id", String.valueOf(user.id));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_get_requests, "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    System.out.println("Inside Success Block Main thread !!");
                    requests = json.getJSONArray("service_request");
                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject c = requests.getJSONObject(i);

                        // Storing each json item in variable
                        int id = c.getInt("id");
                        int user_id = c.getInt("user_id");
                        String service_type = c.getString("service_type");
                        String service_instruction = c.getString("service_instruction");
                        String service_date = c.getString("service_date");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String,String>();

                        // adding each child node to HashMap key => value
                        map.put("id", String.valueOf(id));
                        map.put("user_id", String.valueOf(user_id));
                        map.put("service_type", service_type);
                        map.put("service_instruction", service_instruction);
                        map.put("service_date", service_date);

                        // adding HashList to ArrayList
                        requestList.add(map);
                    }

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
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(getActivity(), requestList, R.layout.list_item, new String[]{"service_type", "service_instruction", "service_date"}, new int[]{R.id.serviceType, R.id.serviceComment, R.id.serviceDate});
                    // updating listview
                    listView.setAdapter(adapter);
                }
            });
        }

    }
}
