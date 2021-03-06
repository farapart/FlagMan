package com.example.xiaoxiaobai.flagman;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyCalendar cal ;
    final private String database_name = "FlagMan.db";
    final static List<DayFinish> list = new ArrayList<>();
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cal = (MyCalendar)findViewById(R.id.cal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), this.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        SQliteOpeartion sq = new SQliteOpeartion(this, database_name, null, 1);
        db = sq.getWritableDatabase();

        addTempRecord();

        list.clear();


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        cal.setRenwu(String.valueOf(year) + "年" + String.valueOf(month) + "月", list);

        cal.setOnClickListener(new MyCalendar.onClickListener() {
            @Override
            public void onLeftRowClick() {
                Toast.makeText(MainActivity.this, "点击减箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(-1);

                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onRightRowClick() {
                Toast.makeText(MainActivity.this, "点击加箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(1);
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onTitleClick(String monthStr, Date month) {
                Toast.makeText(MainActivity.this, "点击了标题："+monthStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
                Toast.makeText(MainActivity.this, "点击了星期："+weekStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDayClick(int day, String dayStr, DayFinish finish) {
                Toast.makeText(MainActivity.this, "点击了日期："+dayStr, Toast.LENGTH_SHORT).show();
                Log.w("", "点击了日期:"+dayStr);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        cal.setRenwu(String.valueOf(year) + "年" + String.valueOf(month) + "月", list);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Check_Flag.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_set) {
            Intent intent = new Intent(MainActivity.this, Check_App_Info.class);
            startActivity(intent);
        } else if (id == R.id.nav_todo1) {
            Toast. makeText (MainActivity.this, "会有新功能的！", Toast. LENGTH_SHORT ).show();
        } else if (id == R.id.nav_todo2) {
            Toast. makeText (MainActivity.this, "会有新功能的！", Toast. LENGTH_SHORT ).show();
        } else if (id == R.id.nav_todo3) {
            Toast. makeText (MainActivity.this, "会有新功能的！", Toast. LENGTH_SHORT ).show();
        } else if (id == R.id.nav_share) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("关于我们：");
            //builder.setIcon(R.drawable. tools );
            builder.setMessage("这群开发者很懒，\n 什么都不想留下。");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "感谢支持！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast. makeText (MainActivity.this, "依然爱你！", Toast. LENGTH_SHORT ).show();
                }
            });
            builder.create().show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addTempRecord(){
        list.add(new MainActivity.DayFinish(1,2,2));
        list.add(new MainActivity.DayFinish(2,1,2));
        list.add(new MainActivity.DayFinish(3,0,2));
        list.add(new MainActivity.DayFinish(4,2,2));
        list.add(new MainActivity.DayFinish(5,2,2));
        list.add(new MainActivity.DayFinish(6,2,2));
        list.add(new MainActivity.DayFinish(7,2,2));
        list.add(new MainActivity.DayFinish(8,0,2));
        list.add(new MainActivity.DayFinish(9,1,2));
        list.add(new MainActivity.DayFinish(10,2,2));
        list.add(new MainActivity.DayFinish(11,2,5));
        list.add(new MainActivity.DayFinish(12,2,2));
        list.add(new MainActivity.DayFinish(13,2,2));
        list.add(new MainActivity.DayFinish(14,3,2));
        list.add(new MainActivity.DayFinish(15,2,2));
        list.add(new MainActivity.DayFinish(16,1,2));
        list.add(new MainActivity.DayFinish(17,0,2));
        list.add(new MainActivity.DayFinish(18,2,2));
        list.add(new MainActivity.DayFinish(19,2,2));
        list.add(new MainActivity.DayFinish(20,0,2));
        list.add(new MainActivity.DayFinish(21,2,2));
        list.add(new MainActivity.DayFinish(22,1,2));
        list.add(new MainActivity.DayFinish(23,2,0));
        list.add(new MainActivity.DayFinish(24,0,2));
        list.add(new MainActivity.DayFinish(25,2,2));
        list.add(new MainActivity.DayFinish(26,2,2));
        list.add(new MainActivity.DayFinish(27,2,2));
        list.add(new MainActivity.DayFinish(28,2,2));
        list.add(new MainActivity.DayFinish(29,2,2));
        list.add(new MainActivity.DayFinish(30,2,2));
        list.add(new MainActivity.DayFinish(31,2,2));

        for(DayFinish temp : list){
            TaskInfo task = new TaskInfo();
            task.year = 2018;
            task.month = 12;
            task.day = temp.day;
            task.task_num = temp.all;
            task.finished_num = temp.finish;
            SQliteOpeartion.InsertIntoTask(MainActivity.db, task);
        }

        for(int i = 1; i<=9; i++){
            TaskInfo task = new TaskInfo();
            task.year = 2019;
            task.month = 1;
            task.day = i;
            task.task_num = list.get(i).all;
            task.finished_num = list.get(i).finish;
            SQliteOpeartion.InsertIntoTask(MainActivity.db, task);
        }
    }

    static class DayFinish{
        int day;
        int all;
        int finish;
        public DayFinish(int day, int finish, int all) {
            this.day = day;
            this.all = all;
            this.finish = finish;
        }
    }

}

