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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends Activity {
    EditText ed,ed1;
    Button b2;
    String username,password,ip,url;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed = findViewById(R.id.uname);
        ed1 = findViewById(R.id.editTextTextPassword);
        b2 = findViewById(R.id.button5
        );
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        ip=sh.getString("ip","");

        url="http://"+sh.getString("ip","")+":5000/logincode";

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=ed.getText().toString();
                password=ed1.getText().toString();



                if(username.equalsIgnoreCase(""))
                {
                    ed.setError("enter yor username");
                }
else if (password.equalsIgnoreCase(""))
                {
                    ed1.setError("enter your password");
                }
else {

                    RequestQueue queue = Volley.newRequestQueue(login.this);

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the response string.

                            try {
                                JSONObject jo = new JSONObject(response);
                                String status = jo.getString("task");
                                Toast.makeText(login.this, status, Toast.LENGTH_SHORT).show();

                                if (status.equalsIgnoreCase("success")) {
                                    String lid = jo.getString("lid");

                                    SharedPreferences.Editor edt = sh.edit();
                                    edt.putString("lid", lid);
                                    edt.commit();
                                    Toast.makeText(login.this, status, Toast.LENGTH_SHORT).show();

                                    Intent
                                            in = new Intent(getApplicationContext(), STUDENTHOME.class);
                                    startActivity(in);
//


                                } else if (status.equals("invalid")) {
                                    Toast.makeText(login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.d("=========", e.toString());
                                Toast.makeText(login.this, "" + e, Toast.LENGTH_SHORT).show();

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("uname", username);
                            params.put("password", password);

                            return params;
                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);


                }

            }
        });
    }
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}