package com.example.myhttp_post_json;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClick = findViewById(R.id.btnClick);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtFName = findViewById(R.id.edtFName);
                EditText edtLName = findViewById(R.id.edtLName);

                String user_firstname = edtFName.getText().toString().trim();
                String user_lastname = edtLName.getText().toString().trim();

                String url = "http://192.168.137.1/test/insert_json.php";

                HTTP_POST_JSON http_post_json = new HTTP_POST_JSON();
                http_post_json.execute(url, user_firstname, user_lastname);
            }
        });

    }

    private class HTTP_POST_JSON extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... values) {
            try {
                URL url = new URL(values[0]);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                //conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user_firstname", values[1]);
                jsonParam.put("user_lastname", values[2]);

                Log.d("JSON", jsonParam.toString());

                DataOutputStream dataOUTPUT = new DataOutputStream(conn.getOutputStream());
                dataOUTPUT.writeBytes(jsonParam.toString());
                dataOUTPUT.flush();
                dataOUTPUT.close();

                Log.d("STATUS", String.valueOf(conn.getResponseCode()));
                Log.d("STATUS_MSG" , conn.getResponseMessage());

                DataInputStream dataInput = new DataInputStream(conn.getInputStream());
              //  String response = dataInput.readLine();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInput));
                String response = bufferedReader.readLine();
                bufferedReader.close();
                conn.disconnect();

                Log.d("RESPONSE" ,response);

                return jsonParam.toString();

            }catch (Exception e){  Log.d("STATUS", e.getLocalizedMessage());}

            return "null";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            TextView txvResponse = findViewById(R.id.txvResponse);
            Log.d("MESSAGE",response);
            txvResponse.append("\n" + response);

            try{

                JSONObject jsonResult = new JSONObject(response);
                Log.d("STATUS",jsonResult.toString());
                String user_fullname = jsonResult.getString("user_firstname")+" "+jsonResult.getString("user_lastname");

                Toast.makeText(MainActivity.this, user_fullname,Toast.LENGTH_LONG).show();

            } catch (JSONException e) { Log.d("STATUS", Objects.requireNonNull(e.getLocalizedMessage()));}

        }
    }
}