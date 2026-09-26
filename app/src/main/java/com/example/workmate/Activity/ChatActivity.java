package com.example.workmate.Activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workmate.Adapter.MessageAdapter;
import com.example.workmate.Model.Message;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String receiverId, receiverName,receiverSurname,receiverImageUrl,receiverImageUrl2;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private ImageView profil_picture;
    private TextView textView_receiverName, textView_receiverSurname;
    private EditText editTextMessage;
    private ImageButton sendButton;
    private RecyclerView recyclerViewMessages;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        editTextMessage = findViewById(R.id.editText_Message);
        sendButton = findViewById(R.id.message_sent_btn);
        recyclerViewMessages = findViewById(R.id.recyclerView_messages);
        profil_picture = findViewById(R.id.profile_picture_image);

        // Karşı taraf bilgisi
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverSurname = getIntent().getStringExtra("receiverSurname");
        receiverImageUrl = getIntent().getStringExtra("profileImageUrl");


        // Toolbarda kullanıcı bilgilerini gösterme
        textView_receiverName = findViewById(R.id.receiver_name);
        textView_receiverSurname = findViewById(R.id.receiver_Surname);
        textView_receiverName.setText(receiverName);
        textView_receiverSurname.setText(receiverSurname);


        if(receiverImageUrl != null && !receiverImageUrl.isEmpty()){
            Glide.with(this).load(receiverImageUrl).into(profil_picture);
        }


        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this,messageList,auth.getCurrentUser().getUid());

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Gönderme butonu
        sendButton.setOnClickListener(v -> sendMessage());

        // Mesajları dinle
        loadMessages();
    }
    private void sendMessage(){
        String msg = editTextMessage.getText().toString().trim();
        if(msg.isEmpty()){
            return;
        }

        Message message = new Message(auth.getCurrentUser().getUid(),receiverId, msg,System.currentTimeMillis());

        db.collection("messages").add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                editTextMessage.setText("");
            }
        });
    }

    public void loadMessages(){
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("messages").orderBy("timestamp").addSnapshotListener((querySnapshot,error)->{
            if(error != null){
                return;
            }
            messageList.clear();
            for(DocumentSnapshot doc: querySnapshot.getDocuments()){
                Message message = doc.toObject(Message.class);
                if((message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(receiverId)) ||
                        (message.getSenderId().equals(receiverId) && message.getReceiverId().equals(currentUserId))){
                    messageList.add(message);
                }
            }
            messageAdapter.notifyDataSetChanged();
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        });
    }
}