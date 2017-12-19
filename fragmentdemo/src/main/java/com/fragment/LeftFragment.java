package com.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fragmentdemo.R;

/**
 * Created by liqiang on 17-12-19.
 */


public class LeftFragment extends Fragment {

    private Button button;
    private EditText editText;

    public LeftFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*动态加载布局*/
        View view = inflater.inflate(R.layout.left, null);
        /*从动态布局中获取对应的控件*/
        editText = (EditText) view.findViewById(R.id.editText1);
        return view;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    public void getEditText(CallBack callBack){
        String msg = editText.getText().toString();
        callBack.getResult(msg);
    }
    public interface CallBack{
        public void getResult(String result);
    }
}
