package com.example.el_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ns on 2018/4/13.
 */

//此class为添加任务页面的“预计时间”与“优先级”所用的对话框
public class ItemsDialogFragment extends DialogFragment {
    private String title;//对话框标题

    private String[] items;//选项名称

    private DialogInterface.OnClickListener onClickListener;//监听器

    //展示对话框
    public void show(String title, String[] items, DialogInterface.OnClickListener onClickListener,
                     FragmentManager fragmentManager) {
        this.title = title;
        this.items = items;
        this.onClickListener = onClickListener;
        show(fragmentManager, "ItemsDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(items, onClickListener);
        return builder.create();
    }

}
