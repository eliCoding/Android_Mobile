package com.example.jacques.personel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "Name";
    public static final String EXTRA_AGE = "Age";
    public static final String EXTRA_GENDER = "GENDER";
    public static final String EXTRA_PET = "Pet";
    public static final String EXTRA_VEGETERIAN = "Reget";
    public static final String EXTRA_AVG = "Avg";
    private EditText etName,etAge;
    private RadioButton rbMale,rbFemale,rbNA;
    private CheckBox ckCat,ckDog,ckOthers;
    private ToggleButton tbVegeterian;
    private SeekBar sbAvg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText)findViewById(R.id.etName);
        etAge = (EditText)findViewById(R.id.etAge);
        rbMale = (RadioButton)findViewById(R.id.rbMale);
        rbFemale = (RadioButton)findViewById(R.id.rbFemale);
        rbNA = (RadioButton)findViewById(R.id.rbNA);
        ckCat = (CheckBox)findViewById(R.id.ckCat);
        ckDog = (CheckBox)findViewById(R.id.ckDog);
        ckOthers = (CheckBox)findViewById(R.id.ckOthers);
        tbVegeterian = (ToggleButton) findViewById(R.id.tbVegeterian);
        sbAvg = (SeekBar) findViewById(R.id.sbAvg);
    }

    public void onTellMeClick(View view) {
        // 0. Before all - create new activity SayHelloActivity
        // 1. Extract name and age strings
        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        boolean vegeterian;
        String gender = "Male";
        String pet = "Cat";

        if(rbFemale.isChecked()) {
            gender = "Female";
        } else if (rbNA.isChecked()) {
            gender = "N/A";
        }

        if(ckDog.isChecked()) {
            pet = "Dog";
        }  else {
            pet = "Others";
        }

        if (tbVegeterian.isChecked()) {
            // The toggle is enabled
            vegeterian = true;
        } else {
            // The toggle is disabled
            vegeterian = false;
        }

        int avg = sbAvg.getProgress();

        // TODO: check name / age are not empty - show message box with error if they ware
        // 2. Create an intent and put name&age into Extras
        Intent intent = new Intent(this,TellMeActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_AGE, age);
        intent.putExtra(EXTRA_GENDER,gender);
        intent.putExtra(EXTRA_PET,pet);
        intent.putExtra(EXTRA_VEGETERIAN, vegeterian);
        intent.putExtra(EXTRA_AVG, avg);
        // 3. Tell OS to show new Activity with that Intent
        startActivity(intent);
    }
}
