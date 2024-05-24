package com.challenger.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.challenger.app.R;

public class IntroActivity extends AppCompatActivity {

    Intent intent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goLoginPage(View view) {
        Log.d("Navigation", "Navigating to LoginActivity");
        intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void goRegisterPage(View view) {
        Log.d("Navigation", "Navigating to RegisterActivity");
        intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }


    public void goMenu(View view) {
        intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }
}