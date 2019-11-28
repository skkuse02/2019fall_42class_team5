package com.example.se_team5;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final TextView username = findViewById(R.id.username);
        final TextView password = findViewById(R.id.password);

        Button signinButton = findViewById(R.id.signinButton);
        TextView signupText = findViewById(R.id.signupButton);


        // Sign In
        signinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = username.getText().toString();
                String pw = password.getText().toString();

                // 입력 값 없는 것 처리
                if(name.length()==0 || pw.length()==0) return;
                
                // JSON으로 로그인 데이터 보냄
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username",username.getText().toString());
                    postData.put("password", password.getText().toString());

                    new sendLoginInfo(JoinLoginActivity.this).execute("http://cc442251.ngrok.io/user/login", postData.toString());

                    // 빈칸으로 바꾸기
                    username.setText("");
                    password.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Sign Up
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });


    }

    private static class sendLoginInfo extends AsyncTask<String, Void, JSONObject> {

        private WeakReference<JoinLoginActivity> activityReference;

        // only retain a weak reference to the activity
        sendLoginInfo(JoinLoginActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject response = new JSONObject();

            HttpURLConnection httpURLConnection = null;
            try {

                // server와 연결
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();

                // POST 형식으로 json 데이터 보내고 결과 받기
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                // response 성공여부
                int status = httpURLConnection.getResponseCode();
                response.put("status",status);
                response.put("message","");

                // response body 받기
                if(status!=200){
                    InputStream is = httpURLConnection.getErrorStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String responseMsg = in.readLine();
                    response.put("message",responseMsg);
                    in.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // server와의 connection 해제
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return response;
        }
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            JoinLoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            try {
                int status = response.getInt("status");
                String message = response.getString("message");

                if (status == 200) {
                    // 로그인 성공 시, 메인 화면 띄우기
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e){
                Log.e("error",e.getMessage());
            }

        }
    }
}

