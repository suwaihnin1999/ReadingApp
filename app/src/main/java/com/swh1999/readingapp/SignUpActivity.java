package com.swh1999.readingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
EditText mEmail,mPass,mConfirmPass;
Button mBtn;
TextView mLogin;
FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fAuth=FirebaseAuth.getInstance();

        inits();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(in);
                finish();
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                String conPass=mConfirmPass.getText().toString().trim();
                if(email.equals("")){
                    mEmail.setError("Email required!");
                }
                else if(pass.equals("")){
                    mPass.setError("Password required!");
                }
                else if(conPass.equals("")){
                    mConfirmPass.setError("Confirm password required!");
                }
                else if(pass.length()<6){
                    mPass.setError("Password at least 6 characters");
                }
                else if(conPass.length()<6){
                    mConfirmPass.setError("Password at least 6 characters");
                }
                else if(!(pass.equals(conPass))){
                    mConfirmPass.setError("Password and confirm password doesn't match.");
                }
                else{
                    Intent intent=new Intent(SignUpActivity.this,AddProfileActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("pass",pass);
                    startActivity(intent);

                }
            }
        });




    }

    private void inits() {
        mEmail=findViewById(R.id.signUp_email);
        mPass=findViewById(R.id.signUp_pass);
        mConfirmPass=findViewById(R.id.signUp_conPass);
        mBtn=findViewById(R.id.signUp_btn);
        mLogin=findViewById(R.id.signUp_login);
    }
}