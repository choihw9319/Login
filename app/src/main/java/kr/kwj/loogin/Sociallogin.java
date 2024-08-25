package kr.kwj.loogin;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Sociallogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_login_dialog); // social_login_dialog 레이아웃 설정

        Button signenup = findViewById(R.id.signeup); // 버튼 ID 확인

        signenup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void showSignUpDialog() {
        // 다이얼로그를 위한 레이아웃 인플레이터 생성
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signe_up, null); // signe_up 레이아웃 설정

        // 다이얼로그 생성
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Sign Up"); // 제목 추가

        // 다이얼로그 표시
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
