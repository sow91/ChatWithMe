package chat.upmc.fr.chatwithme.activities;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.models.User;
import chat.upmc.fr.chatwithme.utils.Constants;
import chat.upmc.fr.chatwithme.utils.SharedPrefUtil;

/**
 * Created by pc-sow on 28/12/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnRegister;

    private ProgressBar mprogressBar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //Get Firebase Auth instance
        auth = FirebaseAuth.getInstance();

        mETxtEmail = (EditText) findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) findViewById(R.id.edit_text_password);
        mBtnRegister = (Button) findViewById(R.id.button_register);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);

    }


    @Override
    protected void onStart() {
        super.onStart();

        mBtnRegister.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                onRegister(v);
                break;
        }
    }


    private void onRegister(View v) {

        String emailId = mETxtEmail.getText().toString().trim();
        String password = mETxtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailId)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(),R.string.minimum_password, Toast.LENGTH_SHORT).show();
            return;
        }


        mprogressBar.setVisibility(View.VISIBLE);

        //create user
        auth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        mprogressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            //add the user to users table
                            addUser(task.getResult().getUser());
                            startActivity(new Intent(RegisterActivity.this, UserActivity.class));
                            finish();
                        }

                    }
                });
    }

    private void addUser(FirebaseUser firebaseUser) {

        mprogressBar.setVisibility(View.VISIBLE);
        addUserToDatabase(firebaseUser);


    }

    private void addUserToDatabase(FirebaseUser firebaseUser) {

        // get refernce to database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN));
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.user_successfully_added, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.user_unable_to_add, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
       mprogressBar.setVisibility(View.GONE);
    }
}
