package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView myPostsList;

    private FirebaseAuth mAuth;
    private DatabaseReference postsRef, usersRef;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.my_post_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        myPostsList = (RecyclerView) findViewById(R.id.my_all_posts_list);
        myPostsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostsList.setLayoutManager(linearLayoutManager);

        DisplayMyAllPosts();
    }

    private void DisplayMyAllPosts() {

        Query myPostsQuery = postsRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId + "/uf8ff");

        FirebaseRecyclerAdapter<Posts, MyPostsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Posts, MyPostsViewHolder>
                (
                        Posts.class,
                        R.layout.all_posts_layout,
                        MyPostsViewHolder.class,
                        myPostsQuery


                ) {
            @Override
            protected void populateViewHolder(MyPostsViewHolder myPostsViewHolder, Posts posts, int i) {
                final String PostKey = getRef(i).getKey();

                myPostsViewHolder.setFullname(posts.getFullname());
                myPostsViewHolder.setTime(posts.getTime());
                myPostsViewHolder.setDate(posts.getDate());
                myPostsViewHolder.setDescription(posts.getDescription());
                myPostsViewHolder.setProfileimage(posts.getProfileimage());
                myPostsViewHolder.setPostimage(posts.getPostimage());
            }
        };

        myPostsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setFullname(String fullname) {
            TextView username  = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }
        public void setProfileimage(String profileimage) {
            CircleImageView profile_image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(profile_image);
        }
        public void setTime(String time) {
            TextView postTime  = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("   " + time);
        }
        public void setDate(String date) {
            TextView postDate  = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("   " + date);
        }
        public void setDescription(String description) {
            TextView postDescription  = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }
        public void setPostimage(String postimage) {

            if (postimage != null){
                ImageView post_Image = (ImageView) mView.findViewById(R.id.post_image);
                Picasso.get().load(postimage).into(post_Image);
                //пока не разобрался с проблемой изображения оставлю все так
                //post_Image.setVisibility(View.VISIBLE);
            }

        }
    }

}