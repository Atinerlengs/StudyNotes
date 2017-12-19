package com.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fragmentdemo.DynamicFragment;
import com.fragmentdemo.R;

/**
 * Created by liqiang on 17-12-19.
 */

public class DynamicActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_activity_layout);
        Button btnLoadFrag1 = (Button)findViewById(R.id.btn_show_fragment1);
        btnLoadFrag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                DynamicFragment fragment1 = new DynamicFragment();
                transaction.add(R.id.fragment_container, fragment1);
                transaction.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
