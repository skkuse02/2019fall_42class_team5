package com.example.se_team5;

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

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button SignUpButton = findViewById(R.id.signupButton);
        final TextView username = findViewById(R.id.idInput);
        final TextView password = findViewById(R.id.pwInput);
        final TextView pwCheck = findViewById(R.id.pwcheckInput);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _username = username.getText().toString();
                String _password = password.getText().toString();
                String _pwCheck = pwCheck.getText().toString();

                // password 확인 체크
                if ((_password).equals(_pwCheck)) {
                    // 입력 값 없는 것 처리
                    if (_username.length() == 0 || _password.length() == 0) return;

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("username", username.getText().toString());
                        postData.put("password", password.getText().toString());

                        new sendSignUpInfo(JoinActivity.this).execute("/user/signup", postData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static class sendSignUpInfo extends AsyncTask<String, Void, String> {

        private WeakReference<JoinActivity> activityReference;

        // only retain a weak reference to the activity
        sendSignUpInfo(JoinActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendPost(params[0], params[1]);

        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            JoinActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(response==null) return;

            if (response.substring(0,3).equals("200")) {
                Toast.makeText(activity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                Toast.makeText(activity, response.substring(3), Toast.LENGTH_SHORT).show();
            }


        }
    }
}
