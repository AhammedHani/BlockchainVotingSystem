package com.example.voting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class VOTING extends Activity implements AdapterView.OnItemSelectedListener  {
    Spinner s1;
    Button b1;

    SharedPreferences sp;
    String ip="",url="",lid="";
    public static ArrayList<String> post,post_id,name,id;
    String a;
    RelativeLayout r1;
    RadioGroup rg;
    RadioButton[] rb;
    int pos;
    String chkd="",cid="";





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        s1=(Spinner)findViewById(R.id.spinner1);
        b1=(Button)findViewById(R.id.button1);
        s1.setOnItemSelectedListener(this);
        r1=(RelativeLayout)findViewById(R.id.rl);

        rg=new RadioGroup(this);
        rb = new RadioButton[10];
        sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip=sp.getString("ip", "");
        lid=sp.getString("lid", "");
        s1.setOnItemSelectedListener(this);

        url="http://"+ip+":5000/viewpost";
        try
        {
            if(android.os.Build.VERSION.SDK_INT>9)
            {
                StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        }
        catch(Exception e)
        {

        }
        RequestQueue queue = Volley.newRequestQueue(VOTING.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    post_id= new ArrayList<>();
                    post= new ArrayList<>();



                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        post.add(jo.getString("post_name"));
                        post_id.add(jo.getString("post_id"));

                    }

                    ArrayAdapter<String>ad=new ArrayAdapter<String>(VOTING.this, android.R.layout.simple_list_item_1,post);
                    s1.setAdapter(ad);




                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lid", lid);
                return params;
            }
        };
        queue.add(stringRequest);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(VOTING.this);
                url = "http://" + ip + ":5000/sdsa";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("+++++++++++++++++", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            String s = json.getString("task");


                            if(s.equalsIgnoreCase("Success"))
                            {

                                Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(), VOTING.class);
                                startActivity(i);

                            }
                            else if(s.equalsIgnoreCase("faild"))
                            {


                                Toast.makeText(getApplicationContext(), "already voted ", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(),STUDENTHOME.class);
                                startActivity(i);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "exp" + e, Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("lid", lid);
                        params.put("cid", chkd);
                        params.put("post", a);

                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        rg.removeAllViews();

        a=post_id.get(position);
        url="http://"+ip+":5000/candidates";
        RequestQueue queue = Volley.newRequestQueue(VOTING.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    name=new ArrayList<String>();
                    id=new ArrayList<String>();
//                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();


                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        name.add(jo.getString("student_name"));

                        id.add(jo.getString("login_id"));



                        rb[i]=new RadioButton(VOTING.this);
                        rg.addView(rb[i]);

                        rb[i].setText(name.get(i));







                    }


                    r1.addView(rg);
                    r1.setPadding(50, 50, 50, 50);





                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post",a);

                return params;
            }
        };
        queue.add(stringRequest);




        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                // TODO Auto-generated method stub
                pos = rg.indexOfChild(findViewById(checkedId));
                chkd = id.get(pos);
//                Toast.makeText(getApplicationContext(),"===="+chkd,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), chkd, Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
