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
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    public static final String EXTRA_ID = "id";

    ListView lvBirthdays;
    ArrayAdapter<Birthday> adapter;
    public final ArrayList<Birthday> birthdaysList = new ArrayList<>();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bindings
        lvBirthdays = (ListView) findViewById(R.id.lvBirthdays);
        //
        adapter = new ArrayAdapter<Birthday>(this, android.R.layout.simple_list_item_1, birthdaysList);
        lvBirthdays.setAdapter(adapter);
        //

        //
       lvBirthdays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                // put clicked item's position in Extra of Intent starting AddEditActivity
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                // pass id of the record, NOT position in the list
                intent.putExtra(EXTRA_ID, birthdaysList.get(pos).id);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        try {

            database = Database.openDatabase(this);

            birthdaysList.clear();
            birthdaysList.addAll(database.getAllBirthdays(this));
        } catch (SQLException e) {
            String errorString = getResources().getString(R.string.error_loading_data);
             Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        database.close();
        // Database.closeDatabase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_add_birthday: {
                Log.d(TAG, "Menu item Add selected");
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
