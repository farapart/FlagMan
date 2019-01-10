package com.example.xiaoxiaobai.flagman;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check_Flag extends AppCompatActivity {
    private ArrayList<App_Info> ShowList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);

        List<Map<String,Object>> datalist = null;

        Get_App_Info statisticsInfo = new Get_App_Info(this);
        ShowList = statisticsInfo.getShowList();
        datalist = getDataList(ShowList);

        ListView listView = (ListView)findViewById(R.id.AppStatisticsList);

        SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.inner_list_check,
                new String[]{"label","info","icon","flagtime"},
                new int[]{R.id.label2,R.id.info2,R.id.icon2,R.id.flagtime2});
        listView.setAdapter(adapter);

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

        TaskInfo result = new TaskInfo();
        result.finished_num = 0;
        result.task_num = 0;

        for(App_Info appInformation : ShowList) {
            map = new HashMap<String,Object>();
            if (appInformation.getFlagtime() == 0) {
                continue;
            }
            else {
                result.task_num++;
                map.put("label", appInformation.getLabel());
                map.put("info", "运行时间: " + DateUtils.formatElapsedTime(appInformation.getUsedTimebyDay() / 1000));
                map.put("icon", appInformation.getIcon());
                if (appInformation.getFlagtime() * 60 * 1000 > appInformation.getUsedTimebyDay()) {
                    result.finished_num++;
                    map.put("flagtime", "你的Flag：" + (DateUtils.formatElapsedTime(appInformation.getFlagtime() * 60) + "成功啦！"));
                }
                else
                    map.put("flagtime", "你的Flag：" + (DateUtils.formatElapsedTime(appInformation.getFlagtime() * 60) + "又倒啦！"));
            }
            dataList.add(map);
        }

        Calendar calendar = Calendar.getInstance();
        result.year = calendar.get(Calendar.YEAR);
        result.month = calendar.get(Calendar.MONTH) + 1;
        result.day = calendar.get(Calendar.DAY_OF_MONTH);

        SQliteOpeartion.InsertIntoTask(MainActivity.db, result);
//        SQliteOpeartion.DeleteAllCurrentTask(MainActivity.db);
        return dataList;
    }
}
