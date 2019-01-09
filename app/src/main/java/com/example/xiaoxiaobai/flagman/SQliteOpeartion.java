package com.example.xiaoxiaobai.flagman;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

class TaskInfo{
    int year;
    int month;
    int day;
    int task_num;
    int finished_num;
}

public class SQliteOpeartion extends SQLiteOpenHelper {

    public static ArrayList<TaskInfo> List = new ArrayList<TaskInfo>();

    public SQliteOpeartion(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("drop table if exists CurrentTask");
        db.execSQL("drop table if exists Task");

        String Table_task = "CREATE TABLE CurrentTask(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "appName VARCHAR(50), "+
                "timeLimit INTEGER)";
        String Task = "CREATE TABLE Task(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "year INTEGER, "+
                "month INTEGER, "+
                "day INTEGER, "+
                "taskNum INTEGER, "+
                "finishedNum INTEGER)";
        db.execSQL(Table_task);
        db.execSQL(Task);
        Log.i("SQLite", Table_task);
        Log.i("SQLite", Task);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists CurrentTask");
        db.execSQL("drop table if exists Task");
        onCreate(db);
    }

    public static void UpdateInCurrentTask(SQLiteDatabase db, String app_name, Integer timeLimit){
        Cursor cursor = db.query("CurrentTask", null, "appName == ?", new String[]{app_name}, null, null, null, null);
        if(cursor.moveToNext() == false){
            String insert = "INSERT INTO CurrentTask(appName, timeLimit) VALUES("+'"'+app_name+'"'+", "+timeLimit+")";
            db.execSQL(insert);
            Log.i("SQLite", insert);
        }else{
            String Update = "UPDATE CurrentTask SET appName = "+'"'+app_name+'"'+", timeLimit = "+timeLimit+" WHERE appName == "+'"'+app_name+'"';
            db.execSQL(Update);
            Log.i("SQLite", Update);
        }
    }

    public static int SelectFromCurrentTask(SQLiteDatabase db, String app_name){
        Cursor cursor = db.query("CurrentTask", null, "appName == ?", new String[]{app_name}, null, null, null, null);
        if(cursor.moveToNext()){
            return cursor.getInt(2);
        }else{
            return 0;
        }
    }

    public static void DeleteFromCurrentTask(SQLiteDatabase db, String app_name){
        Cursor cursor = db.query("CurrentTask", null, "appName == ?", new String[]{app_name}, null, null, null, null);
        if(!cursor.moveToNext()) return;
        String delete = "DELETE FROM CurrentTask WHERE appName == "+'"'+app_name+'"';
        db.execSQL(delete);
        Log.i("SQLite", delete);
    }

    public static void DeleteAllCurrentTask(SQLiteDatabase db){
        String delete = "DELETE FROM CurrentTask ";
        db.execSQL(delete);
        Log.i("SQLite", delete);
    }

    public static void SelectFromTask(SQLiteDatabase db, int year, int month){
        List.clear();
        Cursor cursor = db.query("Task", null, "year == ? AND month == ?", new String[]{String.valueOf(year), String.valueOf(month)}, null, null, null, null);
        while (cursor.moveToNext()) {
            TaskInfo temp = new TaskInfo();
            temp.year = cursor.getInt(1);
            temp.month = cursor.getInt(2);
            temp.day = cursor.getInt(3);
            temp.task_num = cursor.getInt(4);
            temp.finished_num = cursor.getInt(5);
            List.add(temp);
        }
    }

    public static void InsertIntoTask(SQLiteDatabase db, TaskInfo info){
        String insert = "INSERT INTO Task(year, month, day, taskNum, finishedNum) VALUES("+info.year+", "
                +info.month+", "
                +info.day+", "
                +info.task_num +", "+ info.finished_num+")";
        db.execSQL(insert);
        Log.i("SQLite", insert);
    }

}
