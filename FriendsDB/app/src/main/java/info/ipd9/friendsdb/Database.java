package info.ipd9.friendsdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
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

    // Safety measure to make sure programmer doesn't call close() by mistake
    @Override
    public void close()  {
        Database.closeDatabase();
        // throw new RuntimeException("Do NOT call close(), use closeDatabase() instead");
    }

    private static final String TAG = "Database";

    private static final String DATABASE_FILE = "friends.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_FRIENDS_TABLE =
            "CREATE TABLE " + Friend.TABLE_NAME + " ( " +
                    Friend.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    Friend.COLUMN_NAME + " TEXT, " +
                    Friend.COLUMN_AGE + " INTEGER, " +
                    Friend.COLUMN_GENDER + " TEXT, " +
                    Friend.COLUMN_INTERESTS + " TEXT, " +
                    Friend.COLUMN_VEGETARIAN + " TEXT " + " )";

    private Database(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
        Log.d(TAG, "instantiated");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.execSQL(SQL_CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.wtf(TAG, "onUpgrade() not supported!");
        throw new InvalidParameterException("Database.onUpgrade() not supported");
    }

    //
    public void addFriend(Friend friend) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Friend.COLUMN_NAME, friend.name); // Contact Name
        values.put(Friend.COLUMN_AGE, friend.age);
        // TODO: add other fields later

        // Inserting Row
        connection.insert(Friend.TABLE_NAME, null, values);
    }

    private String [] FRIEND_COLUMNS_ALL = {
            Friend.COLUMN_ID, Friend.COLUMN_NAME, Friend.COLUMN_AGE,
            Friend.COLUMN_INTERESTS, Friend.COLUMN_GENDER, Friend.COLUMN_VEGETARIAN
    };

    public ArrayList<Friend> getAllFriends(Context context) throws SQLException {
        Cursor cursor = connection.query(Friend.TABLE_NAME, FRIEND_COLUMNS_ALL,
                null, null, null, null, null, null);

        ArrayList<Friend> friendList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // context required for translation
                Friend friend = new Friend(context);
                friend.id = Integer.parseInt(cursor.getString(0));
                friend.name = cursor.getString(1);
                friend.age = Integer.parseInt(cursor.getString(2));
                // TODO: handle other columns
                friend.gender = Friend.Gender.Undeclared;
                //
                friendList.add(friend);
            } while (cursor.moveToNext());
        }
        return friendList;
    }

    public Friend getFriendById(Context context, int id) throws SQLException {
        Cursor cursor = connection.query(Friend.TABLE_NAME, FRIEND_COLUMNS_ALL,
                Friend.COLUMN_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Friend friend = new Friend(context);
        friend.id = Integer.parseInt(cursor.getString(0));
        friend.name = cursor.getString(1);
        friend.age = Integer.parseInt(cursor.getString(2));
        // TODO: handle other columns
        friend.gender = Friend.Gender.Undeclared;
        //
        return friend;
    }

    public int updateFriend(Friend friend) {
        ContentValues values = new ContentValues();
        values.put(Friend.COLUMN_NAME, friend.name); // Contact Name
        values.put(Friend.COLUMN_AGE, friend.age);
        // TODO: add other fields later

        // updating row
        return connection.update(Friend.TABLE_NAME, values, Friend.COLUMN_ID + " = ?",
                new String[] { String.valueOf(friend.id) });
    }
}
