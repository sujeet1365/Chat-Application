package com.kpf.sujeet.chat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kpf.sujeet.chat.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash_Screen_Activity extends Activity implements Animation.AnimationListener
{
    FirebaseUser firebaseUser;
    Animation animation;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen_);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView)findViewById(R.id.img_splash_screen);
        textView = (TextView)findViewById(R.id.txt_spash) ;


        animation= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        animation.setAnimationListener(this);
//        textView.startAnimation(animation);
        imageView.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(firebaseUser != null){
                    startActivity(new Intent(Splash_Screen_Activity.this,HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(Splash_Screen_Activity.this,Login_Activity.class));
                    finish();
                }

            }
        },2000);

// for generating hash key
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.kpf.sujeet.chat",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
