package com.example.getnanny20;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<>();
    private PostItemClickListener postItemClickListener;
    private boolean isParent;
    //private Button post_BTN_details;

    public AdapterPost(FragmentActivity activity, ArrayList<Post> posts, boolean isParent) {
        this.activity = activity;
        this.posts = posts;
        this.isParent = isParent;
    }

    public AdapterPost setPostItemClickListener(PostItemClickListener postItemClickListener) {
        this.postItemClickListener = postItemClickListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_posts, viewGroup, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;
        Post post = getItem(position);
        postViewHolder.post_txt_name.setText(""+post.getName());
        postViewHolder.post_txt_description.setText(""+post.getDescription());
        if(isParent){
            postViewHolder.post_txt_age.setText(""+post.getAge()+" Years Old");
            postViewHolder.post_txt_hourlyRate.setText("  "+post.getHourlyRate()+"$");
            postViewHolder.post_txt_experience.setText(post.getYearsOfExperience()+" Years Of Experience");
        }
        else{
            postViewHolder.post_txt_age.setText(""+post.getNumOfChildren()+" Children");
            postViewHolder.post_txt_experience.setText(post.getDateString());
            postViewHolder.post_txt_hourlyRate.setText("");
        }

        Glide
                .with(activity)
                .load(post.getImage())
                .into(postViewHolder.post_img_image);

    }

    @Override
    public int getItemCount() { return posts.size(); }

    private Post getItem(int position) {
        return posts.get(position);
    }



    public interface PostItemClickListener {
        void postItemClicked(Post post, int pos);
    }



    public class PostViewHolder extends RecyclerView.ViewHolder {

        public MaterialTextView post_txt_name;
        public MaterialTextView post_txt_hourlyRate;
        public MaterialTextView post_txt_age;
        public MaterialTextView post_txt_experience;
        public MaterialTextView post_txt_description;
        public ShapeableImageView post_img_image;


        public PostViewHolder(final View itemView) {
            super(itemView);
            this.post_txt_name = itemView.findViewById(R.id.post_txt_name);
            this.post_txt_hourlyRate = itemView.findViewById(R.id.post_txt_hourlyRate);
            this.post_txt_age = itemView.findViewById(R.id.post_txt_age);
            this.post_txt_experience = itemView.findViewById(R.id.post_txt_experience);
            this.post_txt_description = itemView.findViewById(R.id.post_txt_description);
            this.post_img_image = itemView.findViewById(R.id.post_img_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postItemClickListener.postItemClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });


        }



    }

}

