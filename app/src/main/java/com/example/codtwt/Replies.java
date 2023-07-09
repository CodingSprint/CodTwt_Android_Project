package com.example.codtwt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Replies extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private chat chatAdapter;
    private FirebaseDatabase database;
    private DatabaseReference chatRef;
    private String name,uid,profileImgUrl,msg;
    private ImageView sendMsg;
    private EditText typeMsg;
    private List<ChatMessage> chatMessages;
    private ReplyHandler rp;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);
        Bundle extras=getIntent().getExtras();
        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        if(extras!=null){

            String id=extras.getString("key");
            rp=new ReplyHandler(id);
            {
                recyclerView = findViewById(R.id.chat_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                chatMessages = new ArrayList<>();
                chatAdapter = new chat(this, chatMessages);
                recyclerView.setAdapter(chatAdapter);
                rp.readDataFromDatabase(chatAdapter, chatMessages);

            }
            {
                name = currentUser.getDisplayName();
                profileImgUrl = currentUser.getPhotoUrl().toString();
                sendMsg = findViewById(R.id.sendMsg);
                typeMsg = findViewById(R.id.typeMsg);
                sendMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        msg = typeMsg.getText().toString();
                        rp.sendMessage(name, msg, uid, profileImgUrl);
                        typeMsg.setText("");
                    }
                });
            }
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    chatMessages.clear();
                    chatAdapter.notifyDataSetChanged();
                    rp.readDataFromDatabase(chatAdapter, chatMessages);
                    recyclerView.scrollToPosition(chatMessages.size()-1);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }
}