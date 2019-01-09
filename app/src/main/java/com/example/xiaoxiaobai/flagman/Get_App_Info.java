package com.example.xiaoxiaobai.flagman;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Get_App_Info {
    private ArrayList<App_Info> ShowList;
    private ArrayList<App_Info> AppInfoList;
    private List<UsageStats> result;
    private long begintime;

    public Get_App_Info(Context context) {
        try {
            begintime = getBeginTime();
            setUsageStatsList(context);
            setShowList();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    //将次数和时间为0的应用信息过滤掉
    private void setShowList() {
        this.ShowList = new ArrayList<>();
        for(int i=0;i<AppInfoList.size();i++) {
            //if(AppInfoList.get(i).getUsedTimebyDay() > 0 ) {
                this.ShowList.add(AppInfoList.get(i));
            //}
        }

        //将显示列表中的应用按显示顺序排序
        for(int i = 0;i<this.ShowList.size() - 1;i++) {
            for(int j = 0; j< this.ShowList.size() - i - 1; j++) {
                if(this.ShowList.get(j).getUsedTimebyDay() < this.ShowList.get(j+1).getUsedTimebyDay()) {
                    App_Info temp = this.ShowList.get(j);
                    this.ShowList.set(j,this.ShowList.get(j+1));
                    this.ShowList.set(j+1,temp);
                }
            }
        }
    }


    //统计当天的应用使用时间
    private void setUsageStatsList(Context context) throws NoSuchFieldException {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        setResultList(context);
        List<UsageStats> Mergeresult = MergeList(this.result);

        for(UsageStats usageStats:Mergeresult) {
            this.AppInfoList.add(new App_Info(usageStats , context));
        }
    }

    private void setResultList(Context context) {
        UsageStatsManager m = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.AppInfoList = new ArrayList<>();
        if(m != null) {
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, begintime, now);
        }
    }

    private long getBeginTime() {
        Calendar calendar = Calendar.getInstance();
        long btime;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        calendar.add(Calendar.SECOND, -1 * second);
        calendar.add(Calendar.MINUTE, -1 * minute);
        calendar.add(Calendar.HOUR, -1 * hour);

        btime = calendar.getTimeInMillis();

        return btime;
    }

    private List<UsageStats> MergeList( List<UsageStats> result) {
        List<UsageStats> Mergeresult = new ArrayList<>();

        for(int i=0;i<result.size();i++) {
            //if(result.get(i).getFirstTimeStamp() > begintime) {
            int num = FoundUsageStats(Mergeresult, result.get(i));
            if (num >= 0) {
                UsageStats u = Mergeresult.get(num);
                u.add(result.get(i));
                Mergeresult.set(num, u);
            } else Mergeresult.add(result.get(i));
            //}
        }
        return Mergeresult;
    }

    private int FoundUsageStats(List<UsageStats> Mergeresult, UsageStats usageStats) {
        for(int i=0;i<Mergeresult.size();i++) {
            if(Mergeresult.get(i).getPackageName().equals(usageStats.getPackageName())) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<App_Info> getShowList() {
        return ShowList;
    }
}
