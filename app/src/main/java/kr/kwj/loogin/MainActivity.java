package kr.kwj.loogin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText socialIdEditText;
    private EditText socialHomeEditText;
    private EditText socialFavoritEditText;
    private static final String TAG = "MainActivity"; // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // activity_main.xml에 정의된 EditText들을 참조
        socialIdEditText = findViewById(R.id.social_idint);
        socialHomeEditText = findViewById(R.id.social_homeint);
        socialFavoritEditText = findViewById(R.id.social_favoritint);

        // 각 EditText에 TextWatcher를 설정
        setTextWatcher(socialIdEditText);
        setTextWatcher(socialHomeEditText);
        setTextWatcher(socialFavoritEditText);

        // "회원가입" 버튼 클릭 리스너 설정
        Button socialButton = findViewById(R.id.signeup);
        socialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialLoginDialog();
            }
        });

        // "로그인" 버튼 클릭 리스너 설정
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoginValuesToJson();
            }
        });
    }

    private void showSocialLoginDialog() {
        // 다이얼로그 레이아웃 인플레이트
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signe_up, null);

        // 다이얼로그 내 EditText들 참조
        EditText dialogSocialIdEditText = dialogView.findViewById(R.id.social_idint2);
        EditText dialogSocialHomeEditText = dialogView.findViewById(R.id.social_homeint2);
        EditText dialogSocialFavoritEditText = dialogView.findViewById(R.id.social_favoritint2);

        // 중복 체크 버튼과 결과 표시할 TextView 참조
        Button checkButton = dialogView.findViewById(R.id.check);
        TextView textViewNo = dialogView.findViewById(R.id.textViewno);
        TextView textViewOk = dialogView.findViewById(R.id.textViewok);

        // 회원가입 버튼 참조
        Button signUpDialogButton = dialogView.findViewById(R.id.signeup_dialog);

        // 다이얼로그 생성 및 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Social Login")
                .setView(dialogView)
                .setPositiveButton("닫기", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        // 다이얼로그의 버튼 클릭 리스너 설정
        AlertDialog dialog = builder.create();
        dialog.show();

        // 중복 체크 버튼 클릭 리스너
        checkButton.setOnClickListener(v -> {
            String id = dialogSocialIdEditText.getText().toString();
            if (isIdDuplicated(id)) {
                textViewNo.setVisibility(View.VISIBLE);
                textViewOk.setVisibility(View.GONE);
                signUpDialogButton.setEnabled(false); // 중복된 ID가 있으면 버튼 비활성화
            } else {
                textViewNo.setVisibility(View.GONE);
                textViewOk.setVisibility(View.VISIBLE);
                signUpDialogButton.setEnabled(true); // 사용 가능한 ID이면 버튼 활성화
            }
        });

        // 회원가입 버튼 클릭 리스너 설정
        signUpDialogButton.setOnClickListener(v -> {
            String id = dialogSocialIdEditText.getText().toString();
            String home = dialogSocialHomeEditText.getText().toString();
            String favorit = dialogSocialFavoritEditText.getText().toString();

            saveDialogValuesToJson(id, home, favorit);
            dialog.dismiss(); // 버튼 클릭 후 다이얼로그 닫기
        });
    }

    private boolean isIdDuplicated(String id) {
        try {
            FileInputStream fileInputStream = openFileInput("user_data.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            // JSON 파일의 내용 파싱
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            String savedId = jsonObject.optString("아이디", "");

            return savedId.equals(id);
        } catch (Exception e) {
            Log.e(TAG, "JSON 파일 읽기 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    private void saveDialogValuesToJson(String id, String home, String favorit) {
        // JSON 객체 생성
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("아이디", id);
            jsonObject.put("사는 곳", home);
            jsonObject.put("자주 가는 곳", favorit);

            // JSON 파일로 저장
            FileOutputStream fileOutputStream = openFileOutput("user_data.json", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.close();

            // JSON 파일 내용 로그에 출력
            Log.d(TAG, "회원가입 JSON 파일이 성공적으로 저장되었습니다.");
            printJsonFileContent(); // 저장 후 파일 내용 출력
        } catch (Exception e) {
            Log.e(TAG, "회원가입 JSON 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void saveLoginValuesToJson() {
        // JSON 객체 생성
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("아이디", socialIdEditText.getText().toString());
            jsonObject.put("사는 곳", socialHomeEditText.getText().toString());
            jsonObject.put("자주 가는 곳", socialFavoritEditText.getText().toString());

            // JSON 파일로 저장
            FileOutputStream fileOutputStream = openFileOutput("user_data.json", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.close();

            // JSON 파일 내용 로그에 출력
            Log.d(TAG, "로그인 JSON 파일이 성공적으로 저장되었습니다.");
            printJsonFileContent(); // 저장 후 파일 내용 출력
        } catch (Exception e) {
            Log.e(TAG, "로그인 JSON 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void printJsonFileContent() {
        try {
            FileInputStream fileInputStream = openFileInput("user_data.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            // JSON 파일 내용 로그에 출력
            Log.d(TAG, "저장된 JSON 파일 내용: " + stringBuilder.toString());

        } catch (IOException e) {
            Log.e(TAG, "JSON 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    private void setTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 변경 중
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후, 모든 EditText의 값을 로그에 출력
                String socialId = socialIdEditText.getText().toString();
                String socialHome = socialHomeEditText.getText().toString();
                String socialFavorit = socialFavoritEditText.getText().toString();
            }

        });
    }

}

