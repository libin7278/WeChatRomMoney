package com.qianghongbao.libin.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qianghongbao.libin.R;
import com.qianghongbao.libin.base.BaseActivity;
import com.qianghongbao.libin.utils.BitmapUtils;

import java.io.File;

/**
 * 功能说明
 */
public class ExplainActivity extends BaseActivity {
    private Button btn_git;
    private Button btn_blog;
    private Button btn_play_tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);

        findView();

        click();
    }

    private void click() {
        btn_git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://github.com/libin7278/WeChatRomMoney");
                intent.setData(content_url);
                startActivity(intent);

            }
        });

        btn_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://blog.csdn.net/github_33304260?viewmode=contents");
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        btn_play_tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDonateDialog();
            }
        });
    }

    private void findView() {
        btn_git = (Button) findViewById(R.id.btn_git);
        btn_play_tour = (Button) findViewById(R.id.btn_play_tour);
        btn_blog = (Button) findViewById(R.id.btn_blog);
    }

    /** 显示捐赠的对话框*/
    private void showDonateDialog() {
        final Dialog dialog = new Dialog(this, R.style.QR_Dialog_Theme);
        View view = getLayoutInflater().inflate(R.layout.donate_dialog_layout, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                File output = new File(android.os.Environment.getExternalStorageDirectory(), "weChart_RobMoney.jpg");
                if(!output.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wechatpay_qr);
                    BitmapUtils.saveBitmap(ExplainActivity.this, output, bitmap);
                }
                Toast.makeText(ExplainActivity.this, "已保存到:" + output.getAbsolutePath(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }



}
