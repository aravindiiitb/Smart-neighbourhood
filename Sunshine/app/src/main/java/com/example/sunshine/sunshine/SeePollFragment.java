package com.example.sunshine.sunshine;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by dell pc on 08/01/2016.
 */
public class SeePollFragment extends Fragment {

    ListView listView;
    private static String url_get_poll = "http://10.10.21.86/Sunshine/get_all_polls.php";
    private static String TAG_SUCCESS = "success";
    JSONParser jsonParser= new JSONParser();
    JSONArray requests = null;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> requestList = new ArrayList<>();



    int poll_id = 0;
    public SeePollFragment () {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_see_poll, container, false);
        poll_id = getArguments().getInt("pollDesc");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        new GetPoll().execute(url_get_poll);
    }
/*
    class GetPoll extends AsyncTask<String,String,String> {

    }*/
}
