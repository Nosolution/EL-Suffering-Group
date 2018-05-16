package com.example.el_project;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomTimeToFinishPicker {
    /**
     * 定义结果回调接口
     */
    public interface ResultHandler {
        void handle(String hour, String min);
    }

    public enum SCROLL_TYPE {
        HOUR(1),
        MINUTE(2);

        SCROLL_TYPE(int value) {
            this.value = value;
        }

        public int value;
    }

    private int scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value;
    private ResultHandler handler;
    private Context context;
    private boolean canAccess = false;

    private Dialog timePickDialog;

    private DatePickerView hourPicker;
    private DatePickerView minutePicker;
    private TextView textCancel;
    private TextView textSelect;

    private String selectedHour;
    private String selectedMinute;

    private ArrayList<String> hourList, minuteList;

    public CustomTimeToFinishPicker(Context context, ResultHandler handler){
        canAccess = true;
        this.context = context;
        this.handler = handler;

        initDialog();
        initView();
    }

    private void initDialog(){
        if (timePickDialog == null){
            timePickDialog = new Dialog(context, R.style.time_dialog);
            timePickDialog.setCanceledOnTouchOutside(false);
            timePickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            timePickDialog.setContentView(R.layout.time_to_finish_picker);
            Window window = timePickDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initView(){
        hourPicker = (DatePickerView)timePickDialog.findViewById(R.id.hour_picker);
        minutePicker = (DatePickerView)timePickDialog.findViewById(R.id.minter_picker);
        textCancel = timePickDialog.findViewById(R.id.text_cancel);
        textSelect = timePickDialog.findViewById(R.id.text_select);

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog.dismiss();
            }
        });
        textSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.handle(selectedHour, selectedMinute);
                timePickDialog.dismiss();
            }
        });
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void initPicker(){
        initArrayList();
        for (int i = 0; i < 25; i++){
            hourList.add(formatTimeUnit(i));
        }
        for (int i = 0; i < 60; i += 5){
            minuteList.add(formatTimeUnit(i));
        }
        loadComponent();
    }

    private void initArrayList(){
        if (hourList == null) hourList = new ArrayList<>();
        if (minuteList == null) minuteList = new ArrayList<>();
        hourList.clear();
        minuteList.clear();
    }

    private void loadComponent(){
        hourPicker.setData(hourList);
        minutePicker.setData(minuteList);
        hourPicker.setCanScroll(true);
        minutePicker.setCanScroll(true);
    }

    private void addListener(){
        hourPicker.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedHour = text;
            }
        });
        minutePicker.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedMinute = text;
            }
        });
    }

    private void setSelected(String time){
        String[] times = time.split(":");
        hourPicker.setSelected(times[0]);
        minutePicker.setSelected(times[1]);
    }

    public void show(String time){
        if (canAccess) {
            initPicker();
            addListener();
            setSelected(time);
            timePickDialog.show();
        }
    }
}
