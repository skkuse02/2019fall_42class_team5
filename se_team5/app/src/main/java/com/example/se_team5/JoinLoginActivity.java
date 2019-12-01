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

import java.lang.ref.WeakReference;

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
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = username.getText().toString();
                String pw = password.getText().toString();

                // 입력 값 없는 것 처리
                if (name.length() == 0 || pw.length() == 0) return;

                // JSON으로 로그인 데이터 보냄
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", username.getText().toString());
                    postData.put("password", password.getText().toString());

                    new sendLoginInfo(JoinLoginActivity.this).execute("/user/login", postData.toString());

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

    private static class sendLoginInfo extends AsyncTask<String, Void, String> {

        private WeakReference<JoinLoginActivity> activityReference;

        // only retain a weak reference to the activity
        sendLoginInfo(JoinLoginActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendPost(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JoinLoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(response==null) return;

            if (response.substring(0,3).equals("200")) {
                // 로그인 성공 시, 메인 화면 띄우기
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, response.substring(3), Toast.LENGTH_SHORT).show();
            }


        }
    }
}

