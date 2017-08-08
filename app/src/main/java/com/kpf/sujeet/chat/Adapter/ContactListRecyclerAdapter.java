package com.kpf.sujeet.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kpf.sujeet.chat.Activity.ChatActivity;
import com.kpf.sujeet.chat.Models.User;
import com.kpf.sujeet.chat.R;
import com.kpf.sujeet.chat.Utils.AppController;

import java.util.List;

/**
 * Created by SUJEET on 1/7/2017.
 */



public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.MyViewHolder> {

    ImageLoader imageLoader;
    Context context;
    List<User> userList;
    public ContactListRecyclerAdapter(Context contex, List<User> userList){
        this.context = contex;
        this.userList=userList;
        imageLoader = AppController.getInstance().getImageLoader();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        NetworkImageView network_chat_image;
        TextView txt_chat_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            network_chat_image = (NetworkImageView)itemView.findViewById(R.id.network_chat_image);
            txt_chat_name = (TextView)itemView.findViewById(R.id.txt_chat_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            User user = userList.get(position);
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("other_user_uid",user.uid);
            intent.putExtra("name",user.name);
            context.startActivity(intent);
//            Toast.makeText(context, ""+ user.name+user.uid, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = userList.get(position);

        if(user.picture_url.equals("")) {
            holder.network_chat_image.setDefaultImageResId(R.drawable.man);
        }
        else{
            holder.network_chat_image.setImageUrl(user.picture_url, imageLoader);
        }
        holder.txt_chat_name.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
