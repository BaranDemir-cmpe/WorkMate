package com.example.workmate.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workmate.Adapter.MessagesUserAdapter;
import com.example.workmate.Adapter.UserAdapter;
import com.example.workmate.Model.User;
import com.example.workmate.Model.UserProfile;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView_userlist;
    private List<User> userlist;
    private MessagesUserAdapter messagesUserAdapter;

    private EditText editTextSearch;

    private FirebaseFirestore db;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);

        recyclerView_userlist = findViewById(R.id.recyclerView_UserList);
        recyclerView_userlist.setLayoutManager(new LinearLayoutManager(this));
        userlist = new ArrayList<>();
        messagesUserAdapter = new MessagesUserAdapter(this,userlist);
        recyclerView_userlist.setAdapter(messagesUserAdapter);

        editTextSearch = findViewById(R.id.editTextSearch);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUsers();

        // Arama çubuğu filtresi

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messagesUserAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }
    private void loadUsers(){
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userlist.clear();
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    if(!doc.getId().equals(currentUserId)){
                        User user = doc.toObject(User.class);
                        user.setUserId(doc.getId());
                        userlist.add(user);
                    }
                }
                messagesUserAdapter.updateFullList(userlist);
            }
        });
    }
}