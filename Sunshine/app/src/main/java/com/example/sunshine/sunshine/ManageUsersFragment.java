package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dell pc on 01/01/2016.
 */
public class ManageUsersFragment extends Fragment {

    String Email = "";
    String user_name = "";
    private ProgressDialog pDialog;
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    private static String url_get_all_users = "http://10.10.21.86/Sunshine/get_all_users.php";
    private static String url_verify_user = "http://10.10.21.86/Sunshine/verify_user.php";
    private static String url_delete_user = "http://10.10.21.86/Sunshine/delete_user.php";
    private static String url_send_sms_user_verified = "http://10.10.21.86/Sunshine/send_sms_user.php";
    private static String TAG_SUCCESS = "success";

    ArrayList<HashMap<String, String>> requestList = new ArrayList<>();
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage_users, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);
        new GetAllUsers().execute(url_get_all_users);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StringBuffer buffer = new StringBuffer();

                buffer.append("Username :" + requestList.get(position).get("username") + "\n\n");
                buffer.append("email :" + requestList.get(position).get("email") + "\n\n");
                buffer.append("password :" + requestList.get(position).get("password") + "\n\n");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("User Details");
                builder.setMessage(buffer.toString());
                builder.setPositiveButton("Ok", null);        //Adde this line Aravind
                builder.show();
            }
        });

    }

    private class MyListAdapter extends SimpleAdapter {

        private final LayoutInflater mInflater;

        private int[] mTo;
        private String[] mFrom;
        private List<? extends HashMap<String, String>> mData;

        private int mResource;
        private int mDropDownResource;

        private int layout;


        public MyListAdapter(Context context, List<? extends HashMap<String, String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            layout = resource;
            mData = data;
            mResource = mDropDownResource = resource;
            mFrom = from;
            mTo = to;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.user_row,parent,false);
                viewHolder = new ViewHolder();

                viewHolder.txtEmail = (TextView) convertView.findViewById(R.id.email);
                viewHolder.btnRemove = (Button) convertView.findViewById(R.id.remove);
                viewHolder.btnVerify = (Button) convertView.findViewById(R.id.verify);

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //((UserProfile) getActivity()).displayComments(mData.size() - position);
                        Email = finalViewHolder.txtEmail.getText().toString();
                        new VerifyUser().execute();
                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Email = finalViewHolder.txtEmail.getText().toString();
                        new DeleteUser().execute();
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final HashMap<String,String> temp = mData.get(position);
            viewHolder.txtEmail.setText(temp.get("email"));
            return convertView;
        }
    }

    public class ViewHolder {
        TextView txtEmail;
        Button btnRemove, btnVerify;
    }

    class  VerifyUser extends  AsyncTask<String, String, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((UserProfile) getActivity()).displayView(7);
        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("username", Email);

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_verify_user, "POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("value_username",Email);
                    JSONObject json1 = jsonParser.makeHttpRequest(url_send_sms_user_verified, "POST", params1);
                    int success1 = json.getInt(TAG_SUCCESS);
                    if(success1 == 1){
                        System.out.println("printed this bro !!");
                    }
                    // closing this screen
                } else {
                    // failed to create product
                    //showMessage("Error","Something went wrong :( please try again later !!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class DeleteUser extends AsyncTask<String,String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Deleting User. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("email", Email);

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_delete_user, "POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    // closing this screen
                } else {
                    // failed to create product
                    //showMessage("Error","Something went wrong :( please try again later !!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            ((UserProfile) getActivity()).displayView(7);
        }


    }
    class GetAllUsers extends AsyncTask<String,String,String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading all Users. Please wait...");
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

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_get_all_users, "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    if(json.length() != 0) {
                        requests = json.getJSONArray("users");
                        for (int i = 0; i < requests.length(); i++) {
                            JSONObject c = requests.getJSONObject(i);

                            // Storing each json item in variable
                            int id = c.getInt("id");
                            String username = c.getString("username");
                            String password = c.getString("password");
                            String email = c.getString("email");

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put("id", String.valueOf(id));
                            map.put("username", username);
                            map.put("email", email);
                            map.put("password", password);

                            // adding HashList to ArrayList
                            requestList.add(map);
                        }
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
                    ListAdapter adapter = new MyListAdapter(getActivity(), requestList, R.layout.user_row, new String[]{"email", "username", "password"}, new int[]{R.id.email, R.id.username, R.id.password});
                    // updating listview
                    listView.setAdapter(adapter);
                }
            });
        }
    }


}
