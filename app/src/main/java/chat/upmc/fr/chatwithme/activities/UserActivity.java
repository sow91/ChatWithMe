package chat.upmc.fr.chatwithme.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;


import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.fragments.ChatRoomFragment;
import chat.upmc.fr.chatwithme.fragments.UserFragment;

/**
 * Created by pc-sow on 28/12/2016.
 */
public class UserActivity extends AppCompatActivity{
    private FirebaseAuth auth;
    UserFragment userFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        init();

    }

    private void init() {

        // create user fragment
        userFragment = new UserFragment();
        // get instance of firebaseAuth
        auth = FirebaseAuth.getInstance();

        // set the user screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_users,
                userFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.chat_room:
                startChatRoom();
                break;
            case R.id.edit_profil:
                editProfil();
                break;
            case R.id.status:
                postStatus();
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postStatus() {

    }



    private void editProfil() {

        /*
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_users,
                new EditProfilFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        */
    }




    private void startChatRoom() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_users,
                new ChatRoomFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }





    private void logout() {

        new AlertDialog.Builder(this)
                .setTitle("Logout").setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        auth.signOut();
                        startActivity(UserActivity.this,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void startActivity(Context mContext, int flags) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(flags);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
