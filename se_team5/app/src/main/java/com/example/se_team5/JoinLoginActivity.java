package com.example.se_team5;

import android.content.Intent;
import android.content.SharedPreferences;
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

/* 앱 실행시 처음으로 보이는 로그인 화면 */
public class JoinLoginActivity extends AppCompatActivity {

    private String user_id;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView username = findViewById(R.id.username);
        final TextView password = findViewById(R.id.password);

        Button signinButton = findViewById(R.id.signinButton);
        TextView signupText = findViewById(R.id.signupButton);

        // 로컬에 저장된 로그인 정보 확인
        SharedPreferences check = getSharedPreferences("userFile", MODE_PRIVATE);
        String pastID = check.getString("username","");
        String pastPW = check.getString("password", null);

        if(pastID.length()>0 && pastPW.length()>0){
            // 저장된 값이 있다면 로그인 시도
            JSONObject postData = new JSONObject();
            try {
                postData.put("username", pastID);
                postData.put("password", pastPW);

                new sendLoginInfo(JoinLoginActivity.this).execute("/user/login", postData.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Sign In
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_id = username.getText().toString();
                user_password = password.getText().toString();

                // 입력 값 없는 것 처리
                if (user_id.length() == 0 || user_password.length() == 0) return;

                // JSON으로 로그인 데이터 보냄
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", user_id);
                    postData.put("password", user_password);

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

    private class sendLoginInfo extends AsyncTask<String, Void, String> {

        private WeakReference<JoinLoginActivity> activityReference;

        // only retain a weak reference to the activity
        sendLoginInfo(JoinLoginActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            // POST로 보냄
            return req.sendPost(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JoinLoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(response==null) return;
            // 응답코드가 200이면
            if (response.substring(0,3).equals("200")) {
                // 로그인 성공
                // 자동로그인 위해 sharedpreferences에 저장
                SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("username", user_id);
                editor.putString("password", user_password);
                editor.commit();

                // 메인 화면 띄우기
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("username", user_id);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast.makeText(activity, response.substring(3), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

