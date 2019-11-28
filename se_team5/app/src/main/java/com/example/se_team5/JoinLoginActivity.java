package com.example.se_team5;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
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

                    new sendLoginInfo(JoinLoginActivity.this).execute("http://3ba7896a.ngrok.io/user/login", postData.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                username.setText("");
                password.setText("");
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

    private static class sendLoginInfo extends AsyncTask<String, Void, Boolean> {

        private WeakReference<JoinLoginActivity> activityReference;

        // only retain a weak reference to the activity
        sendLoginInfo(JoinLoginActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected Boolean doInBackground(String... params) {

            Boolean success = false;

            HttpURLConnection httpURLConnection = null;
            try {

                // server와 연결
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();

                // POST 형식으로 json 데이터 보내기
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                // response 성공여부
                success = (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // server와의 connection 해제
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return success;
        }
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            JoinLoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(!success){
                Toast.makeText(activity, "등록되지 않은 사용자입니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

