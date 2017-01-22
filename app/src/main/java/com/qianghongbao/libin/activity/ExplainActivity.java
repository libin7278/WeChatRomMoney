package com.qianghongbao.libin.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qianghongbao.libin.R;
import com.qianghongbao.libin.base.BaseActivity;

/**
 * 功能说明
 */
public class ExplainActivity extends BaseActivity {
    private Button btn_git;
    private Button btn_blog;

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
    }

    private void findView() {
        btn_git = (Button) findViewById(R.id.btn_git);
        btn_blog = (Button) findViewById(R.id.btn_blog);
    }
}
