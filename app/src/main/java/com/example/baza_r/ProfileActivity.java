package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, friendsRef, postsRef;
    private FirebaseAuth mAuth;

    private Button myPostsButton, myFriendsButton;

    private Toolbar mToolbar;

    private String currentUserId;

    private int countFriends = 0, countPosts = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mToolbar = (Toolbar)findViewById(R.id.profile_app_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        userName = (TextView) findViewById(R.id.my_username);
        userProfName = (TextView) findViewById(R.id.my_profile_full_name);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userCountry = (TextView) findViewById(R.id.my_country);
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation = (TextView) findViewById(R.id.my_relationship_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.my_profile_pic);
        myFriendsButton = (Button) findViewById(R.id.my_friends_button);
        myPostsButton = (Button) findViewById(R.id.my_post_button);

        myFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToFriendsActivity();
            }
        });

        myPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMyPostsActivity();
            }
        });

        friendsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countFriends = (int) snapshot.getChildrenCount();
                    myFriendsButton.setText(Integer.toString(countFriends) + " friends");
                }
                else {
                    myFriendsButton.setText("0 friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postsRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId + "/uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            countPosts = (int) snapshot.getChildrenCount();
                            myPostsButton.setText(Integer.toString(countPosts) + " Posts");
                        }
                        else {
                            myPostsButton.setText("0 Posts");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String myProfileImage = snapshot.child("profileimage").getValue().toString();
                String myUserName = snapshot.child("username").getValue().toString();
                String myUserProfileName = snapshot.child("fullname").getValue().toString();
                String myProfileStatus = snapshot.child("status").getValue().toString();
                String myDOB = snapshot.child("dob").getValue().toString();
                String myCountry = snapshot.child("countryname").getValue().toString();
                String myGender = snapshot.child("gender").getValue().toString();
                String myRelationStatus = snapshot.child("relationship").getValue().toString();

                if (snapshot.hasChild("profileimage")){

                    String image = snapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.avatar).into(userProfileImage);
                }
//                else {
//                    Picasso.get().load(myProfileImage).placeholder(R.drawable.avatar).into(userProfileImage);
//                }


                userName.setText("@" + myUserName);
                userProfName.setText(myUserProfileName);
                userStatus.setText(myProfileStatus);
                userDOB.setText("DOB" + myDOB);
                userCountry.setText("Country" + myCountry);
                userGender.setText("Gender" + myGender);
                userRelation.setText("Relation" + myRelationStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(friendsIntent);
    }
    private void SendUserToMyPostsActivity() {
        Intent postsIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(postsIntent);
    }
}