package com.example.el_project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ns on 2018/5/16.
 */

public class MyAlgorithm {
    public static int calcScores(int totalTime,int usedTime,int breakCount){
        int[] highestScores={96,97,98,99,100};
        int breakWeight=0;
        double timeRate=(double)usedTime/totalTime;
        int scores=timeRate>=0.75? highestScores[(int)(Math.random()*5)]:(int)(timeRate*100+20);
        while(breakCount>0){
            scores-=(int)(Math.random()*(2+0.3*breakWeight));
            breakWeight++;
            breakCount--;
        }
        return scores;
    }

    public static int calcDateDiffWeight(String currentDate,String deadline)throws ParseException{
        int result=calcDateDifference(currentDate,deadline);
        return result>0? 5-result:0;
    }

    public static int calcDateDifference(String date1,String date2)throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date1));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(date2));
        long time2 = cal.getTimeInMillis();
        if(time2<=time1)
            return -1;
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static int calcDateDiffWeight(String deadline){
        try {
            int result = calcDateDifference(deadline);
            return result > 0 ? 5 - result : 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static int calcDateDifference(String date)throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = formatDate.format(calendar.getTime());
        cal.setTime(sdf.parse(currentDate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(date));
        long time2 = cal.getTimeInMillis();
        if(time2<=time1)
            return -1;
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static String getTaskRestDays(String ddl){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String deadLine=ddl.split(" ")[0];
        String currentDate=formatDate.format(calendar.getTime());
        try {
            int diff = MyAlgorithm.calcDateDifference(currentDate, deadLine);
            if(diff>0&&diff<=3){
                switch (diff){
                    case 1:
                        return "1天";
                    case 2:
                        return "2天";
                    case 3:
                        return "3天";
                }
            }
            else if(diff>3){
                String[] temp=deadLine.substring(5).split("-");
                if(Integer.parseInt(temp[0])<10)
                    return temp[0].substring(1)+"."+temp[1];
                else
                    return temp[0]+"."+temp[1];
            }
            else
                return "";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
