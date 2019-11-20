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

import java.io.DataOutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button SignUpButton = findViewById(R.id.signupButton);
        final TextView username = findViewById(R.id.idInput);
        final TextView nickname = findViewById(R.id.nicknameInput);
        final TextView password = findViewById(R.id.pwInput);
        final TextView pwCheck = findViewById(R.id.pwcheckInput);

        SignUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String _username = username.getText().toString();
                String _nickname = nickname.getText().toString();
                String _password = password.getText().toString();
                String _pwCheck = pwCheck.getText().toString();

                // password 확인 체크
                if ((_password).equals(_pwCheck)) {
                    // 입력 값 없는 것 처리
                    if(_username.length()==0 || _nickname.length()==0 || _password.length()==0) return;

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("username", username.getText().toString());
                        postData.put("nickname", nickname.getText().toString());
                        postData.put("password", password.getText().toString());

                        new sendSignUpInfo(JoinActivity.this).execute("http://41a495db.ngrok.io/user/signup", postData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private static class sendSignUpInfo extends AsyncTask<String, Void, Integer> {

        private WeakReference<JoinActivity> activityReference;
        // only retain a weak reference to the activity
        sendSignUpInfo(JoinActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(String... params) {

            int response_code=404;

            HttpURLConnection httpURLConnection = null;
            try {

                // server와 연결
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();

                // POST 형식으로 json 데이터 보내기
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();


                // response 성공여부
                response_code = httpURLConnection.getResponseCode();


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // server와의 connection 해제
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return response_code;
        }
        protected void onPostExecute(Integer response_code) {
            super.onPostExecute(response_code);

            JoinActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if(response_code == HttpURLConnection.HTTP_OK){
                Toast.makeText(activity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            else if(response_code == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                Toast.makeText(activity, "이미 사용중인 ID입니다", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(activity, "HTTP: "+response_code.toString(),Toast.LENGTH_SHORT).show();
            }

        }
    }
}
