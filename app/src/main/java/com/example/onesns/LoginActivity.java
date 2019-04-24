package com.example.onesns;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

public class LoginActivity extends AppCompatActivity {
    private Button gotoRegisterBtn;
    private Button gotoLoginBtn;
    private EditText loginIDBox;
    private EditText loginPWBox;
    private Context cont;
    private ImageButton kakaoLoginBtn;

    private void InitComponents(){
        gotoRegisterBtn = (Button)findViewById(R.id.regbtn);
        gotoLoginBtn = (Button)findViewById(R.id.loginbtn);
        loginIDBox = (EditText)findViewById(R.id.LoginIDBox);
        loginPWBox = (EditText)findViewById(R.id.LoginPWBox);
        kakaoLoginBtn = (ImageButton)findViewById(R.id.btn_kakao_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cont = this;

        InitComponents();

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(cont,RegisterActivity.class);
                startActivity(i);
            }
        });

        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(cont,MainActivity.class);
                startActivity(i);
                finish();*/
                // Using REST API Manager For Using User Login DB//
                RESTManager restmng = new RESTManager(cont);
                restmng.setURL("http://fght7100.dothome.co.kr/profile.php");
                restmng.setMethod("GET");
                restmng.putArgument("mode","login");
                //restmng.putArgument("id",loginIDBox.getText().toString());
                restmng.putArgument("id",EncryptionEncoder.encryptBase64(loginIDBox.getText().toString()));
                //restmng.putArgument("pw",loginPWBox.getText().toString());
                restmng.putArgument("pw",EncryptionEncoder.encryptMD5(loginPWBox.getText().toString()));

                restmng.execute();
            }
        });

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = com.kakao.auth.Session.getCurrentSession();
                session.addCallback(new KakaoSessionCallback(cont));
                session.open(AuthType.KAKAO_LOGIN_ALL,LoginActivity.this);
            }
        });

    }
}
