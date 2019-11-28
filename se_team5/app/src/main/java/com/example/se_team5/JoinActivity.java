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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

                        new sendSignUpInfo(JoinActivity.this).execute("http://cc442251.ngrok.io/user/signup", postData.toString());

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
    private static class sendSignUpInfo extends AsyncTask<String, Void, JSONObject> {

        private WeakReference<JoinActivity> activityReference;
        // only retain a weak reference to the activity
        sendSignUpInfo(JoinActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject response= new JSONObject();

            HttpURLConnection httpURLConnection = null;
            try {

                // server와 연결
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();

                // POST 형식으로 json 데이터 보내기
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

            JoinActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                int status = response.getInt("status");
                String message = response.getString("message");

                if (status == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(activity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
