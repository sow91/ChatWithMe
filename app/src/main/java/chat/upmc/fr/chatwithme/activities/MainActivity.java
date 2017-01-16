package chat.upmc.fr.chatwithme.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import chat.upmc.fr.chatwithme.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                // check if user is already logged in or not

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // if logged in redirect the user to chat room
                    Intent i = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(i);
                } else {
                    // otherwise redirect the user to login activity
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                finish();
            }
        };

        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);

    }
}
