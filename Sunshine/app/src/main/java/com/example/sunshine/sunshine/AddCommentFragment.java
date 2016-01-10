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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dell pc on 06/01/2016.
 */
public class AddCommentFragment extends Fragment {

    EditText commentTxt;
    Button comment_it;
    ListView listView;

    private static String url_get_all_comments = "http://10.10.21.86/Sunshine/get_all_comments.php";
    private static String url_create_comment = "http://10.10.21.86/Sunshine/create_comment.php";
    private static String url_send_sms = "http://10.10.21.86/Sunshine/send_sms_comment.php";

    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    ArrayList<HashMap<String,String>> allComments = new ArrayList<>();

    String value_comment_txt = "";


    ModelUser user = new ModelUser();

    private ProgressDialog pDialog;

    int post_id = 0;
    public AddCommentFragment () {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_content_comments, container, false);
        post_id = getArguments().getInt("postid");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentTxt = (EditText) view.findViewById(R.id.etComments);
        comment_it = (Button) view.findViewById(R.id.bComment);
        listView = (ListView) view.findViewById(R.id.container);
        UserLocalStore userLocalStore = new UserLocalStore(getActivity());
        new GetAllComments().execute(url_get_all_comments);

        comment_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubmitComment().execute(url_create_comment);
                ((UserProfile)getActivity()).displayComments(post_id);
            }
        });

        user = userLocalStore.getLoggedInUser();

    }

    class SubmitComment extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Submitting Comment. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            value_comment_txt = commentTxt.getText().toString();
        }


        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("value_username",user.username);
            params.put("post_id", String.valueOf(post_id));
            params.put("value_comment",value_comment_txt);

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_create_comment, "POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("value_username",user.username);
                    params1.put("value_body",value_comment_txt);
                    JSONObject json1 = jsonParser.makeHttpRequest(url_send_sms, "POST", params1);
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

            //showMessage("Comment Added", "Your Comment has been Succefully added !!");
        }
    }

    class GetAllComments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting All Comments. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }


        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(post_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_get_all_comments, "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    requests = json.getJSONArray("comments");
                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject c = requests.getJSONObject(i);

                        // Storing each json item in variable
                        int id = c.getInt("id");
                        String username = c.getString("username");
                        String comment = c.getString("comment_txt");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String,String>();

                        // adding each child node to HashMap key => value
                        map.put("id", String.valueOf(id));
                        map.put("username", username);
                        map.put("comment", comment);

                        // adding HashList to ArrayList
                        allComments.add(map);
                    }

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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter listAdapter = new SimpleAdapter(getActivity(), allComments, R.layout.comment_row, new String[]{"username", "comment"}, new int[]{R.id.username, R.id.etComments});
                    listView.setAdapter(listAdapter);
                }
            });

        }
    }
}
