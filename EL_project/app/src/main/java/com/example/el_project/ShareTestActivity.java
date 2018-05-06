package com.example.el_project;

/*
*
* 分享到qq空间的测试活动，已成功
*
* */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ShareTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnShare;
    private Tencent mTencent;
    private MyIUiListener myIUiListener;
    private Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_test);

        btnShare = (Button)findViewById(R.id.button_share_to_qq);
        btnShare.setOnClickListener(this);

        mTencent = Tencent.createInstance("1106810223", getApplicationContext());
        myIUiListener = new MyIUiListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_share_to_qq:

                Calendar now = new GregorianCalendar();
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String fileName = simpleDate.format(now.getTime());
                Log.d("TEST", "onClick: " + fileName);

                String dicPath = getFilePath(this, "tempPicToShare");
                Log.d("TEST", "onClick: " + dicPath);

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_back_snake, options);
                Log.i("TEST", "options: " + options.inDensity + "," + options.inTargetDensity);

                String filePicStoredPath = dicPath + File.separator + fileName + ".jpg";
                File filePicStored = new File(filePicStoredPath);
                try {
                    FileOutputStream out = new FileOutputStream(filePicStored);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                }catch (FileNotFoundException e){
                    Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }catch (IOException e){
                    Toast.makeText(this, "IO Exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                shareImgToQQ(filePicStoredPath);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, myIUiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, myIUiListener);
            }
        }
    }

    private void shareImgToQQ(String imgUrl){
        params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);// 设置分享类型为纯图片分享
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);// 需要分享的本地图片URL
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//默认分享到空间
        mTencent.shareToQQ(ShareTestActivity.this, params, myIUiListener);
    }

    public static String getFilePath(Context context, String dir) {
        String directoryPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ) {//判断外部存储是否可用
            directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
        }else{//没外部存储就使用内部存储
            directoryPath = context.getFilesDir()+ File.separator+dir;
        }
        File file = new File(directoryPath);
        if(!file.exists()){//判断文件目录是否存在
            file.mkdirs();
        }
        return directoryPath;
    }
}
