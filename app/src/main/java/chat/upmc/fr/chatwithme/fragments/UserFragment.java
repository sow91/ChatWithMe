package chat.upmc.fr.chatwithme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chat.upmc.fr.chatwithme.Listeners.RecyclerTouchListener;
import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.adapters.ListOfUsersRecycleAdapter;
import chat.upmc.fr.chatwithme.models.User;
import chat.upmc.fr.chatwithme.utils.Constants;

/**
 * Created by pc-sow on 29/12/2016.
 */

public class UserFragment extends Fragment {

    private List<User> mUsers = new ArrayList<User>();
    private ListOfUsersRecycleAdapter mAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    ChatFragment chatFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.user_fragment, container, false);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatFragment =  new ChatFragment();
        init();
    }

    private void init() {
        mAdapter = new ListOfUsersRecycleAdapter(mUsers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getAllUsersFromFirebase();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {

            public void onClick(View view, int position) {
                User user = mUsers.get(position);
                //Toast.makeText(getActivity(), user.getEmail() + " Fragment is selected!", Toast.LENGTH_SHORT).show();
                startChat(user.getEmail(), user.getUid(), user.getFirebaseToken());
            }

            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void startChat(String receiver, String receiverUid, String firebaseToken) {

        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        chatFragment.setArguments(args);
        //mUsers.clear();

        // set the user screen fragment
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_users,
                chatFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void getAllUsersFromFirebase() {

        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                        mUsers.clear();

                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            User user = dataSnapshotChild.getValue(User.class);
                            if (!TextUtils.equals(user.getUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                mUsers.add(user);
                            }
                        }

                        mAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}


