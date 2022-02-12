package com.example.getnanny20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class ActivityLottieParent extends AppCompatActivity {

    private LottieAnimationView lottieparent_SPC_mom;
    private MaterialButton lottieparent_BTN_next;
    private MaterialTextView lottieparent_TXT_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_parent);
        findViews();


        lottieparent_BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityLottieParent.this, ActivityLottieBabysitter.class);
                startActivity(intent);
            }
        });
        lottieparent_TXT_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ActivityLottieParent.this, ActivityStart.class);
                startActivity(intent);
            }
        });
    }
    public void findViews(){
        lottieparent_SPC_mom = findViewById(R.id.lottieparent_SPC_mom);
        lottieparent_BTN_next =findViewById(R.id.lottieparent_BTN_next);
        lottieparent_TXT_skip = findViewById(R.id.lottieparent_TXT_skip);
    }
}

