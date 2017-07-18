package info.ipd9.noteslist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Created by ipd on 7/18/2017.
 */

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


    //safety measure to make sure programmer doesnot call close() by mistake
    @Override
    public void close() {

        database.closeDatabase();

    }


    private static final String TAG = "Database";

    private static final String DATABASE_FILE = "notes.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_FRIENDS_TABLE =
            "CREATE TABLE " + Note.TABLE_NAME + " ( " +
                    Note.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    Note.COLUMN_NOTE + " TEXT, " +
                    " )";

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
    public void addNote(Note note) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.note); // Contact Name


        // Inserting Row
        connection.insert(Note.TABLE_NAME, null, values);
    }

    private String [] NOTES_COLUMNS_ALL = {
            Note.COLUMN_ID, Note.COLUMN_NOTE
    };

    public ArrayList<Note> getAllNotes() throws SQLException {
        Cursor cursor = connection.query(Note.TABLE_NAME, NOTES_COLUMNS_ALL,
                null, null, null, null, null, null);

        ArrayList<Note> notesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // context required for translation
                Note note = new Note();
                note.id = Integer.parseInt(cursor.getString(0));
                note.note= cursor.getString(1);

                //
                notesList.add(note);
            } while (cursor.moveToNext());
        }
        return notesList;
    }

   /* public Note getNotesById(int id) throws SQLException {
        Cursor cursor = connection.query(Note.TABLE_NAME, NOTES_COLUMNS_ALL,
                Note.COLUMN_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note();
        note.id = Integer.parseInt(cursor.getString(0));
        note.note = cursor.getString(1);

        //
        return note;
    }*/

    public int updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.note); // Contact Name

        // updating row
        return connection.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[] { String.valueOf(note.id) });
    }

    public void deleteNote(Note note) {

        connection.delete(Note.TABLE_NAME,Note.COLUMN_ID + " = ?",

                new String[] { String.valueOf(note.id
                )});

    }




}
