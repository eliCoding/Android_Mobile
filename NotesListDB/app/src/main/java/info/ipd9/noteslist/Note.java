package info.ipd9.noteslist;

import android.provider.BaseColumns;

/**
 * Created by ipd on 7/18/2017.
 */

public class Note {

    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NOTE = "note";



      int id;
      String note;

    @Override
    public String toString() {
        return   note
                ;


    }
}
