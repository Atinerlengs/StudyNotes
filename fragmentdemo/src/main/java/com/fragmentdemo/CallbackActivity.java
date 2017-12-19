package com.fragmentdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fragment.LeftFragment;

/**
 * Created by liqiang on 17-12-19.
 */

public class CallbackActivity extends FragmentActivity{

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callback_activity_layout);

        /* 获取对应的控件 */
        button = (Button) this.findViewById(R.id.button);
        /* 获取manager */
        manager = this.getSupportFragmentManager();
        /* 创建事物 */
        transaction = manager.beginTransaction();

        /* 创建LeftFragment(在内部类中使用到了，所以要用final) */
        final LeftFragment leftFragment = new LeftFragment();
        /* 把Fragment添加到对应的位置 */
        transaction.add(R.id.left, leftFragment, "left");
        /* 提交事物 */
        transaction.commit();

        /* 设置按钮的监听事件 */
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                leftFragment.getEditText(new LeftFragment.CallBack() {
                    @Override
                    public void getResult(String result) {
                        Toast.makeText(CallbackActivity.this, "-->>" + result, Toast.LENGTH_SHORT).show();                    }
                });

            }
        });

    }

}
