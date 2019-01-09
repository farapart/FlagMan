package com.example.xiaoxiaobai.flagman;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check_App_Info extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<App_Info> ShowList;
    private ArrayList<App_Info> AppInfoList;
    private List<UsageStats> result;
    private long FlagTime, tmpTime;
    private String app_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);
        onResume();
    }

    //每次重新进入界面的时候加载listView
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        List<Map<String,Object>> datalist = null;

        Get_App_Info statisticsInfo = new Get_App_Info(this);
        ShowList = statisticsInfo.getShowList();
        datalist = getDataList(ShowList);

        ListView listView = (ListView)findViewById(R.id.AppStatisticsList);

        SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.inner_list,
                new String[]{"label","info","times","icon","flagtime"},
                new int[]{R.id.label,R.id.info,R.id.times,R.id.icon,R.id.flagtime});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Drawable){

                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)o);
                    return true;
                }
                else return false;
            }
        });

    }

    private List<Map<String,Object>> getDataList(ArrayList<App_Info> ShowList) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        Map<String,Object> map = new HashMap<String,Object>();

        for(App_Info appInformation : ShowList) {
            map = new HashMap<String,Object>();
            map.put("label",appInformation.getLabel());
            map.put("info","运行时间: " + DateUtils.formatElapsedTime(appInformation.getUsedTimebyDay() / 1000));
            map.put("times","本次开机操作次数: " + appInformation.getTimes());
            map.put("icon",appInformation.getIcon());
            if (appInformation.getFlagtime() == 0)
                map.put("flagtime", "无使用上限");
            else
                map.put("flagtime", "使用时间上限：" + DateUtils.formatElapsedTime(appInformation.getFlagtime() * 60));
            dataList.add(map);
        }

        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        app_name = ShowList.get(position).getLabel();
        FlagTime = ShowList.get(position).getFlagtime();
//        Toast. makeText (Check_App_Info.this, label, Toast. LENGTH_LONG ).show();

        final String[] items = new String[] { "无", "10min", "20min", "30min", "60min", "90min", "120min" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Check_App_Info.this);
//        builder.setIcon(R.drawable. tools ); //设置对话框的图标
        builder.setTitle("请选择要使用的情景模式：");  //设置对话框的标题
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        tmpTime = 0;
                        break;
                    case 1:
                        tmpTime = 10;
                        break;
                    case 2:
                        tmpTime = 20;
                        break;
                    case 3:
                        tmpTime = 30;
                        break;
                    case 4:
                        tmpTime = 60;
                        break;
                    case 5:
                        tmpTime = 90;
                        break;
                    case 6:
                        tmpTime = 120;
                        break;
                }
                Toast. makeText (Check_App_Info.this, "您选择了" + items[which], Toast. LENGTH_SHORT ).show(); //显示选择结果
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FlagTime = tmpTime;
                Toast.makeText(Check_App_Info.this, "FlagTime: " + FlagTime + "min.", Toast.LENGTH_SHORT).show();
                if(FlagTime == 0){
                    SQliteOpeartion.DeleteFromCurrentTask(MainActivity.db, app_name);
                }else{
                    SQliteOpeartion.UpdateInCurrentTask(MainActivity.db, app_name, (int)FlagTime);
                }
                onResume();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast. makeText (Check_App_Info.this, "还是不立Flag了吧~", Toast. LENGTH_SHORT ).show();
            }
        });

        builder.create().show(); // 创建对话框并显示
    }

}
