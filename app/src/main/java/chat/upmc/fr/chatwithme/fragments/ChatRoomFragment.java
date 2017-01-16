package chat.upmc.fr.chatwithme.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.activities.ChatRoomActivity;
import chat.upmc.fr.chatwithme.models.Chat;

/**
 * Created by pc-sow on 30/12/2016.
 */
public class ChatRoomFragment extends Fragment {
    private ListView mListView;
    private EditText mRoomName;
    private Button mAddRoom;
    ArrayAdapter<String> mArrayAdapter;
    private String name;

    private ProgressDialog mProgressDialog;
    private List<Chat> mChats = new ArrayList<>();
    private String receiver;
    private String receiverUid;
    private String sender;
    private String senderUid;
    private String receiverFirebaseToken;
    private List<String> list_of_rooms = new ArrayList<>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.chat_room_fragment, container, false);
        mListView = (ListView) fragmentView.findViewById(R.id.listview);
        mRoomName = (EditText) fragmentView.findViewById(R.id.edit_text_message);
        mAddRoom = (Button) fragmentView.findViewById(R.id.button_send);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_of_rooms);
        mListView.setAdapter(mArrayAdapter);

        request_user_name();

        mAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(mRoomName.getText().toString(), "");
                root.child("CHAT_ROOMS_MSG").updateChildren(map);
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.child("CHAT_ROOMS_MSG").getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("room_name", ((TextView)view).getText().toString());
                intent.putExtra("user_name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(intent);
            }
        });
    }

    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Name:  ");

        final EditText input_field = new EditText(getActivity());
        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = input_field.getText().toString().trim();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                request_user_name();
            }
        });

        builder.show();
    }
}
