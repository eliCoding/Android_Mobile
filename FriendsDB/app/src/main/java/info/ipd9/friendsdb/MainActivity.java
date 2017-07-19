package info.ipd9.friendsdb;

import android.content.Intent;
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

public class MainActivity extends LivecycleTrackingActivity {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_ID = "id";

    ListView lvFriends;
    ArrayAdapter<Friend> adapter;
    public final ArrayList<Friend> friendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        //
        lvFriends = (ListView) findViewById(R.id.lvFriends);
        //
        adapter = new ArrayAdapter<Friend>(this, android.R.layout.simple_list_item_1, friendsList);
        lvFriends.setAdapter(adapter);
        //
        lvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "long clicked pos: " + pos);
                // TODO: showDeleteDialog(pos);
                return true;
            }
        });
        //
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                // put clicked item's position in Extra of Intent starting AddEditActivity
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                // pass id of the record, NOT position in the list
                intent.putExtra(EXTRA_ID, friendsList.get(pos).id);
                startActivity(intent);
            }
        });
    }

    private Database database;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        try {
            // Globals.loadData(this);
            database = Database.openDatabase(this);
            // FIXME: don't need globals
            friendsList.clear();
            friendsList.addAll(database.getAllFriends(this));
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
            case R.id.mi_add_friend:
            {
                Log.d(TAG, "Menu item Add selected");
                Intent intent = new Intent(this, AddEditActivity.class);
                startActivity(intent);
            }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
