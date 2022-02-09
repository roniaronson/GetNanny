package com.example.getnanny20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


public class ActivityMenu extends AppCompatActivity {
    MaterialButton menu_btn_parent;
    MaterialButton menu_btn_nanny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();

        menu_btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ActivityMenu.this, ActivityMap.class);
                startActivity(intent);
            }
        });

        menu_btn_nanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ActivityMenu.this, ActivityAddPost.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        menu_btn_parent = findViewById(R.id.menu_btn_parent);
        menu_btn_nanny = findViewById(R.id.menu_btn_nanny);
    };
}



