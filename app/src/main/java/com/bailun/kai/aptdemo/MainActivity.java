package com.bailun.kai.viewjet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.bailun.kai.aptlib.FastClick;
import com.bailun.kai.fastclick.ViewJet;
import com.bailun.kai.viewjet.R;

public class MainActivity extends AppCompatActivity {


    private int count =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewJet.init(this);
    }


    @FastClick(R.id.bt_test)
    public void test(View view){
        count ++;
        Log.e("打印",count+"");
        Toast.makeText(this,count+"",Toast.LENGTH_SHORT).show();
    }
}
