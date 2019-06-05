package com.example.onesns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

import java.util.Arrays;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button gotoRegisterBtn;
    private Button gotoLoginBtn;
    private EditText loginIDBox;
    private EditText loginPWBox;
    private ImageButton kakaoLoginBtn;
    private LoginButton facebookLoginBtn;
    private CheckBox autoLoginCheckBox;

    private FacebookLoginCallback facebookLoginCallback;
    private CallbackManager facebookCallbackManager;

    private void InitComponents(){
        gotoRegisterBtn = (Button)findViewById(R.id.regbtn);
        gotoLoginBtn = (Button)findViewById(R.id.loginbtn);
        loginIDBox = (EditText)findViewById(R.id.LoginIDBox);
        loginPWBox = (EditText)findViewById(R.id.LoginPWBox);
        kakaoLoginBtn = (ImageButton)findViewById(R.id.btn_kakao_login);
        facebookLoginBtn = (LoginButton)findViewById(R.id.facebookLoginButton);
        autoLoginCheckBox = (CheckBox)findViewById(R.id.autoLoginCheck);
    }

    private void InitLoginAPIs(){
        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginCallback = new FacebookLoginCallback(getApplicationContext());
        facebookLoginBtn.setReadPermissions(Arrays.asList("public_profile","email"));
        facebookLoginBtn.registerCallback(facebookCallbackManager,facebookLoginCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookCallbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("DebugKeyHash",EncryptionEncoder.getHashKey(getApplicationContext()));

        InitComponents();
        InitLoginAPIs();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("AutoLogin",MODE_PRIVATE);
        autoLoginCheckBox.setChecked(pref.getBoolean("isAutoLoginChecked",false));

        autoLoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("AutoLogin",MODE_PRIVATE);
                SharedPreferences.Editor prefEdit = pref.edit();

                prefEdit.putBoolean("isAutoLoginChecked",isChecked);
                prefEdit.commit();
            }
        });

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });

        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Using REST API Manager For Using User Login DB//
                RESTManager restmng = new RESTManager(getApplicationContext());
                restmng.setURL("http://fght7100.dothome.co.kr/profile.php");
                restmng.setMethod("GET");
                restmng.putArgument("mode","login");
                //restmng.putArgument("id",loginIDBox.getText().toString());
                restmng.putArgument("id",EncryptionEncoder.encryptBase64(loginIDBox.getText().toString()));
                //restmng.putArgument("pw",loginPWBox.getText().toString());
                restmng.putArgument("pw",EncryptionEncoder.encryptMD5(loginPWBox.getText().toString()));
                restmng.execute();
//                Intent i = new Intent(cont,MainActivity.class);
//                startActivity(i);
//                finish();
            }
        });

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = com.kakao.auth.Session.getCurrentSession();
                session.addCallback(new KakaoSessionCallback(getApplicationContext()));
                session.open(AuthType.KAKAO_LOGIN_ALL,LoginActivity.this);
            }
        });

        if( autoLoginCheckBox.isChecked() ){
            // Get UserSessionInfo //
            SharedPreferences sessionpref = getApplicationContext().getSharedPreferences("UserSession",MODE_PRIVATE);
            // Using REST API Manager For Using User Login DB//
            RESTManager restmng = new RESTManager(getApplicationContext());
            restmng.setURL("http://fght7100.dothome.co.kr/profile.php");
            restmng.setMethod("GET");
            restmng.putArgument("mode","login");
            //restmng.putArgument("id",loginIDBox.getText().toString());
            restmng.putArgument("id",EncryptionEncoder.encryptBase64(sessionpref.getString("UID","[NULLID]")));
            //restmng.putArgument("pw",loginPWBox.getText().toString());
            restmng.putArgument("pw",sessionpref.getString("UPW","[NULLPW]"));
            restmng.execute();
        }

    }
}
