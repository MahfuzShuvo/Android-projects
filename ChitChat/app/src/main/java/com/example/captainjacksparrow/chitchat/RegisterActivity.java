package com.example.captainjacksparrow.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mUser;
    private TextInputLayout mEmail;
    private TextInputLayout mPass;

    private Button btnReg;

    private Toolbar regToolbar;


    // ProgressBar --
    private ProgressDialog mRegProgress;

    // Firebase Auth ---
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set toolbar
        regToolbar = (Toolbar) findViewById(R.id.register_toolBar);
        setSupportActionBar(regToolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRegProgress = new ProgressDialog(this);

        // Firebase Auth ---
        mAuth = FirebaseAuth.getInstance();

        mUser = (TextInputLayout)findViewById(R.id.reg_user);
        mEmail = (TextInputLayout)findViewById(R.id.reg_email);
        mPass = (TextInputLayout)findViewById(R.id.reg_pass);

        btnReg = (Button)findViewById(R.id.reg_btn);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = mUser.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String pass = mPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(user) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {

                    mRegProgress.setTitle("Registering user");
                    mRegProgress.setMessage("Please wait until creating your account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(user, email, pass);

                }

            }
        });
    }

    private void register_user(final String user, String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", user);
                    userMap.put("status", "Hi, there. I'm using chitchat.");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mRegProgress.dismiss();

                                Toast.makeText(RegisterActivity.this, "Successfully registered...", Toast.LENGTH_LONG).show();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            } else {

                                mRegProgress.hide();

                                Toast.makeText(RegisterActivity.this, "Cannot register. There is some error", Toast.LENGTH_LONG).show();

                            }

                        }
                    });


                } else {

                    mRegProgress.hide();

                    Toast.makeText(RegisterActivity.this, "Cannot register. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
