package com.kpf.sujeet.chat.Models;

/**
 * Created by SUJEET on 12/29/2016.
 */

public class User {

    public String name = "";
    public String email = "";
    public String contact = "";
    public String uid = "";
    public String picture_url = "";

    public User(){

    }
    public User(String name, String email, String contact, String uid,String picture_url){
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.uid = uid;
        this.picture_url = picture_url;
    }
}
