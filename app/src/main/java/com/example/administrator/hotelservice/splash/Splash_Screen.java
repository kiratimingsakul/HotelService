package com.example.administrator.hotelservice.splash;




import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.login.Login;



public class Splash_Screen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen.this,Login.class);
                Splash_Screen.this.startActivity(intent);
                Splash_Screen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }


}

