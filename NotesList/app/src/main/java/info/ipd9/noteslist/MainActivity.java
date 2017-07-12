package info.ipd9.noteslist;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    String[] initialNotesList = {"Buy Milk", "Learn Android", "Do the homework"};

    ArrayList<String> notesList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView lvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // do not put any thing before super
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");


        setContentView(R.layout.activity_main);
        //Binding
        lvNotes = (ListView) findViewById(R.id.lvNotes);

        //array adapter is a class
        notesList.addAll(Arrays.asList(initialNotesList));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesList);
        lvNotes.setAdapter(adapter);

        //
        lvNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.d(TAG, "long clicked pos:" + pos);
                showDeleteDialog(pos);
                return true;
            }
        });


        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int pos, long id) {


                Log.d(TAG, "long clicked pos:" + pos);
                showEditDialog(pos);

            }

        });
    }

    private void showDeleteDialog(final int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete note");

        //setup text input
        final TextView tvNote = new TextView(this);
        tvNote.setText(notesList.get(pos));

        //input type regular text
        builder.setView(tvNote);

        //setup buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Delete note:" + notesList.get(pos));
                notesList.remove(pos);
                adapter.notifyDataSetChanged();

            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //ToDO: sdd item to list
                dialog.cancel();

            }


        });

        builder.show();

    }

    private void showEditDialog(int pos) {
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        saveDate();


    }

    private static final String filename = "data.txt";

    private void saveDate() {

        String string = "";

        for (String note : notesList) {
            string += note + "\n";

        }
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Enter saving data", Toast.LENGTH_LONG).show();
        }

    }

    private void loadDate() {
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(filename);
            Scanner input = new Scanner(inputStream);
            notesList.clear();

            while (input.hasNextLine()) {
                String note = input.nextLine();
                notesList.add(note);

            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Enter saving data", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notesmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_add_note:
                Log.d(TAG, "Menu item Add Note Selected");
                showAddNoteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add note");

        //setup text input
        final EditText input = new EditText(this);

        //input type - regular text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //setup buttons
        builder.setPositiveButton("Add note", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //ToDO: sdd item to list
                String note = input.getText().toString();
                Log.d(TAG, "Add note:" + note);
                notesList.add(note);
                adapter.notifyDataSetChanged();

            }


        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //ToDO: sdd item to list
                dialog.cancel();

            }


        });

        builder.show();


    }


}
