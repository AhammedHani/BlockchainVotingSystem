package com.example.voting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class viewresult extends Activity implements AdapterView.OnItemSelectedListener {
    Spinner s;
    Button b1;
    ListView e1;
    SharedPreferences sh;
    int res;

    String url="",ip="",postid;
    ArrayList<String> post,pid;
    ArrayList<String> candidatename,result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewresult);
        s=findViewById(R.id.spinner3);
        b1=findViewById(R.id.button8);
        e1=findViewById(R.id.e1);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        viewpost();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                url ="http://"+sh.getString("ip", "") + ":5000/result";

                RequestQueue queue = Volley.newRequestQueue(viewresult.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("+++++++++++++++++",response);
                        try {

                            JSONArray ar=new JSONArray(response);
                            candidatename= new ArrayList<>();
                            result= new ArrayList<>();



                            for(int i=0;i<ar.length();i++)
                            {
                                JSONObject jo=ar.getJSONObject(i);
                                candidatename.add(jo.getString("student_name"));

                                result.add(jo.getString("vote"));


                            }




                            e1.setAdapter(new CUSTOM2(viewresult.this,candidatename,result));


                        } catch (Exception e) {
                            Log.d("=========", e.toString());
                        }


                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(viewresult.this, "err"+error, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("pid",postid);


                        return params;
                    }
                };
                queue.add(stringRequest);

            }
        });

    }

    private void viewpost() {
        url ="http://"+sh.getString("ip", "") + ":5000/post";
        RequestQueue queue = Volley.newRequestQueue(viewresult.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    post= new ArrayList<>();
                    pid= new ArrayList<>();


                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        post.add(jo.getString("post_name"));
                        pid.add(jo.getString("post_id"));


                    }

                    ArrayAdapter<String> ad=new ArrayAdapter<String>(viewresult.this,android.R.layout.simple_list_item_1,post);
                    s.setAdapter(ad);

                    s.setOnItemSelectedListener(viewresult.this);



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(viewresult.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lid",sh.getString("lid",""));

                return params;
            }
        };
        queue.add(stringRequest);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        postid=pid.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}