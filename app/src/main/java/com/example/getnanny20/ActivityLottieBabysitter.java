package com.example.getnanny20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class ActivityLottieBabysitter extends AppCompatActivity {

    private LottieAnimationView lottiebabysitter_SPC_babysitter;
    private MaterialButton lottiebabysitter_BTN_next;
    private MaterialTextView lottiebabysitter_TXT_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_babysitter);
        findViews();


        lottiebabysitter_BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityLottieBabysitter.this, ActivityStart.class);
                startActivity(intent);
            }
        });
        lottiebabysitter_TXT_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityLottieBabysitter.this, ActivityStart.class);
                startActivity(intent);
            }
        });
    }
    public void findViews(){
        lottiebabysitter_SPC_babysitter = findViewById(R.id.lottiebabysitter_SPC_babysitter);
        lottiebabysitter_BTN_next=findViewById(R.id.lottiebabysitter_BTN_next);
        lottiebabysitter_TXT_skip = findViewById(R.id.lottiebabysitter_TXT_skip);
    }
}


