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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText socialIdEditText;
    private static final String TAG = "MainActivity"; // 로그 태그
    private static final String FILE_NAME = "user_data.txt"; // 파일 이름
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // activity_main.xml에 정의된 EditText들을 참조
        socialIdEditText = findViewById(R.id.social_idint);

        // 각 EditText에 TextWatcher를 설정
        setTextWatcher(socialIdEditText);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://152.67.209.177:3000/") // 실제 API 서버의 URL로 변경하세요.
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // "회원가입" 버튼 클릭 리스너 설정
        Button socialButton = findViewById(R.id.signeup);
        socialButton.setOnClickListener(v -> showSocialLoginDialog());

        // "로그인" 버튼 클릭 리스너 설정
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            saveLoginValuesToTextFile();
        });
    }

    private void showSocialLoginDialog() {
        // 다이얼로그 레이아웃 인플레이트
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signe_up, null);

        // 다이얼로그 내 EditText들 참조
        EditText dialogSocialEmailEditText = dialogView.findViewById(R.id.social_email);
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
                Log.d(TAG, "중복된 ID가 있습니다: " + id);
            } else {
                textViewNo.setVisibility(View.GONE);
                textViewOk.setVisibility(View.VISIBLE);
                signUpDialogButton.setEnabled(true); // 사용 가능한 ID이면 버튼 활성화
                Log.d(TAG, "사용 가능한 ID입니다: " + id);
            }
        });

        // 회원가입 버튼 클릭 리스너 설정
        signUpDialogButton.setOnClickListener(v -> {
            String email = dialogSocialEmailEditText.getText().toString();
            String id = dialogSocialIdEditText.getText().toString();
            String home = dialogSocialHomeEditText.getText().toString();
            String favorit = dialogSocialFavoritEditText.getText().toString();

            // 데이터를 로컬에 저장
            saveDialogValuesToTextFile(email, id, home, favorit);

            // 수집된 데이터로 서버에 요청
            sendPostRequest(email,id, home, favorit);

            dialog.dismiss(); // 버튼 클릭 후 다이얼로그 닫기
        });
    }

    private boolean isIdDuplicated(String id) {
        try {
            FileInputStream fileInputStream = openFileInput(FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

            // 텍스트 파일 내용에서 ID 검색
            String savedData = stringBuilder.toString();
            return savedData.contains("아이디: " + id);
        } catch (IOException e) {
            Log.e(TAG, "텍스트 파일 읽기 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    private void saveDialogValuesToTextFile(String email,String id, String home, String favorit) {
        try {
            // 저장할 문자열 데이터 생성
            String data = "아이디: " + id +  "\n이메이: " + email +"\n사는 곳: " + home + "\n자주 가는 곳: " + favorit;

            // 텍스트 파일로 저장
            FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            // 저장 성공 메시지 로그에 출력
            Log.d(TAG, "회원가입 정보가 성공적으로 저장되었습니다.");
            printTextFileContent(); // 저장 후 파일 내용 출력
        } catch (Exception e) {
            Log.e(TAG, "회원가입 정보 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void saveLoginValuesToTextFile() {
        try {
            // 로그인 시 입력된 ID를 텍스트로 저장
            String inputId = socialIdEditText.getText().toString();
            String data = "아이디: " + inputId;

            // 텍스트 파일로 저장
            FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            // 저장 성공 메시지 로그에 출력
            Log.d(TAG, "로그인 정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            Log.e(TAG, "로그인 정보 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void printTextFileContent() {
        try {
            FileInputStream fileInputStream = openFileInput(FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

            // 텍스트 파일 내용 로그에 출력
            Log.d(TAG, "저장된 텍스트 파일 내용: " + stringBuilder.toString());
        } catch (IOException e) {
            Log.e(TAG, "텍스트 파일 읽기 중 오류 발생: " + e.getMessage());
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
                Log.d(TAG, "Social ID 입력 값: " + socialId);
            }
        });
    }

    private void sendPostRequest(String email,String id, String home, String favorit) {

        PostData postData = new PostData(email, id, home,favorit);

        apiService.createPost(postData).enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Post 성공: " + response.body().getId(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(MainActivity.this, "Error: " + response.code() + " - " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
