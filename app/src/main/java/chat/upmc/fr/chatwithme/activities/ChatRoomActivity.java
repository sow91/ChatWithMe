package chat.upmc.fr.chatwithme.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.adapters.ChatRoomsRecycleAdapter;
import chat.upmc.fr.chatwithme.adapters.ChatRoomsRecyclerAdapter1;
import chat.upmc.fr.chatwithme.adapters.ListOfUsersRecycleAdapter;
import chat.upmc.fr.chatwithme.models.Chat;
import chat.upmc.fr.chatwithme.models.User;

/**
 * Created by pc-sow on 31/12/2016.
 */
public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private Button mbuttonSend;
    private String userName,roomName;

    private DatabaseReference root;
    private String temp_key;
    private ArrayList<Chat> mchats = new ArrayList<Chat>();
    private ChatRoomsRecyclerAdapter1 mAdapter;
    Chat chat;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_activity);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) findViewById(R.id.edit_text_message);
        mbuttonSend = (Button) findViewById(R.id.button_send);

        init();
    }

    private void init() {

        mAdapter = new ChatRoomsRecyclerAdapter1(mchats);
        mRecyclerViewChat.setAdapter(mAdapter);






        userName = getIntent().getExtras().get("user_name").toString();
        roomName = getIntent().getExtras().get("room_name").toString();
        setTitle(" ROOM - "+roomName);

        root = FirebaseDatabase.getInstance().getReference().child("CHAT_ROOMS_MSG").child(roomName);
        mbuttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("name", userName);
                map1.put("msg", mETxtMessage.getText().toString());

                message_root.updateChildren(map1);

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
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

            }
        });
    }






    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();


        while (i.hasNext()) {

            chat = new Chat();
            chat.setMessage((String) ((DataSnapshot) i.next()).getValue());
            chat.setSender((String) ((DataSnapshot) i.next()).getValue());
                mchats.add(chat);
            }

        mAdapter.notifyDataSetChanged();
        mRecyclerViewChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        mETxtMessage.setText(" ");
        }


    }
