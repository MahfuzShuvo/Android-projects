package com.example.captainjacksparrow.chitchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        //mUsersList.setHasFixedSize(true);
        //mUsersList.setLayoutManager(new LinearLayoutManager(this));




        final List<Users> listUsers;
        listUsers = new ArrayList<>();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {

                    Userdetails userdetails = dataSnapshot1.getValue(Userdetails.class);

                    String u_name = userdetails.getName();
                    String u_status = userdetails.getStatus();
                    String u_thumb_image = userdetails.getThumb_image();

                    Users us = new Users();
                    us.setName(u_name);
                    us.setStatus(u_status);
                    us.setThumb_image(u_thumb_image);
                    listUsers.add(us);

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UsersActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });



        RecyclerviewAdapter recycler = new RecyclerviewAdapter(listUsers);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(UsersActivity.this);
        mUsersList.setLayoutManager(layoutmanager);
        mUsersList.setItemAnimator( new DefaultItemAnimator());
        mUsersList.setAdapter(recycler);

    }



}
