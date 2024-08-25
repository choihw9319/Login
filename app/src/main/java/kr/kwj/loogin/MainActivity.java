package kr.kwj.loogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 소셜 로그인 버튼 클릭 리스너 설정
        Button socialButton = findViewById(R.id.Login_socialbtnbool);
        socialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialLoginDialog();
            }
        });

        // 회원가입 버튼 클릭 리스너 설정
        Button signupButton = findViewById(R.id.Login_signenupbool);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignupDialog();
            }
        });
    }

    private void showSocialLoginDialog() {
        // 소셜 로그인 다이얼로그 레이아웃 인플레이트
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.social_login_dialog, null);

        // 소셜 로그인 다이얼로그 생성 및 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Social Login")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSignupDialog() {
        // 회원가입 다이얼로그 레이아웃 인플레이트
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signe_up, null);

        // 회원가입 다이얼로그 생성 및 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입")
                .setView(dialogView)
                .setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 회원가입 버튼 클릭 시 처리할 코드
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
