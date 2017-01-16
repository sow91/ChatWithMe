package chat.upmc.fr.chatwithme.utils;

import chat.upmc.fr.chatwithme.models.User;

/**
 * Created by pc-sow on 30/12/2016.
 */

public class List {

   public static void addAll(java.util.List<User> listUsers, java.util.List<User> mUsers){
        for (int i = 0; i< mUsers.size(); i++){
            listUsers.add(mUsers.get(i));
        }
    }
}
