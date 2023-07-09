package com.example.codtwt;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class ReplyHandler {
    FirebaseDatabase database;
    DatabaseReference chatRef;
    ReplyHandler(String id){
        database= FirebaseDatabase.getInstance();
        chatRef=database.getReference("replies").child(id);
    }
    void sendMessage(String name,String message,String uid,String profileImgUrl){
        DatabaseReference newMessageRef=chatRef.push();
        String messageId=newMessageRef.getKey();
        DatabaseReference messageDataRef=chatRef.child(messageId);
        messageDataRef.child("name").setValue(name);
        messageDataRef.child("profile").setValue(profileImgUrl);
        messageDataRef.child("message").setValue(message);
        messageDataRef.child("msgId").setValue(messageId);
        messageDataRef.child("likeCount").setValue(0);

    }

    void readDataFromDatabase(chat chatAdapter, List<ChatMessage> chatMessages){
        Query query=chatRef.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot messageSnapshot: snapshot.getChildren()){
                    String name=messageSnapshot.child("name").getValue(String.class);
                    String message=messageSnapshot.child("message").getValue(String.class);
                    String url=messageSnapshot.child("profile").getValue(String.class);
                    String msgId=messageSnapshot.child("msgId").getValue(String.class);
                    int likeCount=messageSnapshot.child("likeCount").getValue(Integer.class);
                    Log.d("debug", "onDataChange: "+name);
                    ChatMessage chatMsg=new ChatMessage(name,message,url,msgId,likeCount);
                    chatMessages.add(chatMsg);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void updateLike(String id, Context context){
        chatRef=database.getReference("chat").child(id);
        chatRef.child("likeCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int currentLikeCount=dataSnapshot.getValue(Integer.class);
                    int newLikeCount=currentLikeCount+1;
                    chatRef.child("likeCount").setValue(newLikeCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
