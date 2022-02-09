package com.example.getnanny20;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

    public class MyFirebaseDB {

        public interface CallBack_Users {
            void dataReady(ArrayList<User> users);
        }

        public static void getUsers(CallBack_Users callBack_users) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://get-nanny-1a620-default-rtdb.firebaseio.com/");
            DatabaseReference myRef = database.getReference("users");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<User> users = new ArrayList<>();
                    ArrayList<Post> posts = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        try {
                            User user = child.getValue(User.class);
                            users.add(user);
                            Post post = user.getPost();
                            if(!post.getDescription().equals("")){
                                posts.add(post);
                            }
                        }catch (Exception ex) {}
                    }

                    if (callBack_users != null)
                        callBack_users.dataReady(users);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {  }
            });
        }
    }


