package com.example.jacques.personel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TellMeActivity extends AppCompatActivity {

    private TextView tvHelloMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tellme);
        tvHelloMessage = (TextView) findViewById(R.id.tvHelloMessage);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        String age = intent.getStringExtra(MainActivity.EXTRA_AGE);
        String gender = intent.getStringExtra(MainActivity.EXTRA_GENDER);
        String pet = intent.getStringExtra(MainActivity.EXTRA_PET);
        boolean vegeterian = intent.getBooleanExtra(MainActivity.EXTRA_VEGETERIAN,true);
        int avg = intent.getIntExtra(MainActivity.EXTRA_AVG,0);

        String msg = String.format("Hello %s, you are %s y/o.\n You are %s.\n You have %s \n Your avg is %s\n Your vegeterian is %b",
                name, age, gender, pet, avg, vegeterian);
        tvHelloMessage.setText(msg);
    }
}
