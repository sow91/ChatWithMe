package chat.upmc.fr.chatwithme.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chat.upmc.fr.chatwithme.Listeners.RecyclerTouchListener;
import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.adapters.ChatRecyclerAdapter;
import chat.upmc.fr.chatwithme.adapters.ListOfUsersRecycleAdapter;
import chat.upmc.fr.chatwithme.models.Chat;
import chat.upmc.fr.chatwithme.models.User;
import chat.upmc.fr.chatwithme.utils.Constants;
import chat.upmc.fr.chatwithme.utils.SharedPrefUtil;

/**
 * Created by pc-sow on 29/12/2016.
 */

public class ChatFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private Button mbuttonSend;

    private ProgressDialog mProgressDialog;
    private String receiver;
    private String receiverUid;
    private String sender;
    private String senderUid;
    private String receiverFirebaseToken;
    private ChatRecyclerAdapter mChatRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.chat_fragment, container, false);
        mRecyclerViewChat = (RecyclerView) fragmentView.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) fragmentView.findViewById(R.id.edit_text_message);
        mbuttonSend = (Button) fragmentView.findViewById(R.id.button_send);

        return fragmentView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        receiver = getArguments().getString(Constants.ARG_RECEIVER);
        receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);

        //load messages
        getMessageFromFirebaseUser(senderUid, receiverUid);

        mbuttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_send:
                sendMessage(v);
                break;
        }
    }

    private void sendMessage(View v) {

        String message = mETxtMessage.getText().toString();

        if (!message.isEmpty()) {


            Chat chat = new Chat(sender,
                    receiver,
                    senderUid,
                    receiverUid,
                    message,
                    System.currentTimeMillis());

            sendMessageToFirebaseUser(getActivity(), chat, receiverFirebaseToken);
        }
    }

    private void sendMessageToFirebaseUser(FragmentActivity activity, final Chat chat, final String receiverFirebaseToken) {
        final String room_type_1 = chat.getSenderUid() + "_" + chat.getReceiverUid();
        final String room_type_2 = chat.getReceiverUid() + "_" + chat.getSenderUid();

        Log.e("room_type_1 ", room_type_1);
        Log.e("room_type_2 ", room_type_2);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e("room_type_1", "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e("room_type_2", "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                }
                else {
                    Log.e("room_type_nonexistant", "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                    getMessageFromFirebaseUser(chat.getSenderUid(), chat.getReceiverUid());
                }



                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.getSender(),
                        chat.getMessage(),
                        chat.getSenderUid(),
                        new SharedPrefUtil(getActivity()).getString(Constants.ARG_FIREBASE_TOKEN),
                        receiverFirebaseToken);

                mETxtMessage.setText("");
                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e("room1", "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            Log.e("getMessageFrom ", chat.getMessage());

                            putMessageToRecycleView(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(),"Unable to get message: ", Toast.LENGTH_LONG);
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e("room_type2", "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            putMessageToRecycleView(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(),"Unable to get message: ", Toast.LENGTH_LONG);
                        }
                    });
                } else {
                    Log.e("no room type", "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Unable to get message: ", Toast.LENGTH_LONG);
            }
        });
    }



    private void sendPushNotificationToReceiver(String username, String message, String uid, String firebaseToken, String receiverFirebaseToken) {

        /*
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
                */
    }










    private void putMessageToRecycleView(Chat chat) {

        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }
}