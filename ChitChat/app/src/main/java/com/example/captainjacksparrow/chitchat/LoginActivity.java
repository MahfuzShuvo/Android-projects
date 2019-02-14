package com.example.captainjacksparrow.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private Toolbar logToolbar;

    private Button btnLog;

    private TextInputLayout mEmail;
    private TextInputLayout mPass;

    // ProgressBar --
    private ProgressDialog mLogProgress;

    // Firebase Auth --
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set toolbar
        logToolbar = (Toolbar) findViewById(R.id.login_toolBar);
        setSupportActionBar(logToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLogProgress = new ProgressDialog(this);

        // Firebase Auth --
        mAuth = FirebaseAuth.getInstance();


        btnLog = (Button) findViewById(R.id.log_btn);

        mEmail = (TextInputLayout) findViewById(R.id.log_email);
        mPass = (TextInputLayout) findViewById(R.id.log_pass);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getEditText().getText().toString();
                String pass = mPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {

                    mLogProgress.setTitle("Logging In");
                    mLogProgress.setMessage("Please wait until checking your credentials!");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();

                    loginUser(email, pass);

                }

            }
        });

    }

    private void loginUser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mLogProgress.dismiss();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {

                    mLogProgress.hide();

                    Toast.makeText(LoginActivity.this, "Cannot sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
