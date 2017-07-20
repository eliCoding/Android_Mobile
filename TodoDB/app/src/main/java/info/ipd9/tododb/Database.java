package info.ipd9.tododb;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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

    // Safety measure to make sure programmer doesn't call close() by mistake
    @Override
    public void close() {
        Database.closeDatabase();
        // throw new RuntimeException("Do NOT call close(), use closeDatabase() instead");
    }

    private static final String TAG = "Database";

    private static final String DATABASE_FILE = "todos.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TODOS_TABLE =
            "CREATE TABLE " + TodoItem.TABLE_NAME + " ( " +
                    TodoItem.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    TodoItem.COLUMN_TASK + " TEXT, " +
                    TodoItem.COLUMN_DUEDATE + " TEXT, " +
                    TodoItem.COLUMN_ISDONE + " INTEGER " + " )";

    private Database(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
        Log.d(TAG, "instantiated");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.execSQL(SQL_CREATE_TODOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.wtf(TAG, "onUpgrade() not supported!");
        throw new InvalidParameterException("Database.onUpgrade() not supported");
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    private String dateToString(Date date) {
        return dateFormat.format(date);
    }

    private Date stringToDate(String string) throws ParseException {
        return dateFormat.parse(string);
    }

    //
    public void addTodoItem(TodoItem item) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(TodoItem.COLUMN_TASK, item.task);
        values.put(TodoItem.COLUMN_DUEDATE, dateToString(item.dueDate));
        values.put(TodoItem.COLUMN_ISDONE, item.isDone ? 1 : 0);
        // Inserting Row
        connection.insert(TodoItem.TABLE_NAME, null, values);
    }

    private String[] TODOITEM_COLUMNS_ALL = {
            TodoItem.COLUMN_ID, TodoItem.COLUMN_TASK, TodoItem.COLUMN_DUEDATE, TodoItem.COLUMN_ISDONE};

    public ArrayList<TodoItem> getAllTodoItems(Context context) throws SQLException {
        Cursor cursor = connection.query(TodoItem.TABLE_NAME, TODOITEM_COLUMNS_ALL,
                null, null, null, null, null, null);
        try {
            ArrayList<TodoItem> todoList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    // context required for translation
                    TodoItem item = new TodoItem(context);
                    item.id = Integer.parseInt(cursor.getString(0));
                    item.task = cursor.getString(1);
                    item.dueDate = stringToDate(cursor.getString(2));
                    item.isDone = cursor.getString(3).equals("0") ? false : true;

                    todoList.add(item);
                } while (cursor.moveToNext());
            }
            return todoList;
        } catch (ParseException e) {
            throw new android.database.SQLException("Error parsing dueDate in record");
        }
    }

    public TodoItem getTodoItemById(Context context, int id) throws SQLException {
        try {
            Cursor cursor = connection.query(TodoItem.TABLE_NAME, TODOITEM_COLUMNS_ALL,
                    TodoItem.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            TodoItem item = new TodoItem(context);
            item.id = Integer.parseInt(cursor.getString(0));
            item.task = cursor.getString(1);
            item.dueDate = stringToDate(cursor.getString(2));
            item.isDone = cursor.getString(3).equals("0") ? false : true;
            //
            return item;
        } catch (ParseException e) {
            throw new android.database.SQLException("Error parsing dueDate in record");
        }
    }

    public int updateTodoItem(TodoItem item) {
        ContentValues values = new ContentValues();
        values.put(TodoItem.COLUMN_TASK, item.task);
        values.put(TodoItem.COLUMN_DUEDATE, dateToString(item.dueDate));
        values.put(TodoItem.COLUMN_ISDONE, item.isDone ? 1 : 0);
        // updating row
        return connection.update(TodoItem.TABLE_NAME, values, TodoItem.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.id)});
    }
}
