package info.ipd9.friendsdb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.SQLException;
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

    Button btAddSave;

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
        btAddSave = (Button) findViewById(R.id.btAddSave);
        btAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSaveClick(v);
            }
        });
    }

    private Database database;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        database = Database.openDatabase(this);
        // load data here, not in onCreate
        // Check if Extra with index was provided, if yes, load data from ArrayList[index] into UI
        Intent intent = getIntent();
        // if extra is absent then assume it was -1
        int currentId = intent.getIntExtra(MainActivity.EXTRA_ID, -1);

        if (currentId != -1) {
            try {
                // pull translation from strings.xml in current language of the device
                String saveString = getResources().getString(R.string.save);
                btAddSave.setText(saveString);
                // load the data of edited item
                currentFriend = database.getFriendById(this, currentId);
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
            } catch (SQLException e) {
                e.printStackTrace();
                String errorString = getResources().getString(R.string.error_loading_data);
                Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        Database.closeDatabase();
    }

    public void onAddSaveClick(View v) {
        Log.d(TAG, "onAddSaveClick()");
        // extract data from UI, put in Friend instance
        Friend friend = (currentFriend == null) ? new Friend(this) : currentFriend;
        friend.name = etName.getText().toString();
        friend.age = sbAge.getProgress();
        friend.gender = getSelectedGender();
        friend.interestSet = getSelectedInterestSet();
        friend.vegetarian = swVegetarian.isChecked();
        // add or save data
        try {
            if (currentFriend == null) {
                database.addFriend(friend);
                // show toast
                String friendAddedString = getResources().getString(R.string.friend_added);
                Toast.makeText(this, friendAddedString, Toast.LENGTH_SHORT).show();
            } else {
                database.updateFriend(friend);
                // show toast
                String friendUpdatedString = getResources().getString(R.string.friend_updated);
                Toast.makeText(this, friendUpdatedString, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            String errorString = getResources().getString(R.string.error_saving_data);
            Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
        }

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
