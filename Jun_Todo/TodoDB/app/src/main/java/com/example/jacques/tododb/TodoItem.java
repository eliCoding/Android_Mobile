package com.example.jacques.tododb;

import android.content.Context;
import android.provider.BaseColumns;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Jacques on 2017-07-18.
 */

public class TodoItem {

    public static final String TABLE_NAME = "todos";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_DUEDATE = "dueDate";
    public static final String COLUMN_ISDONE = "isDone";

    private static final String TAG = "TodoItem";

    int id;
    String action;
    Date dueDate;
    boolean isDone;

    String formatString;
    String strDone,strNotDone;

    public TodoItem(Context context) {
        formatString = context.getResources().getString(R.string.todoItem_format);

        strDone = context.getResources().getString(R.string.done);
        strNotDone = context.getResources().getString(R.string.notDone);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      //  String d = sdf.format(dueDate).toString();

        return String.format(formatString, action, sdf.format(dueDate).toString(), isDone ? strDone : strNotDone);
    }

}
