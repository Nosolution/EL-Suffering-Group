package com.example.el_project;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;

public class TempPicStorageManager {
    private String dirPath;
    private Context context;

    TempPicStorageManager(Context context, String dirName){
        this.context = context;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            dirPath = context.getExternalFilesDir(dirName).getAbsolutePath();
        }else {
            dirPath = context.getFilesDir()+ File.separator+dirName;
        }
        File file = new File(dirPath);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public String getDirPath(){
        return dirPath;
    }

    public boolean cleanDir(String dirPath){
        File dir = new File(dirPath);

        if(!dir.exists() || !dir.isDirectory()){
            Log.d("DIR_DELETE_ERROR", "clean: 文件夹不存在或非文件");
            return false;
        }
        boolean flag = true;
        File[] childrenFiles = dir.listFiles();
        for (File file: childrenFiles){
            if(file.isFile()){
                flag = file.delete();
            }else if(file.isDirectory()){
                flag = cleanDir(file.getAbsolutePath());
            }else {
                return false;
            }
            if (!flag){
                return false;
            }
        }

        return flag;
    }

    public boolean clean(){
        return cleanDir(dirPath);
    }
}
