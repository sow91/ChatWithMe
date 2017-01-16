package chat.upmc.fr.chatwithme.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.models.Chat;
import chat.upmc.fr.chatwithme.models.User;

/**
 * Created by pc-sow on 28/12/2016.
 */

public class ChatRoomsRecycleAdapter extends RecyclerView.Adapter<ChatRoomsRecycleAdapter.UserViewHolder> {

    private List<Chat> mchats;


    public ChatRoomsRecycleAdapter(List<Chat> chats) {
        this.mchats = chats;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserAlphabet, txtUsername, txtMessage;

        public UserViewHolder(View itemView) {
            super(itemView);

            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            //txtUsername = (TextView) itemView.findViewById(R.id.text_view_username);
            txtMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
        }
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Chat chat = mchats.get(position);

        String alphabet = chat.getSender().substring(0, 1);
        holder.txtMessage.setText(chat.getMessage());
        holder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mchats != null) {
            return mchats.size();
        }
        return 0;
    }




}
