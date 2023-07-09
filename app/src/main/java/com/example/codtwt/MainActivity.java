package com.example.codtwt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN=9001;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private updateDb updb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        updb=new updateDb();
        if(currentUser==null){
            signIn();
        }
        else {
            updateUI(currentUser);
        }
        currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {

            {
                recyclerView = findViewById(R.id.chat_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                chatMessages = new ArrayList<>();
                chatAdapter = new chat(this, chatMessages);
                recyclerView.setAdapter(chatAdapter);
                updb.readDataFromDatabase(chatAdapter, chatMessages);

            }
            {
                name = currentUser.getDisplayName();
                uid = currentUser.getUid();
                profileImgUrl = currentUser.getPhotoUrl().toString();
                sendMsg = findViewById(R.id.sendMsg);
                typeMsg = findViewById(R.id.typeMsg);
                sendMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        msg = typeMsg.getText().toString();
                        updb.sendMessage(name, msg, uid, profileImgUrl);
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
                    updb.readDataFromDatabase(chatAdapter, chatMessages);
                    recyclerView.scrollToPosition(chatMessages.size()-1);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
}

    private  void signIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google sign-in failed
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Sign-in failed
                            Toast.makeText(MainActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        LinearLayout signInLayout=findViewById(R.id.signInLayout);

        if (user != null) {
            signInLayout.setVisibility(View.INVISIBLE);
//            TextView name=findViewById(R.id.name);
//            name.setText(user.getDisplayName().toString());
        } else {
            signInLayout.setVisibility(View.VISIBLE);
        }
    }
}