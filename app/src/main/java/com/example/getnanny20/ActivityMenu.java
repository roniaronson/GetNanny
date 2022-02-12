package com.example.getnanny20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


public class ActivityMenu extends AppCompatActivity {
    MaterialButton menu_btn_parent;
    MaterialButton menu_btn_nanny;
    boolean isParent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();

        menu_btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isParent = true;
                finish();
                Intent intent = new Intent(ActivityMenu.this, ActivityAddPost.class);
                intent.putExtra("isParent", true);
                startActivity(intent);
            }
        });

        menu_btn_nanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isParent = false;
                finish();
                Intent intent = new Intent(ActivityMenu.this, ActivityAddPost.class);
                intent.putExtra("isParent", false);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        menu_btn_parent = findViewById(R.id.menu_btn_parent);
        menu_btn_nanny = findViewById(R.id.menu_btn_nanny);
    };
}



