package com.example.ipd9.friends;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;
import com.example.ipd9.friends.Globals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static com.example.ipd9.friends.Friend.createFromLine;
import static com.example.ipd9.friends.Globals.friendsList;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_OP = "OPERATION";
    public static final String EXTRA_POS = "POSITION";
    private final static String TAG = "MainActivity";
    public final static String EDIT = "Edit";
    public final static String ADD = "Add";
    ArrayAdapter<Friend> adapter;
    private ListView lvfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        lvfriends = (ListView) findViewById(R.id.lvFriends);
        adapter = new ArrayAdapter<Friend>(this, android.R.layout.simple_list_item_1, Globals.friendsList);
        lvfriends.setAdapter(adapter);

        lvfriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                showDeletefriendDialog(pos);
            }
        });

        lvfriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                Log.d(TAG, "long clicked pos" + pos);
                Intent intent = new Intent(MainActivity.this,AddEditFriends.class);
                intent.putExtra(EXTRA_OP, EDIT);
                intent.putExtra(EXTRA_POS, pos);
                startActivity(intent);
                return true;
            }
        });

        loadData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        try {
            saveData();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error saving data", Toast.LENGTH_LONG).show();
        }
    }

    private static final String filename = "friends.txt";

    private void saveData() {
        String string = "";
        for (Friend f : friendsList) {
            string += f.toString() + "\n";
        }

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Error saving data", Toast.LENGTH_LONG).show();
        }
    }

    private void loadData() {
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(filename);
            Scanner input = new Scanner(inputStream);
            friendsList.clear();
            while (input.hasNextLine()) {
                String str = input.nextLine();
                Friend friend = createFromLine(str);
                friendsList.add(friend);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e ){
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Error reading data", Toast.LENGTH_LONG).show();
        }
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mi_add_friend:
                Log.d(TAG, "Menu item Add friend selected");
                Intent intent = new Intent(this,AddEditFriends.class);
                intent.putExtra(EXTRA_OP, ADD);
                intent.putExtra(EXTRA_POS, -2);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeletefriendDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete friend");
        // setup text tvfriend
        final EditText tvfriend = new EditText(this);
        tvfriend.setInputType(InputType.TYPE_CLASS_TEXT);
        tvfriend.setText(friendsList.get(pos).getName());
        builder.setView(tvfriend);
        builder.setPositiveButton("Delete friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String friend = tvfriend.getText().toString();
                Log.d(TAG, "Delete friend :" + friend);
                friendsList.remove(pos);
                adapter.notifyDataSetChanged();
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
