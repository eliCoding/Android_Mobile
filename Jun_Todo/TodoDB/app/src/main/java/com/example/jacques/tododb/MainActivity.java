package com.example.jacques.tododb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends LivecycleTrackingActivity {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_ID = "id";

    ListView lvTodoItems;
    ArrayAdapter<TodoItem> adapter;
    public final ArrayList<TodoItem> TodoItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        //
        lvTodoItems = (ListView) findViewById(R.id.lvTodoItems);
        //
        adapter = new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, TodoItemsList);
        lvTodoItems.setAdapter(adapter);
        //
        lvTodoItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "long clicked pos: " + pos);
                showDeleteDialog(pos);
                return true;
            }
        });
        //
        lvTodoItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                // put clicked item's position in Extra of Intent starting AddEditActivity
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                // pass id of the record, NOT position in the list
                intent.putExtra(EXTRA_ID, TodoItemsList.get(pos).id);
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
            TodoItemsList.clear();
            TodoItemsList.addAll(database.getAllTodoItems(this));
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
        Database.closeDatabase();
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
            case R.id.mi_add_todoItem:
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

    private void showDeleteDialog(final int pos) {
        // FIXME: all strings should come from strings.xml !!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String deleteTodo = getResources().getString(R.string.delete_todo);
        builder.setTitle(deleteTodo);
        // setup text input
        final TextView tvNote = new TextView(this);
        tvNote.setText(TodoItemsList.get(pos).action);
        // center TextView and give it more space on top
        tvNote.setPadding(0, 25, 0, 0);
        tvNote.setGravity(Gravity.CENTER);
        // input type - regular text
        builder.setView(tvNote);
        // setup buttons
        String delete = getResources().getString(R.string.delete);
        builder.setPositiveButton(delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Log.d(TAG, "Delete TodoItem: " + TodoItemsList.get(pos));
                    database.deleteNote(TodoItemsList.get(pos));
                    TodoItemsList.clear();
                    TodoItemsList.addAll(database.getAllTodoItems(MainActivity.this));
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                    String todoItemDeletedErrorString = getResources().getString(R.string.todoItem_DeletedError);
                    Toast.makeText(MainActivity.this, todoItemDeletedErrorString,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        String cancel = getResources().getString(R.string.cancel);
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

