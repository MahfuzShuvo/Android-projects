package com.example.captainjacksparrow.chitchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder> {

    List<Users> listUsers;

    //private DatabaseReference mUsersDatabase;
    //private FirebaseUser mUser;


    public RecyclerviewAdapter(List<Users> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        Users users = listUsers.get(position);
        holder.name.setText(users.getName());
        holder.status.setText(users.getStatus());
        //holder.usersImage.setImageURI(Uri.parse(users.getThumb_image()));

        Picasso.get().load(users.getThumb_image()).placeholder(R.drawable.default_avatar_large).into(holder.usersImage);

        //mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        final String user_id = String.valueOf(getItemId(position));

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent profileIntent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                //Context.startActivity(profileIntent);
                Toast.makeText(holder.itemView.getContext(), user_id, Toast.LENGTH_LONG).show();
                profileIntent.putExtra("user_id", user_id);
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), ProfileActivity.class));


            }
        });


    }


    @Override
    public int getItemCount() {

        return listUsers.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView name, status;
        CircleImageView usersImage;

        public MyHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.users_single_name);
            status = (TextView) itemView.findViewById(R.id.users_single_status);
            usersImage = (CircleImageView) itemView.findViewById(R.id.users_single_image);


        }
    }
}
