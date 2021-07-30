package com.amupys.invoidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amupys.myinvoidlibrary.InvoidVerification;

public class MainActivity extends AppCompatActivity {

    TextView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_verify);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoidVerification invoid = new InvoidVerification();
                invoid.verify(MainActivity.this, WelcomeActivity.class);
            }
        });
    }
}