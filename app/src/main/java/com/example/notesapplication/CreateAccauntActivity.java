package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccauntActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccauntBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_accaunt);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccauntBtn = findViewById(R.id.create_accaunt_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccauntBtn.setOnClickListener(v -> createAccaunt());
        loginBtnTextView.setOnClickListener(v -> finish());
    }

    void createAccaunt(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated){
            return;
        }
        createAccauntInFirebase(email, password);
    }

    void createAccauntInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccauntActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()){
                            Utility.showToast(CreateAccauntActivity.this, "Successfully create accaunt, Check email and verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            Utility.showToast(CreateAccauntActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccauntBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAccauntBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        /*if (!(password.length() <6)){
            passwordEditText.setError("Password length is invalid");
            return false;
        }*/
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }
}