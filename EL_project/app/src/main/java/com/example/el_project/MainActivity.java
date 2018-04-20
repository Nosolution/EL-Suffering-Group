package com.example.el_project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<com.example.el_project.Task> taskList=new ArrayList<>();
    private RecyclerView recyclerView;
    private MyDatabaseHelper dbHelper;
    private FloatingActionButton button1;

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

        button1 =(FloatingActionButton)findViewById(R.id.fab_add);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initTasks(){
        for(int i=0;i<2;i++){
            Task apple=new Task("Apple",R.drawable.pink);
            taskList.add(apple);
            Task banana=new Task("Banana",R.drawable.red);
            taskList.add(banana);
            Task orange=new Task("Orange",R.drawable.purple);
            taskList.add(orange);
            Task watermelon=new Task("Watermelon",R.drawable.gray);
            taskList.add(watermelon);
            Task grape=new Task("Grape",R.drawable.green);
            taskList.add(grape);
        }
    }
}
//