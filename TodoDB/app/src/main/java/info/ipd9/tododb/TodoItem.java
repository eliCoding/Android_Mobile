package info.ipd9.tododb;

import android.content.Context;
import android.provider.BaseColumns;

import java.util.Date;

public class TodoItem {
    int id;
    String task;
    Date dueDate;
    boolean isDone;

    // translations
    String formatString;

    // database
    public final static String TABLE_NAME = "todoitems";
    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_TASK = "task";
    public final static String COLUMN_DUEDATE = "duedate";
    public final static String COLUMN_ISDONE = "isdone";

    //
    public TodoItem(Context context) {
        formatString = context.getResources().getString(R.string.task_format_string);
    }

    @Override
    public String toString() {
        return String.format(formatString, task, dueDate, isDone ? 1 : 0);
    }

}
