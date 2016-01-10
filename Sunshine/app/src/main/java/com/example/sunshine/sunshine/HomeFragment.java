package com.example.sunshine.sunshine;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

/**
 * Created by dell pc on 25/12/2015.
 */
public class HomeFragment extends Fragment {

    EditText posts;
    Button postit;
    ListView listView;

    private static String url_submit_post = "http://10.10.21.86/Sunshine/create_post.php";
    private static String url_get_all_post = "http://10.10.21.86/Sunshine/get_all_posts.php";
    private static String url_delete_post = "http://10.10.21.86/Sunshine/delete_post.php";
    private static String url_send_sms_post_deleted = "http://10.10.21.86/Sunshine/send_sms_delete_post.php";


    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String,String>> allPosts = new ArrayList<>();
    ModelUser user = new ModelUser();
    String value_post;

    String deletePostId = "";


    public HomeFragment () {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = (EditText) view.findViewById(R.id.etPosts);
        postit = (Button) view.findViewById(R.id.bPostit);
        listView = (ListView) view.findViewById(R.id.container);

        postit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubmitPost().execute(url_submit_post);
            }
        });

        new GetAllPosts().execute(url_get_all_post);


        UserProfile userProfile =  (UserProfile) getActivity();
        user = userProfile.displayUserDetails();

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
                convertView = inflater.inflate(R.layout.post_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.txtPost = (TextView) convertView.findViewById(R.id.etPosts);
                viewHolder.txtDel = (TextView) convertView.findViewById(R.id.delete);
                viewHolder.txtCmnt = (TextView) convertView.findViewById(R.id.cmnt);
                viewHolder.txtLike = (TextView) convertView.findViewById(R.id.like);
                viewHolder.txtUname = (TextView) convertView.findViewById(R.id.username);
                if(!user.is_admin){
                    viewHolder.txtDel.setVisibility(View.INVISIBLE);
                }
                viewHolder.txtCmnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((UserProfile) getActivity()).displayComments(mData.size() - position);

                    }
                });
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.txtDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePostId = finalViewHolder.txtPost.getText().toString();
                        new DeletePost().execute(url_delete_post);
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final HashMap<String,String> temp = mData.get(position);
            viewHolder.txtPost.setText(temp.get("post"));
            viewHolder.txtUname.setText(temp.get("username"));

            return convertView;
        }
    }

    public class ViewHolder {
        TextView txtPost,txtCmnt,txtLike,txtUname, txtDel;
    }

    class DeletePost extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Deleting Post. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            startActivity(new Intent(getActivity(), UserProfile.class));

        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(deletePostId));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_delete_post, "POST", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("value_username",user.username);
                    JSONObject json1 = jsonParser.makeHttpRequest(url_send_sms_post_deleted, "POST", params1);
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
    class SubmitPost extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Submitting Post. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            value_post = posts.getText().toString();
        }


        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();

            params.put("value_post",value_post);
            params.put("value_userid", String.valueOf(user.id));

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_submit_post, "POST", params);

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            startActivity(new Intent(getActivity(),UserProfile.class));
        }
    }



    class GetAllPosts extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading all Posts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();

            // getting JSON Object
            // Note that create product url accepts POST method
            // check for success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_get_all_post, "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    requests = json.getJSONArray("posts");
                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject c = requests.getJSONObject(i);

                        // Storing each json item in variable
                        int id = c.getInt("id");
                        String username = c.getString("username");
                        String post = c.getString("post_text");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String,String>();

                        // adding each child node to HashMap key => value
                        map.put("id", String.valueOf(id));
                        map.put("username", username);
                        map.put("post", post);

                        // adding HashList to ArrayList
                        allPosts.add(map);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                     ListAdapter listAdapter = new MyListAdapter(getActivity(), allPosts, R.layout.post_item, new String[]{"post", "username"}, new int[]{R.id.etPosts, R.id.username});
                    listView.setAdapter(listAdapter);
                }
            });


        }

    }

}
