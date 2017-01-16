package chat.upmc.fr.chatwithme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.utils.Constants;
import chat.upmc.fr.chatwithme.utils.SharedPrefUtil;

/**
 * Created by pc-sow on 28/12/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnLogin, mBtnRegister,mbtnResetPassword;
    private ProgressBar mprogressBar;
    private FirebaseAuth auth;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content view now
        setContentView(R.layout.login_activity);

        mETxtEmail = (EditText) findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) findViewById(R.id.edit_text_password);
        mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnRegister = (Button) findViewById(R.id.button_register);
        mbtnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Get Firebase Auth instance
        auth = FirebaseAuth.getInstance();



    }


    @Override
    protected void onStart() {
        super.onStart();

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mbtnResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_login:
                onLogin(v);
                break;
            case R.id.button_register:
                onRegister(v);
                break;
            case R.id.btn_reset_password:
                onResetPassword(v);
                break;
        }
    }




    private void onLogin(View v) {
        String emailId = mETxtEmail.getText().toString().trim();
        final String password = mETxtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailId)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        mprogressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        mprogressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                mETxtPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {

                            updateFirebaseToken(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }


    private void onRegister(View v) {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void onResetPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mprogressBar.setVisibility(View.GONE);
    }
}
