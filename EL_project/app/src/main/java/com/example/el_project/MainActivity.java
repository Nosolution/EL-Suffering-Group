package com.example.el_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.example.el_project.R;
import com.example.el_project.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<com.example.el_project.Task> taskList=new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.title_toolbar);
        setSupportActionBar(toolbar);
        initTasks();  //初始化数据
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);  //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能

        //创建默认的线性LayoutManager
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //设置Adapter
        RecyclerViewAdapter adapter=new RecyclerViewAdapter(taskList);
        recyclerView.setAdapter(adapter);
    }

    private void initTasks(){
        for(int i=0;i<2;i++){
            Task apple=new Task("Apple",R.drawable.test_image,R.drawable.pink);
            taskList.add(apple);
            Task banana=new Task("Banana",R.drawable.test_image,R.drawable.red);
            taskList.add(banana);
            Task orange=new Task("Orange",R.drawable.test_image,R.drawable.purple);
            taskList.add(orange);
            Task watermelon=new Task("Watermelon",R.drawable.test_image,R.drawable.gray);
            taskList.add(watermelon);
            Task grape=new Task("Grape",R.drawable.test_image,R.drawable.green);
            taskList.add(grape);

        }
    }
}
