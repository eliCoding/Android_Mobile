package info.ipd9.friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AddEditActivity extends LivecycleTrackingActivity {

    private static final String TAG = "AddEditActivity";

    private EditText etName;
    private SeekBar sbAge;
    private CheckBox cbCats, cbDogs, cbFish, cbPigs;
    private Switch swVegetarian;
    private RadioGroup rgGender;

    private Friend currentFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        //
        etName = (EditText) findViewById(R.id.etName);
        sbAge = (SeekBar) findViewById(R.id.sbAge);
        swVegetarian = (Switch) findViewById(R.id.swVegetarian);
        cbCats = (CheckBox) findViewById(R.id.cbCats);
        cbDogs = (CheckBox) findViewById(R.id.cbDogs);
        cbFish = (CheckBox) findViewById(R.id.cbFish);
        cbPigs = (CheckBox) findViewById(R.id.cbPigs);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        // Example on how to add onClick handler programmatically
        Button button = (Button) findViewById(R.id.btAddSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSaveClick(v);
            }
        });
        // Check if Extra with index was provided, if yes, load data from ArrayList[index] into UI
        Intent intent = getIntent();
        // if extra is absent then assume it was -1
        int currentIndex = intent.getIntExtra(MainActivity.EXTRA_INDEX, -1);
        if (currentIndex != -1) {
            String hw = getResources().getString(R.string.Save);
            button.setText(hw);

            // load the data of edited item
            currentFriend = Globals.friendsList.get(currentIndex);
            etName.setText(currentFriend.name);
            sbAge.setProgress(currentFriend.age);
            swVegetarian.setChecked(currentFriend.vegetarian);
            // check boxes for interests
            cbDogs.setChecked(currentFriend.interestSet.contains(Friend.Interest.Dogs));
            cbCats.setChecked(currentFriend.interestSet.contains(Friend.Interest.Cats));
            cbFish.setChecked(currentFriend.interestSet.contains(Friend.Interest.Goldfish));
            cbPigs.setChecked(currentFriend.interestSet.contains(Friend.Interest.Pigs));
            // radio buttons for gender
            switch (currentFriend.gender) {
                case Male:
                    rgGender.check(R.id.rbMale);
                    break;
                case Female:
                    rgGender.check(R.id.rbFemale);
                    break;
                case Undeclared:
                    rgGender.check(R.id.rbUndeclared);
                    break;
                default:
                    Log.wtf(TAG, "currentFriend.gender unknown");
            }
        }
    }

    public void onAddSaveClick(View v) {
        Log.d(TAG, "onAddSaveClick()");
        // extract data from UI, put in Friend instance
        Friend friend = (currentFriend == null) ? new Friend() : currentFriend;
        friend.name = etName.getText().toString();
        friend.age = sbAge.getProgress();
        friend.gender = getSelectedGender();
        friend.interestSet = getSelectedInterestSet();
        friend.vegetarian = swVegetarian.isChecked();
        // add or save data
        if (currentFriend == null) {
            Globals.friendsList.add(friend);
        }


        // save changes to file
        try {
            Globals.saveData(this);
        } catch (IOException e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_LONG).show();
        }
        // show toast
        String toastAdd = getResources().getString(R.string.ToastAdd);
        Toast.makeText(this, toastAdd, Toast.LENGTH_SHORT).show();
        // finish(), do NOT use Intent to go back to MainActivity
        finish();
    }

    private Friend.Gender getSelectedGender() {
        int id = rgGender.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rbMale:
                return Friend.Gender.Male;
            case R.id.rbFemale:
                return Friend.Gender.Female;
            case R.id.rbUndeclared:
                return Friend.Gender.Undeclared;
            default:
                Log.wtf(TAG, "setSelectedGender() unknown ID");
                return Friend.Gender.Male;
        }
    }

    private Set<Friend.Interest> getSelectedInterestSet() {
        HashSet<Friend.Interest> set = new HashSet<>();
        if (cbDogs.isChecked()) {
            set.add(Friend.Interest.Dogs);
        }
        if (cbCats.isChecked()) {
            set.add(Friend.Interest.Cats);
        }
        if (cbFish.isChecked()) {
            set.add(Friend.Interest.Goldfish);
        }
        if (cbPigs.isChecked()) {
            set.add(Friend.Interest.Pigs);
        }
        return set;
    }

}
