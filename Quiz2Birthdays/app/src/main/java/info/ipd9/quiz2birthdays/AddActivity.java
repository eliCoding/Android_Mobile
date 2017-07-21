package info.ipd9.quiz2birthdays;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private final static String TAG = "EditAddActivity";

    // bindings
    EditText etName;
    DatePicker dpDob;



    private Database database;
    private Birthday currentBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // bindings
        etName = (EditText) findViewById(R.id.etName);
        dpDob = (DatePicker) findViewById(R.id.dpDob);

      /*  Birthday dob = (currentBirthday == null) ? new Birthday() : currentBirthday;
        dob.name = etName.getText().toString();
        int year = dpDob.getYear() - 1900;
        int month = dpDob.getMonth();
        int dom = dpDob.getDayOfMonth();
        dob.dob = new Date(year, month, dom);

        // add or save data
        try {
            if (currentBirthday == null) {
                database.addBirthday(dob);
                // show toast
                String friendAddedString = getResources().getString(R.string.birthday_added);
                Toast.makeText(this, friendAddedString, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            String errorString = getResources().getString(R.string.error_saving_data);
            Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
        }*/


    }

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

                // load the data of edited item
                currentBirthday = database.getBirthdayById(this, currentId);
                etName.setText(currentBirthday.name);
                //
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentBirthday.dob);

                dpDob.updateDate(cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            } catch (SQLException e) {
                e.printStackTrace();
                String errorString = getResources().getString(R.string.error_loading_data);
                Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
            }
        }



     //   Intent intent = new Intent(AddActivity.this, MainActivity.class);
        // pass id of the record, NOT position in the list
        //  intent.putExtra(EXTRA_ID, birthdaysList.get(pos).id);
     //   startActivity(intent);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        Database.closeDatabase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_save_birthday: {
                Log.d(TAG, "Menu item Save selected");
                Birthday dob = (currentBirthday == null) ? new Birthday() : currentBirthday;
                dob.name = etName.getText().toString();
                int year = dpDob.getYear() - 1900;
                int month = dpDob.getMonth();
                int dom = dpDob.getDayOfMonth();
                dob.dob = new Date(year, month, dom);

                // add or save data
                try {
                    if (currentBirthday == null) {
                        database.addBirthday(dob);
                        // show toast
                        String friendAddedString = getResources().getString(R.string.birthday_added);
                        Toast.makeText(this, friendAddedString, Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    String errorString = getResources().getString(R.string.error_saving_data);
                    Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();

                }
            }
            finish();
            return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }


}
