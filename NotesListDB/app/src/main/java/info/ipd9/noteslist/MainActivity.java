package info.ipd9.noteslist;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private final ArrayList<Note> notesList = new ArrayList<>();

    ArrayAdapter adapter;
    private ListView lvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        lvNotes = (ListView) findViewById(R.id.lvNotes);

        adapter = new ArrayAdapter<Note>(this, android.R.layout.simple_list_item_1, notesList);
        lvNotes.setAdapter(adapter);
        //
        lvNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "long clicked pos: " + pos);
                showDeleteDialog(pos);
                return true;
            }
        });
        //
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                showEditDialog(pos);
            }
        });
    }

    Database database;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        try {
            database = Database.openDatabase(this);
            // fetch data and put into listview
            notesList.clear();
            notesList.addAll(database.getAllNotes());
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading from database", Toast.LENGTH_LONG).show();
        }
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
            case R.id.mi_add_note:
                Log.d(TAG, "Menu item Add Note selected");
                showAddNoteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddNoteDialog() {
        // FIXME: all strings should come from strings.xml !!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add note");
        // setup text input
        final EditText input = new EditText(this);
        // input type - regular text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // setup buttons
        builder.setPositiveButton("Add note", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // TODO: add item to list
                    Note note = new Note();
                    note.note = input.getText().toString();
                    Log.d(TAG, "Add note: " + note);

                    database.addNote(note);
                    notesList.clear();
                    notesList.addAll(database.getAllNotes());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_LONG).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Database error while adding note",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showDeleteDialog(final int pos) {
        // FIXME: all strings should come from strings.xml !!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete note");
        // setup text input
        final TextView tvNote = new TextView(this);
        tvNote.setText(notesList.get(pos).note);
        // center TextView and give it more space on top
        tvNote.setPadding(0, 25, 0, 0);
        tvNote.setGravity(Gravity.CENTER);
        // input type - regular text
        builder.setView(tvNote);
        // setup buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Log.d(TAG, "Delete note: " + notesList.get(pos));
                    database.deleteNote(notesList.get(pos));
                    notesList.clear();
                    notesList.addAll(database.getAllNotes());
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Database error while deleting note",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void showEditDialog(final int pos) {
        // FIXME: all strings should come from strings.xml !!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit note");
        // setup text input
        final EditText input = new EditText(this);
        input.setText(notesList.get(pos).note);
        // input type - regular text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // setup buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String note = input.getText().toString();
                    Log.d(TAG, "Save note: " + note);
                    Note nnn = notesList.get(pos);
                    nnn.note = note;
                    // update and refresh
                    database.updateNote(nnn);
                    notesList.clear();
                    notesList.addAll(database.getAllNotes());
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Database error while deleting note",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

}
