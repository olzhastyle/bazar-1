package com.example.baza_r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar)findViewById(R.id.find_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_people_button);
        SearchInputText = (EditText)findViewById(R.id.search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();

                SearchPeople(searchBoxInput);
            }
        });
    }

    private void SearchPeople(String searchBoxInput) {
        Toast.makeText(this, "Searching", Toast.LENGTH_LONG).show();

        Query searchPeopleQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        
        FirebaseRecyclerAdapter<FindPeople, FindPeopleViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindPeople, FindPeopleViewHolder>(
                        FindPeople.class,
                        R.layout.all_users_display_layout,
                        FindPeopleViewHolder.class,
                        searchPeopleQuery

        ) {
            @Override
            protected void populateViewHolder(FindPeopleViewHolder findPeopleViewHolder, FindPeople findPeople, int position) {
                findPeopleViewHolder.setFullname(findPeople.getFullname());
                findPeopleViewHolder.setStatus(findPeople.getStatus());
                findPeopleViewHolder.setProfileImage(findPeople.getProfileImage());

                findPeopleViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindPeopleViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(String profileImage){
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileImage).placeholder(R.drawable.avatar).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullname);
        }

        public void setStatus(String status) {
            TextView myStatus = (TextView) mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }
}