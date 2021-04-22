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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton, DeclineRequestButton;

    private DatabaseReference FriendRequestRef, UserRef, FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId, recieverUserId, CURRENT_STATE, saveCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();

        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

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
                    if (CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")){
                        UnfriendAndExistingFriend();
                    }
                }
            });
        }
        else    {
            DeclineRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void UnfriendAndExistingFriend() {
        FriendsRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FriendsRef.child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DeclineRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsRef.child(senderUserId).child(recieverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FriendsRef.child(recieverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FriendRequestRef.child(senderUserId).child(recieverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    FriendRequestRef.child(recieverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        SendFriendRequestButton.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        SendFriendRequestButton.setText("Unfriend this person");

                                                                                        DeclineRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineRequestButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FriendRequestRef.child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend request");

                                                DeclineRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
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
                            else if (request_type.equals("received")) {
                                CURRENT_STATE = "request_received";
                                SendFriendRequestButton.setText("Accept Friend request");

                                DeclineRequestButton.setVisibility(View.VISIBLE);
                                DeclineRequestButton.setEnabled(true);

                                DeclineRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                            else {
                                FriendRequestRef.child(senderUserId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild(recieverUserId)){
                                                    CURRENT_STATE = "friends";
                                                    SendFriendRequestButton.setText("Unfriend this person");

                                                    DeclineRequestButton.setVisibility(View.INVISIBLE);
                                                    DeclineRequestButton.setEnabled(false);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
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
                                    .child("request_type").setValue("received")
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