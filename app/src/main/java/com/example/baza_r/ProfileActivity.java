package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        userName = (TextView) findViewById(R.id.my_username);
        userProfName = (TextView) findViewById(R.id.my_profile_full_name);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userCountry = (TextView) findViewById(R.id.my_country);
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation = (TextView) findViewById(R.id.my_relationship_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.my_profile_pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myProfileImage = snapshot.child("profileimage").getValue().toString();
                String myUserName = snapshot.child("username").getValue().toString();
                String myUserProfileName = snapshot.child("fullname").getValue().toString();
                String myProfileStatus = snapshot.child("status").getValue().toString();
                String myDOB = snapshot.child("dob").getValue().toString();
                String myCountry = snapshot.child("countryname").getValue().toString();
                String myGender = snapshot.child("gender").getValue().toString();
                String myRelationStatus = snapshot.child("relationship").getValue().toString();

                Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

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
}