package com.kpf.sujeet.chat.Fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.chat.Adapter.ContactListRecyclerAdapter;
import com.kpf.sujeet.chat.Models.User;
import com.kpf.sujeet.chat.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment {
    List<User> phone_contact_list;
    List<User> userList;
    List<User> filtered_userList;
    RecyclerView recyclerView;


    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        phone_contact_list = new ArrayList<User>();
        userList = new ArrayList<User>();
        filtered_userList =new ArrayList<User>();
        recyclerView =(RecyclerView)inflater.inflate(R.layout.fragment_chatlist, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // Inflate the layout for this fragment
        return  recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        phone_contact_list.clear();
        userList.clear();
        filtered_userList.clear();

        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+") ASC");
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
                    Cursor pCur = getActivity().getContentResolver().query(ContactsContract.
                            CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},null);
                    while (pCur.moveToNext()){
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        User user = new User();
                        user.name = contactName+" -> "+contactNumber;
                        user.contact = contactNumber;
                        phone_contact_list.add(user);
                        break;
                    }
                    pCur.close();
                }

            }while(cursor.moveToNext());
//            ContactListRecyclerAdapter chatListRecyclerAdapter = new ContactListRecyclerAdapter(getActivity(),phone_contact_list);
//            recyclerView.setAdapter(chatListRecyclerAdapter);

        }

        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                    Iterator iterator1 = dataSnapshot1.getChildren().iterator();
                    User user = new User();
                    user.uid = dataSnapshot1.getKey();
                    while (iterator1.hasNext())
                    {

                        DataSnapshot dataSnapshot2 = (DataSnapshot) iterator1.next();
                        if(dataSnapshot2.getKey().equals("name")){
                            user.name = dataSnapshot2.getValue().toString();
                        }
                        if(dataSnapshot2.getKey().equals("email")){
                            user.email = dataSnapshot2.getValue().toString();
                        }
                        if(dataSnapshot2.getKey().equals("contact")){
                            user.contact = dataSnapshot2.getValue().toString();
                        }
                        if(dataSnapshot2.getKey().equals("picture_url")){
                            user.picture_url = dataSnapshot2.getValue().toString();
                        }
                    }
                    userList.add(user);

                }

                Log.d("Userlist","List data"+userList.toString()+"======="+ phone_contact_list.toString());


                for(User firebase_user:userList){
                    for(User phone_contact_user:phone_contact_list){
                        String contactno= phone_contact_user.contact;
                        contactno = contactno.replaceAll(" ","");
                        if(contactno.length()>=10) {
                            contactno = contactno.substring(contactno.length() - 10);
                            if (firebase_user.contact.equals(contactno) && !firebase_user.contact.equals("")) {
                                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(firebase_user.uid)){

                                }else{
                                    filtered_userList.add(firebase_user);
                                }
                                break;
                            }
                        }
                    }
                }
                ContactListRecyclerAdapter chatListRecyclerAdapter = new ContactListRecyclerAdapter(getActivity(),filtered_userList);
                recyclerView.setAdapter(chatListRecyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
        

    }

}
