package com.kpf.sujeet.chat.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.chat.Adapter.ChatMsgAdapter;
import com.kpf.sujeet.chat.Models.Chat;
import com.kpf.sujeet.chat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String other_user_uid;
    ListView listView;
    List<Chat> chatList;
    ChatMsgAdapter chatMsgAdapter;
    ImageButton sendButton;
    DatabaseReference databaseReference;
    String name = "";
    String currentUserId = "";
    ValueEventListener valueEventListener;
    String chatRoomId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatList = new ArrayList<Chat>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        if(intent != null){
            name = intent.getStringExtra("name");
            other_user_uid = intent.getStringExtra("other_user_uid");
            if(!name.equals("")){
                setTitle(name);
            }
        }

        listView = (ListView)findViewById(R.id.listView);
        sendButton = (ImageButton)findViewById(R.id.sendMessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();

                if(dataSnapshot.getValue() != null){
                    DataSnapshot child;
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        child = (DataSnapshot)iterator.next();
                        if(child.getKey().equals("chat")){
                            Iterator chatRoomIterator = child.getChildren().iterator();
                            while (chatRoomIterator.hasNext()){
                                DataSnapshot chatRoomSnaphot = (DataSnapshot)chatRoomIterator.next();
                                if(chatRoomSnaphot.getKey().equals(currentUserId+":"+other_user_uid)){

                                    chatRoomId = currentUserId+":"+other_user_uid+"";

                                    Iterator iterator1 = chatRoomSnaphot.getChildren().iterator();
                                    while (iterator1.hasNext()){
                                        DataSnapshot snapshot = (DataSnapshot)iterator1.next();

                                        Chat chat = snapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                    }
                                }else if(chatRoomSnaphot.getKey().equals(other_user_uid+":"+currentUserId)){

                                    chatRoomId = other_user_uid+":"+currentUserId+"";

                                    Iterator iterator1 =chatRoomSnaphot.getChildren().iterator();
                                    while (iterator1.hasNext()){
                                        DataSnapshot snapshot = (DataSnapshot)iterator1.next();

                                        Chat chat = snapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                    }
                                }
                            }
                        }
                    }
                }

                ChatMsgAdapter chatMsgAdapter = new ChatMsgAdapter(ChatActivity.this,chatList);
                listView.setAdapter(chatMsgAdapter);
                listView.setSelection(chatList.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(eventListener);

        valueEventListener = eventListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(valueEventListener != null){
            databaseReference.child("chat").removeEventListener(valueEventListener);
        }
    }
//Ask to Sir
    public  void sendMessage(){
        String timeStamp = "";
        EditText inputText = (EditText)findViewById(R.id.messageEditText);
        String input = inputText.getText().toString();
        if(chatRoomId.equals("")){
            chatRoomId = currentUserId+":"+other_user_uid;
        }
        if(!input.equals("") && !currentUserId.equals("") && !other_user_uid.equals("")){
            Long timeStampLong = System.currentTimeMillis()/1000;
            timeStamp = timeStampLong.toString();
            Chat chat = new Chat(currentUserId,input,timeStamp,true);
            Map<String,Object> postValues = chat.tomap();

            Map<String,Object>childUpdates = new HashMap<>();
            childUpdates.put("/chat/"+chatRoomId+"/"+timeStamp,postValues);

            databaseReference.updateChildren(childUpdates);

            inputText.setText("");
        }
    }
}
