package info.ipd9.todoapi;

import java.util.Date;

public class TodoItem {
    int id;
    String task;
    Date dueDate;
    boolean isDone;

    @Override
    public String toString() {
        return String.format("%s is due on %s done=%d", task, dueDate, isDone ? 1 : 0);
    }
}
