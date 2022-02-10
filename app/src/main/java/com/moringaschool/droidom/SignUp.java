package com.moringaschool.droidom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    //Bind up all views containing Sign Up information
    @BindView(R.id.new_name)
    EditText mNewName;
    @BindView(R.id.new_email_input)
    EditText mNewEmailInput;
    @BindView(R.id.new_password_input)
    EditText mNewPasswordInput;
    @BindView(R.id.confirm_password_input)
    EditText mConfirmPasswordInput;
    @BindView(R.id.CreateAccount_button)
    Button mCreateAccountBtn;
    @BindView(R.id.login_textView2)
    TextView mLoginTextView2;
    @BindView(R.id.Login_textView)
    TextView mLoginTextView;

    //Firebase auth tool
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //BindViews with butterKnife
        ButterKnife.bind(this);

        //instantiate firebase tools
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(SignUp.this, SignUp.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        //attach click listeners to these views
        mCreateAccountBtn.setOnClickListener(this);
        mLoginTextView2.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view == mLoginTextView2 || view == mLoginTextView){

            //navigate to login screen
            startActivity(new Intent(SignUp.this,Login.class));
            finish();

        } else if (view == mCreateAccountBtn) {

            createNewUser();

        }

    }

    private void createNewUser() {
        //get user reg details and assign variables
        final String email =mNewEmailInput.getText().toString().trim();
        final String Name = mNewName.getText().toString().trim();
        String password = mNewPasswordInput.getText().toString().trim();
        String confirmPassword = mConfirmPasswordInput.getText().toString().trim();


        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(Name);
        boolean validPassWord = isValidPassword(password,confirmPassword);

        if(!validEmail || !validName || !validPassWord) return;


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    createFirebaseUserProfile(Objects.requireNonNull(task.getResult().getUser()));
                }   else{
                    Toast.makeText(SignUp.this,"Failed to Authenticate",Toast.LENGTH_SHORT).show();

                }

            }

            private void createFirebaseUserProfile(final FirebaseUser user) {

                UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                        .setDisplayName(Name)
                        .build();
                user.updateProfile(addProfileName);
            }
        });

    }

    private boolean isValidPassword(String password, String confirmPassword) {

        if(password.length() < 6){
            mNewPasswordInput.setError("please create a password containing at least 6 characters");
            return  false;
        } else if(!password.equals(confirmPassword)) {
            mConfirmPasswordInput.setError("passwords do not Match");
            return  false;
        }
        return true;

    }

    private boolean isValidName(String name) {
        if(name.equals("")){
            mNewName.setError("please Enter Your Name");
        }
        return true;
    }

    private boolean isValidEmail(String email) {

        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail){

            mNewEmailInput.setError("Please Enter a valid email");
            return  false;

        }
        return true;
    }
    @Override
    public  void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public  void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}