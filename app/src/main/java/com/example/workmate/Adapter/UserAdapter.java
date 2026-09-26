package com.example.workmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workmate.Activity.ChatActivity;
import com.example.workmate.Model.UserProfile;
import com.example.workmate.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<UserProfile> userList;
    private Context context;

    public UserAdapter(List<UserProfile> userList, Context context){
        this.userList = userList;
        this.context = context;
    }
    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        UserProfile user = userList.get(position);
        holder.name.setText(user.getName());
        holder.surname.setText(user.getSurname());
        holder.profession.setText(user.getProfession());
        holder.location.setText(user.getLocation());
        holder.educationStatus.setText(user.getEducationStatus());
        holder.gender.setText(user.getGender());

        String profile_image_url = user.getProfilResimleriUrl();

        Glide.with(context).load(profile_image_url).into(holder.profile_image);

        holder.applicant_cv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cvUrl = user.getCvUrl();
                if(cvUrl != null && !cvUrl.isEmpty()){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(cvUrl));
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context,"Bu kullanıcı CV yüklememiş.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.applicant_message_icon.setOnClickListener(new View.OnClickListener() {
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
        TextView name, surname, profession, location, educationStatus, gender;
        ImageView applicant_message_icon, applicant_cv_icon,profile_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_user_name);
            surname = itemView.findViewById(R.id.text_user_surname);
            profession = itemView.findViewById(R.id.text_user_profession);
            location = itemView.findViewById(R.id.text_user_location);
            educationStatus = itemView.findViewById(R.id.text_user_educationStatus);
            gender = itemView.findViewById(R.id.text_user_gender);
            applicant_message_icon = itemView.findViewById(R.id.applicant_message_icon);
            applicant_cv_icon = itemView.findViewById(R.id.applicant_cv_icon);
            profile_image = itemView.findViewById(R.id.profile_image_user_item);
        }
    }
}
