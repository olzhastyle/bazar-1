package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton, DeclineRequestButton;

    private DatabaseReference FriendRequestRef, UserRef;
    private FirebaseAuth mAuth;
    private String senderUserId, recieverUserId, CURRENT_STATE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();

        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        InitializeFields();

        UserRef.child(recieverUserId).addValueEventListener(new ValueEventListener() {
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

                if (myProfileImage.isEmpty()){
                    return;
                }
                else {
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                }

                userName.setText("@" + myUserName);
                userProfName.setText(myUserProfileName);
                userStatus.setText(myProfileStatus);
                userDOB.setText("DOB" + myDOB);
                userCountry.setText("Country" + myCountry);
                userGender.setText("Gender" + myGender);
                userRelation.setText("Relation" + myRelationStatus);

                MaintanenceofButtons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DeclineRequestButton.setVisibility(View.INVISIBLE);
        DeclineRequestButton.setEnabled(false);

        if (!senderUserId.equals(recieverUserId)){
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestButton.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToaPerson();

                    }
                }
            });
        }
        else    {
            DeclineRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void MaintanenceofButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(recieverUserId))
                        {
                            String request_type = snapshot.child(recieverUserId).child("request_type")
                                    .getValue().toString();
                            if (request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                SendFriendRequestButton.setText("Cancel friend request");

                                DeclineRequestButton.setVisibility(View.INVISIBLE);
                                DeclineRequestButton.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(recieverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FriendRequestRef.child(recieverUserId).child(senderUserId)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                SendFriendRequestButton.setText("Cancel friend request");

                                                DeclineRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {
        userName = (TextView) findViewById(R.id.person_username);
        userProfName = (TextView) findViewById(R.id.person_full_name);
        userStatus = (TextView) findViewById(R.id.person_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_relationship_status);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.person_profile_pic);

        SendFriendRequestButton = (Button) findViewById(R.id.person_send_friend_request_button);
        DeclineRequestButton = (Button) findViewById(R.id.person_decline_friend_request_button);

        CURRENT_STATE = "not_friends";
    }
}