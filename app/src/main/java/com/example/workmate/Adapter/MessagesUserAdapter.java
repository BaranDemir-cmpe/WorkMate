package com.example.workmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workmate.Activity.ChatActivity;
import com.example.workmate.Model.User;
import com.example.workmate.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesUserAdapter extends RecyclerView.Adapter<MessagesUserAdapter.MyViewHolder> {

    private Context context;
    private List<User> userList; // gösterilen liste
    private List<User> userListFull; // tüm kullanıcılar

    public MessagesUserAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList; // gösterilen liste
        this.userListFull = new ArrayList<>(userList);  // tüm kullanıcılar
    }

    // YENİ METOD EKLE - userListFull'u güncellemek için
    public void updateFullList(List<User> newUserList) {
        this.userListFull.clear();
        this.userListFull.addAll(newUserList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_message_item,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessagesUserAdapter.MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textView_receiver_name.setText(user.getName());
        holder.textView_receiver_surname.setText(user.getSurname());

        // Glide ile profil resmi yükleme
        Glide.with(context).load(user.getProfilResimleriUrl()).into(holder.profile_picture_receiver);

        // Mesajlaşma ekranına geçiş
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context, ChatActivity.class);
               intent.putExtra("receiverId",user.getUserId());
               intent.putExtra("receiverName",user.getName());
               intent.putExtra("receiverSurname",user.getSurname());
               intent.putExtra("profileImageUrl",user.getProfilResimleriUrl());
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_picture_receiver;
        TextView textView_receiver_name;
        TextView textView_receiver_surname;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_picture_receiver = itemView.findViewById(R.id.profile_picture_messages);
            textView_receiver_name = itemView.findViewById(R.id.message_user_name);
            textView_receiver_surname = itemView.findViewById(R.id.message_user_surname);
        }
    }

    public void filter(String text){
        userList.clear();
        if(text.isEmpty()){
            userList.addAll(userListFull);
        }else{
            text = text.toLowerCase();
            for(User user: userListFull){
                String fullname = (user.getName() +" "+ user.getSurname()).toLowerCase();
                if(fullname.contains(text)){
                    userList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }


}
