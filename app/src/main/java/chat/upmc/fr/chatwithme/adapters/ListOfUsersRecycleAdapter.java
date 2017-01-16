package chat.upmc.fr.chatwithme.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chat.upmc.fr.chatwithme.R;
import chat.upmc.fr.chatwithme.models.User;

/**
 * Created by pc-sow on 28/12/2016.
 */

public class ListOfUsersRecycleAdapter extends RecyclerView.Adapter<ListOfUsersRecycleAdapter.UserViewHolder> {

    private List<User> mUsers;


    public ListOfUsersRecycleAdapter(List<User> users) {
        this.mUsers = users;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserAlphabet, txtUsername;

        public UserViewHolder(View itemView) {
            super(itemView);

            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            txtUsername = (TextView) itemView.findViewById(R.id.text_view_username);
        }
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user_listing, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mUsers.get(position);

        String alphabet = user.getEmail().substring(0, 1);
        holder.txtUsername.setText(user.getEmail());
        holder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mUsers != null) {
            return mUsers.size();
        }
        return 0;
    }




}
