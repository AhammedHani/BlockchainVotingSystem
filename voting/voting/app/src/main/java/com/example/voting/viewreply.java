package com.example.voting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class viewreply extends Activity {
    ListView l1;
    String ip,url;
    SharedPreferences sh;
     ArrayList<String>complaint,date,reply;
     Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewreply);
        l1=findViewById(R.id.list1);
        b1=findViewById(R.id.button3);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip=sh.getString("ip","");
        RequestQueue queue = Volley.newRequestQueue(viewreply.this);
        String url = "http://" + sh.getString("ip", "") + ":5000/viewreply";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                //  Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    complaint= new ArrayList<>();
                    date= new ArrayList<>();
                    reply=new ArrayList<>();
                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        complaint.add(jo.getString("complaint"));
                        date.add(jo.getString("date"));
                        reply.add(jo.getString("reply"));

                    }

//                    ArrayAdapter<String> ad=new ArrayAdapter<String>(Searchnearestcharity.this,android.R.layout.simple_list_item_1,cname);
//                    l1.setAdapter(ad);

                    l1.setAdapter(new custom3(viewreply.this,complaint,date,reply));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Error"+error,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lid",sh.getString("lid",""));




                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

b1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent
                in = new Intent(getApplicationContext(), addcompl.class);
        startActivity(in);
    }
});

    }
}