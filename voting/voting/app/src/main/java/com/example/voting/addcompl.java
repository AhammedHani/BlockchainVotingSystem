package com.example.voting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addcompl extends Activity {
    EditText e1;
    Button b1;
    String url,ip;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcompl);
        e1=findViewById(R.id.editTextTextMultiLine2);
        b1=findViewById(R.id.button14);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String cp=e1.getText().toString();
                if(cp.equals(""))
                {
                    e1.setError("type complaint");
                }

                else {

                    RequestQueue queue = Volley.newRequestQueue(addcompl.this);
                    url = "http://" + sh.getString("ip", "") + ":5000/sendcomp";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the response string.
                            Log.d("+++++++++++++++++", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                final String res = json.getString("task");

                                if (res.equalsIgnoreCase("successful")) {

                                    Toast.makeText(addcompl.this, "successfull", Toast.LENGTH_SHORT).show();

                                    Intent ik = new Intent(getApplicationContext(), STUDENTHOME.class);
                                    startActivity(ik);

                                } else {


                                    Toast.makeText(addcompl.this, "not successful", Toast.LENGTH_SHORT).show();


                                }
                            } catch (JSONException e) {
                                Toast.makeText(addcompl.this, "error" + e, Toast.LENGTH_SHORT).show();
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
                            params.put("comp", cp);
                            params.put("lid", sh.getString("lid", ""));
                            return params;
                        }
                    };
                    queue.add(stringRequest);


                }}
        });

    }
}