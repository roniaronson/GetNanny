package com.example.getnanny20;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class FragmentList extends Fragment {
    private RecyclerView board_LST_posts;
    private boolean isParent;
    private AppCompatActivity activity;
    private CallBack_List callBack_list;


    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setCallBackList(CallBack_List callBack_list) {
        this.callBack_list = callBack_list;
    }

    public void setIsParent(boolean isParent){ this.isParent = isParent;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        findViews(view);
        initViews(view);

        return view;
    }


    private void initViews(View view) {
        MyFirebaseDB.CallBack_Users callBack_users = new MyFirebaseDB.CallBack_Users() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void dataReady(ArrayList<User> users) {
                ArrayList<Post> posts = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    if (!users.get(i).getPost().getDescription().equals("")) {
                        if(users.get(i).getPost().getIsParent() ^ isParent)
                            posts.add(users.get(i).getPost());
                    }
                }

                AdapterPost adapterPost = new AdapterPost(getActivity(), posts, isParent);

                board_LST_posts.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                board_LST_posts.setHasFixedSize(true);
                board_LST_posts.setItemAnimator(new DefaultItemAnimator());
                board_LST_posts.setAdapter(adapterPost);

                adapterPost.setPostItemClickListener(new AdapterPost.PostItemClickListener() {
                    @Override
                    public void postItemClicked(Post post, int pos) {
                        double lat = post.getLat();
                        double lon = post.getLon();
                        callBack_list.getPostLocation(lat, lon);
                    }
                });
            }
        };
        MyFirebaseDB.getUsers(callBack_users);
    }

    private void findViews(View view) {
        board_LST_posts = view.findViewById(R.id.board_LST_posts);
    }
}