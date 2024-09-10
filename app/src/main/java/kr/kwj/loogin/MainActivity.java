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
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "user_data.txt";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socialIdEditText = findViewById(R.id.social_idint);
        setTextWatcher(socialIdEditText);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://152.67.209.177:3000/") // Update with actual API server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        Button socialButton = findViewById(R.id.signeup);
        socialButton.setOnClickListener(v -> showSocialLoginDialog());

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            String inputId = socialIdEditText.getText().toString();

            apiService.getServerId().enqueue(new Callback<ServerIdResponse>() {
                @Override
                public void onResponse(Call<ServerIdResponse> call, Response<ServerIdResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String serverId = response.body().getId();

                        if (inputId.equals(serverId)) {
                            Toast.makeText(MainActivity.this, "ID가 일치합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "ID가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "서버에서 ID를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerIdResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showSocialLoginDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signe_up, null);

        EditText dialogSocialEmailEditText = dialogView.findViewById(R.id.social_email);
        EditText dialogSocialIdEditText = dialogView.findViewById(R.id.social_idint2);
        EditText dialogSocialHomeEditText = dialogView.findViewById(R.id.social_homeint2);
        EditText dialogSocialFavoritEditText = dialogView.findViewById(R.id.social_favoritint2);

        Button checkButton = dialogView.findViewById(R.id.check);
        TextView textViewNo = dialogView.findViewById(R.id.textViewno);
        TextView textViewOk = dialogView.findViewById(R.id.textViewok);
        Button signUpDialogButton = dialogView.findViewById(R.id.signeup_dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Social Login")
                .setView(dialogView)
                .setPositiveButton("닫기", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        checkButton.setOnClickListener(v -> {
            String id = dialogSocialIdEditText.getText().toString();
            if (isIdDuplicated(id)) {
                textViewNo.setVisibility(View.VISIBLE);
                textViewOk.setVisibility(View.GONE);
                signUpDialogButton.setEnabled(false);
                Log.d(TAG, "중복된 ID가 있습니다: " + id);
            } else {
                textViewNo.setVisibility(View.GONE);
                textViewOk.setVisibility(View.VISIBLE);
                signUpDialogButton.setEnabled(true);
                Log.d(TAG, "사용 가능한 ID입니다: " + id);
            }
        });

        signUpDialogButton.setOnClickListener(v -> {
            String email = dialogSocialEmailEditText.getText().toString();
            String id = dialogSocialIdEditText.getText().toString();
            String home = dialogSocialHomeEditText.getText().toString();
            String favorit = dialogSocialFavoritEditText.getText().toString();

            saveDialogValuesToTextFile(email, id, home, favorit);
            sendPostRequest(email, id, home, favorit);

            dialog.dismiss();
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

            String savedData = stringBuilder.toString();
            return savedData.contains("아이디: " + id);
        } catch (IOException e) {
            Log.e(TAG, "텍스트 파일 읽기 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    private void saveDialogValuesToTextFile(String email, String id, String home, String favorit) {
        try {
            String data = "아이디: " + id + "\n이메일: " + email + "\n사는 곳: " + home + "\n자주 가는 곳: " + favorit;

            FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            Log.d(TAG, "회원가입 정보가 성공적으로 저장되었습니다.");
            printTextFileContent();
        } catch (Exception e) {
            Log.e(TAG, "회원가입 정보 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void saveLoginValuesToTextFile() {
        try {
            String inputId = socialIdEditText.getText().toString();
            String data = "아이디: " + inputId;

            FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();

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

            Log.d(TAG, "저장된 텍스트 파일 내용: " + stringBuilder.toString());
        } catch (IOException e) {
            Log.e(TAG, "텍스트 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    private void setTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String socialId = socialIdEditText.getText().toString();
                Log.d(TAG, "Social ID 입력 값: " + socialId);
            }
        });
    }

    private void sendPostRequest(String email, String id, String home, String favorit) {
        PostData postData = new PostData(email, id, home, favorit);

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
