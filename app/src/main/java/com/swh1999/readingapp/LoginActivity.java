package com.swh1999.readingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.swh1999.readingapp.R;

public class LoginActivity extends AppCompatActivity {
EditText mEmail,mPass;
MaterialButton mBtn;
TextView mSignUp;
FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inits();

        if(fAuth.getCurrentUser()!=null){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(in);
                finish();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                if(email.equals("")){
                    mEmail.setError("Email required!");
                }
                if(pass.equals("")){
                    mPass.setError("Password required!");
                }
                else{
                   fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                               startActivity(intent);
                               finish();
                           }
                           else {
                               Toast.makeText(LoginActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }

            }
        });

    }

    private void inits() {
        fAuth= FirebaseAuth.getInstance();
       mSignUp=findViewById(R.id.login_signUp);
       mBtn=findViewById(R.id.login_btn);
       mEmail=findViewById(R.id.login_email);
       mPass=findViewById(R.id.login_pass);
    }
}