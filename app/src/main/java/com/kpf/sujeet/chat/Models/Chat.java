package com.kpf.sujeet.chat.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUJEET on 1/13/2017.
 */

public class Chat {
    public String author="";
    public String message="";
    public String time_stamp="";
    public String chat_dp_url;
    public String chat_user_name;
    public boolean ismine=false;

    public Chat(){
    }
    public Chat(String author,String msg,String time_stamp,boolean ismine){
        this.author = author;
        this.message = msg;
        this.time_stamp = time_stamp;
        this.ismine = ismine;
    }
    @Exclude
    public Map<String,Object>tomap(){
        HashMap<String,Object>result = new HashMap<>();
        result.put("message",message);
        result.put("author",author);
        result.put("time_stamp",time_stamp);
        result.put("ismine",ismine);

        return result;
    }
}
