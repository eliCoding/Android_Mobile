package com.example.jacques.tododb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;


public class Database extends SQLiteOpenHelper {

    private static int useCount;
    private static Database database;
    private static SQLiteDatabase connection;

    public static Database openDatabase(Context context) {
        Log.d(TAG, "openDatabase, from count=" + useCount);
        useCount++;
        if (database == null) {
            Log.d(TAG, "openDatabase, count was 0, really opening");
            database = new Database(context);
            connection = database.getWritableDatabase();
        }
        return database;
    }

    public static void closeDatabase() {
        Log.d(TAG, "closeDatabase, from count=" + useCount);
        useCount--;
        if (useCount == 0) {
            Log.d(TAG, "closeDatabase - count is 0, really closing");
            connection.close();
            database = null;
        }
    }

    private static final String TAG = "Database";
    private static final String DATABASE_FILE = "TodoItems.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TodoItemS_TABLE =
            "CREATE TABLE " + TodoItem.TABLE_NAME + " ( " +
                    TodoItem.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    TodoItem.COLUMN_ACTION + " TEXT, " +
                    TodoItem.COLUMN_DUEDATE + " DATE, " +
                    TodoItem.COLUMN_ISDONE + " BOOLEAN " + " )";

    private Database(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
        Log.d(TAG, "instantiated");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.execSQL(SQL_CREATE_TodoItemS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.wtf(TAG, "onUpgrade() not supported!");
        throw new InvalidParameterException("Database.onUpgrade() not supported");
    }

    //
    public void addTodoItem(TodoItem TodoItem) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(TodoItem.COLUMN_ACTION, TodoItem.action); // Contact Name
        values.put(TodoItem.COLUMN_DUEDATE, TodoItem.dueDate.toString());
        values.put(TodoItem.COLUMN_ISDONE,String.valueOf(TodoItem.isDone));


        // Inserting Row
        connection.insert(TodoItem.TABLE_NAME, null, values);
    }

    private String [] TodoItem_COLUMNS_ALL = {
            TodoItem.COLUMN_ID, TodoItem.COLUMN_ACTION, TodoItem.COLUMN_DUEDATE,
            TodoItem.COLUMN_ISDONE
    };

    public ArrayList<TodoItem> getAllTodoItems(Context context) throws SQLException {
        Cursor cursor = connection.query(TodoItem.TABLE_NAME, TodoItem_COLUMNS_ALL,
                null, null, null, null, null, null);

        ArrayList<TodoItem> TodoItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // context required for translation
                TodoItem TodoItem = new TodoItem(context);
                TodoItem.id = Integer.parseInt(cursor.getString(0));
                TodoItem.action = cursor.getString(1);
                TodoItem.dueDate = Date.valueOf(cursor.getString(2));
                TodoItem.isDone = Boolean.valueOf(cursor.getString(3));
                //
                TodoItemList.add(TodoItem);
            } while (cursor.moveToNext());
        }
        return TodoItemList;
    }

    public TodoItem getTodoItemById(Context context, int id) throws SQLException {
        Cursor cursor = connection.query(TodoItem.TABLE_NAME, TodoItem_COLUMNS_ALL,
                TodoItem.COLUMN_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        TodoItem TodoItem = new TodoItem(context);
        TodoItem.id = Integer.parseInt(cursor.getString(0));
        TodoItem.action = cursor.getString(1);
        TodoItem.dueDate = Date.valueOf(cursor.getString(2));
        TodoItem.isDone = Boolean.valueOf(cursor.getString(3));

        return TodoItem;
    }

    public int updateTodoItem(TodoItem TodoItem) {
        ContentValues values = new ContentValues();
        values.put(TodoItem.COLUMN_ACTION, TodoItem.action); // Contact Name
        values.put(TodoItem.COLUMN_DUEDATE, TodoItem.dueDate.toString());
        values.put(TodoItem.COLUMN_ISDONE, String.valueOf(TodoItem.isDone));
        // updating row
        return connection.update(TodoItem.TABLE_NAME, values, TodoItem.COLUMN_ID + " = ?",
                new String[] { String.valueOf(TodoItem.id) });
    }

    // Deleting single contact
    public void deleteNote(TodoItem TodoItem) {
        connection.delete(TodoItem.TABLE_NAME, TodoItem.COLUMN_ID + " = ?",
                new String[] { String.valueOf(TodoItem.id) });
    }
}