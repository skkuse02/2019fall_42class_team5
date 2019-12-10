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

/* 회원가입 화면 */
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

                // password와 pwCheck가 같은지 검사
                if ((_password).equals(_pwCheck)) {
                    // 입력 값 없는 것 처리
                    if (_username.length() == 0 || _password.length() == 0) return;
                    // 비밀번호는 4자리 이상
                    if(_password.length()<4){
                        Toast.makeText(getApplicationContext(),"비밀번호는 4자리 이상이어야 합니다",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 서버로 보낼 JSON 객체 생성
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("username", username.getText().toString());
                        postData.put("password", password.getText().toString());

                        // 서버로 signup 정보 보냄
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

    /* sign up method */
    private static class sendSignUpInfo extends AsyncTask<String, Void, String> {

        private WeakReference<JoinActivity> activityReference; // static으로 선언했기 때문에 필요함

        // only retain a weak reference to the activity
        sendSignUpInfo(JoinActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData()); // 서버 주소 받아옴
            return req.sendPost(params[0], params[1]); // 서버로 POST

        }

        /* 응답 받은 후 */
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JoinActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if(response==null) return;


            if (response.substring(0,3).equals("200")) {
                // 응답 상태가 200이면 가입 완료
                Toast.makeText(activity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                // 응답 상태가 200이 아니면 서버로부터 온 실패이유 띄워줌
                Toast.makeText(activity, response.substring(3), Toast.LENGTH_SHORT).show();
            }


        }
    }
}
