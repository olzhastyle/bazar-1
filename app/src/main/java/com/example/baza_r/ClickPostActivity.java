package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton, EditPostButton;

    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserID, databaseUserID, description, image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView) findViewById(R.id.click_post_image);
        PostDescription = (TextView)findViewById(R.id.click_post_description);
        DeletePostButton = (Button) findViewById(R.id.delete_post_button);
        EditPostButton = (Button)findViewById(R.id.edit_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    description = snapshot.child("description").getValue().toString();
                    //image = snapshot.child("postimage").getValue().toString();
                    databaseUserID = snapshot.child("uid").getValue().toString();

                    PostDescription.setText(description);
//                    if (PostImage !=null) {
//                        Picasso.get().load(image).into(PostImage);
//                        PostImage.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        PostImage.setVisibility(View.GONE);
//                    }

                    if (currentUserID.equals(databaseUserID)){
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost();
                        }
                    });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
                Toast.makeText(ClickPostActivity.this, "Post was deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void EditCurrentPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post has been updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }

    private void DeleteCurrentPost() {
        ClickPostRef.removeValue();

        SendUserToMainActivity();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }


}