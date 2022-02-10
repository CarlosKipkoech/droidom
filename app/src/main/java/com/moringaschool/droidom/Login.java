package com.moringaschool.droidom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener {

    /**
     * Bind all the views
     */
    @BindView(R.id.email_input)
    EditText mEmail;
    @BindView(R.id.password_input)
    EditText mPassword;
    @BindView(R.id.login_button)
    Button mLoginBtn;
    @BindView(R.id.sign_up_textView)
    TextView mSignUp;
    @BindView(R.id.sign_up_textView2)
    TextView mSignUp2;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views and attach on click listeners
        ButterKnife.bind(this);
        mLoginBtn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mSignUp2.setOnClickListener(this);

        //initialize FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Login.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    @Override
    public void onClick(View view) {

        // navigate to sign up activity
        if (view == mSignUp || view == mSignUp2){
            startActivity(new Intent(Login.this,SignUp.class));
            finish();
        } else if (view == mLoginBtn){

            loginWithPassword();

        }

    }

    private void loginWithPassword() {

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (email.equals("")) {
            mEmail.setError("please Enter your email");

            return;
        }
        if (password.equals("")) {
            mPassword.setError("Password cannot be blank");

            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"Successful",Toast.LENGTH_LONG).show();
                } else  Toast.makeText(Login.this,"failed",Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


}