package com.example.voting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APPLYFORCANDIDATE extends Activity implements AdapterView.OnItemSelectedListener {
EditText ed;
Button b6;
Spinner spinner;
Button b7;
    SharedPreferences sh;
    private static final int FILE_SELECT_CODE = 0;
    int res;
    String fileName="",path="";



    String url="",ip="",postid;
    ArrayList<String> post,pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyforcandidate);
        ed=findViewById(R.id.editTextTextPersonName8);
        b6=findViewById(R.id.button6);
        spinner=findViewById(R.id.spinner);
        b7=findViewById(R.id.button7);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        viewpost();


        if(android.os.Build.VERSION.SDK_INT>9)
        {
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //getting all types of files
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, ""), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {

                    Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(path.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "pls choose a file", Toast.LENGTH_LONG).show();
                }
                else {
                    String out = uploadFile(path);
                    try {
                        JSONObject jo = new JSONObject(out);
                        String status = jo.getString("task");
                        if (status.equals("sucess")) {
                            Toast.makeText(getApplicationContext(), " registered", Toast.LENGTH_LONG).show();
                            Intent ik = new Intent(getApplicationContext(), STUDENTHOME.class);
                            startActivity(ik);

                        } else {
                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {

                    }



                }
            }
        });
    }

    private void viewpost() {






        url ="http://"+sh.getString("ip", "") + ":5000/post";
        RequestQueue queue = Volley.newRequestQueue(APPLYFORCANDIDATE.this);

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

                     ArrayAdapter<String> ad=new ArrayAdapter<String>(APPLYFORCANDIDATE.this,android.R.layout.simple_list_item_1,post);
                    spinner.setAdapter(ad);

                    spinner.setOnItemSelectedListener(APPLYFORCANDIDATE.this);



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(APPLYFORCANDIDATE.this, "err"+error, Toast.LENGTH_SHORT).show();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        postid=pid.get(i);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("File Uri", "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        path = FileUtils.getPath(this, uri);
                        //e4.setText(path1);
                        Log.d("path", path);
                        File fil = new File(path);
                        int fln = (int) fil.length();
                          ed.setText(path);

                        File file = new File(path);

                        byte[] byteArray = null;
                        try {
                            InputStream inputStream = new FileInputStream(fil);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] b = new byte[fln];
                            int bytesRead = 0;

                            while ((bytesRead = inputStream.read(b)) != -1) {
                                bos.write(b, 0, bytesRead);
                            }

                            byteArray = bos.toByteArray();
                            inputStream.close();
                            Bitmap bmp= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            if(bmp!=null){
//
//
//                                i1.setVisibility(View.VISIBLE);
//                                i1.setImageBitmap(bmp);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    } catch (URISyntaxException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please select suitable file", Toast.LENGTH_LONG).show();
                }
                break;


        }


    }

    public String uploadFile(String sourceFileUri) {
        try {
            fileName = sourceFileUri;
            String upLoadServerUri = "http://" + sh.getString("ip", "") + ":5000/application";
//            Toast.makeText(getApplicationContext(), upLoadServerUri, Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
            FileUpload fp = new FileUpload(fileName);
            Map mp = new HashMap<String, String>();
            mp.put("pid",postid );
            mp.put("sid",sh.getString("lid","") );


           String res= fp.multipartRequest(upLoadServerUri, mp, fileName, "files", "application/octet-stream");
            return res;

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"error"+e,Toast.LENGTH_LONG).show();
            return "0";
        }
    }
}