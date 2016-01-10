package com.example.sunshine.sunshine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Map;

/**
 * Created by dell pc on 28/12/2015.
 */
public class CheckPollFragment extends Fragment {

    ListView listView;
    private static String url_get_polls = "http://10.10.21.86/Sunshine/get_all_polls.php";
    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> requestList = new ArrayList<>();



    public CheckPollFragment () {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_check_poll, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);

        new GetAllPolls().execute(url_get_polls);

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.poll_view,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.txtPoll = (TextView) convertView.findViewById(R.id.description);
                viewHolder.btnParticipate = (Button) convertView.findViewById(R.id.participate);
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.btnParticipate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //((UserProfile) getActivity()).displayPoll(mData.size() - position);
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final HashMap<String,String> temp = mData.get(position);
            viewHolder.txtPoll.setText(temp.get("description"));
            return convertView;
        }
    }

    public class ViewHolder {
        TextView txtPoll;
        Button btnParticipate;
    }


    class GetAllPolls extends AsyncTask<String,String,String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading all Polls. Please wait...");
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
                JSONObject json = jsonParser.makeHttpRequest(url_get_polls, "GET", params);

                // check log cat fro response
                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    requests = json.getJSONArray("polls");
                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject c = requests.getJSONObject(i);

                        // Storing each json item in variable
                        int id = c.getInt("id");
                        int user_id = c.getInt("user_id");
                        String description = c.getString("description");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String,String>();

                        // adding each child node to HashMap key => value
                        map.put("id", String.valueOf(id));
                        map.put("user_id", String.valueOf(user_id));
                        map.put("description", description);

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
                    //ListAdapter adapter = new SimpleAdapter();
                    // updating listview
                    //listView.setAdapter(adapter);
                    listView.setAdapter(new MyListAdapter(getActivity(), requestList, R.layout.poll_view, new String[]{"description"}, new int[]{R.id.description}));
                }
            });
        }
    }
}
