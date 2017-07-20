package info.ipd9.tododb;

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

    ListView lvTodos;
    ArrayAdapter<TodoItem> adapter;
    public final ArrayList<TodoItem> todoList = new ArrayList<>();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bindings
        lvTodos = (ListView) findViewById(R.id.lvTodos);
        //
        adapter = new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, todoList);
        lvTodos.setAdapter(adapter);
        //
        lvTodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "long clicked pos: " + pos);
                // TODO: showDeleteDialog(pos);
                return true;
            }
        });
        //
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                // put clicked item's position in Extra of Intent starting AddEditActivity
                Intent intent = new Intent(MainActivity.this, EditAddActivity.class);
                // pass id of the record, NOT position in the list
                intent.putExtra(EXTRA_ID, todoList.get(pos).id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        try {
            // Globals.loadData(this);
            database = Database.openDatabase(this);
            // FIXME: don't need globals
            todoList.clear();
            todoList.addAll(database.getAllTodoItems(this));
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
            case R.id.mi_add_todo:
            {
                Log.d(TAG, "Menu item Add selected");
                Intent intent = new Intent(this, EditAddActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
