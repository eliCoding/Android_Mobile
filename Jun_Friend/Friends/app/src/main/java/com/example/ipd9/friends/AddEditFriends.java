package com.example.ipd9.friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Set;

import static com.example.ipd9.friends.Globals.friendsList;

public class AddEditFriends extends AppCompatActivity {

    private EditText etName;
    private RadioButton rbMale,rbFemale,rbNA;
    private CheckBox cbCats,cbDogs,cbPigs, cbGoldfish;
    private Switch swVegeterian;
    private SeekBar sbAge;
    private Button btAddEdit;
    private String op;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_friends);

        etName = (EditText)findViewById(R.id.etName);
        sbAge = (SeekBar) findViewById(R.id.sbAge);
        rbMale = (RadioButton)findViewById(R.id.rbMale);
        rbFemale = (RadioButton)findViewById(R.id.rbFemale);
        rbNA = (RadioButton)findViewById(R.id.rbNA);
        cbCats = (CheckBox)findViewById(R.id.cbCats);
        cbDogs = (CheckBox)findViewById(R.id.cbDogs);
        cbPigs = (CheckBox)findViewById(R.id.cbPigs);
        cbGoldfish = (CheckBox)findViewById(R.id.cbGoldfish);
        swVegeterian = (Switch)findViewById(R.id.swVegeterian);
        btAddEdit = (Button)findViewById(R.id.btAddEdit);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        op = intent.getStringExtra(MainActivity.EXTRA_OP);
        pos = intent.getIntExtra(MainActivity.EXTRA_POS,0);
        if(op.equalsIgnoreCase(MainActivity.EDIT) ) {
            etName.setText(friendsList.get(pos).getName());
            sbAge.setProgress(friendsList.get(pos).getAge());
            swVegeterian.setChecked(friendsList.get(pos).isVegeterian());
            switch (friendsList.get(pos).getGender()) {
                case Male:
                    rbMale.setChecked(true);
                    break;
                case Female:
                    rbFemale.setChecked(true);
                    break;
                case NotAvailable:
                    rbNA.setChecked(true);
                    break;
                default:
                    break;
            }
            Set<Interest> interests = friendsList.get(pos).getInterests();
            for(Interest i : interests) {
                switch (i) {
                    case Cats:
                        cbCats.setChecked(true);
                        break;
                    case Dogs:
                        cbDogs.setChecked(true);
                        break;
                    case Pigs:
                        cbPigs.setChecked(true);
                        break;
                    case GoldFish:
                        cbGoldfish.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
            btAddEdit.setText(MainActivity.EDIT);
            this.setTitle(MainActivity.EDIT + " Friend");
        } else if(op.equalsIgnoreCase(MainActivity.ADD) ) {
            etName.setText("");
            sbAge.setProgress(0);
            swVegeterian.setChecked(false);
            rbMale.setChecked(true);
            cbCats.setChecked(false);
            cbDogs.setChecked(false);
            cbPigs.setChecked(false);
            cbGoldfish.setChecked(false);
            btAddEdit.setText(MainActivity.ADD);
            this.setTitle(MainActivity.ADD + " Friend");
        }
    }

    public void onAddEditClick(View view) {

        boolean vegeterian;
        Gender gender = Gender.Male;
        Set<Interest> interests = new HashSet<>();

        String name = etName.getText().toString();

        int age = sbAge.getProgress();

        if(rbFemale.isChecked()) {
            gender = Gender.Female;
        } else if (rbNA.isChecked()) {
            gender = Gender.NotAvailable;
        }

        if(cbDogs.isChecked()) {
            interests.add(Interest.Dogs);
        }
        if (cbCats.isChecked()){
            interests.add(Interest.Cats);
        }
        if (cbPigs.isChecked()) {
            interests.add(Interest.Pigs);
        }
        if(cbGoldfish.isChecked()) {
            interests.add(Interest.GoldFish);
        }

        if (swVegeterian.isChecked()) {
            // The toggle is enabled
            vegeterian = true;
        } else {
            // The toggle is disabled
            vegeterian = false;
        }

        Friend friend = new Friend(name,age,interests,gender,vegeterian);
        if(op.equals(MainActivity.ADD)){
            friendsList.add(friend);
        } else if(op.equals(MainActivity.EDIT)) {
            friendsList.set(pos,friend);
        }

        finish();
    }

}
