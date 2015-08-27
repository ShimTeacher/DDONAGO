package com.example.admin.biojima;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import org.json.JSONException;


public class MainActivity extends Activity {


    //TestCode
    static Double X = 127.0409111; //경도
    static Double Y = 37.65508056; //위도
    static int radious = 3;
    //TestCode


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       tabSetting();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;


        }


        return super.onOptionsItemSelected(item);
    }

    public void tabSetting() //메뉴 기본탭을 셋팅한다.
    {
        TabHost tabhost = (TabHost)findViewById(R.id.tabHost);
        tabhost.setup();

        TabHost.TabSpec spec1 = tabhost.newTabSpec("Tab1").setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        TabHost.TabSpec spec2 = tabhost.newTabSpec("Tab2").setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        TabHost.TabSpec spec3 = tabhost.newTabSpec("Tab3").setContent(R.id.tab3).setIndicator(getString(R.string.tab3));
        //TabHost.TabSpec spec4 = tabhost.newTabSpec("Tab3").setContent(R.id.tab4).setIndicator(getString(R.string.tab4));

        tabhost.addTab(spec1);
        tabhost.addTab(spec2);
        tabhost.addTab(spec3);
        //tabhost.addTab(spec4);

        tabhost.getTabWidget().getChildAt(0).getLayoutParams().height=80;
        tabhost.getTabWidget().getChildAt(1).getLayoutParams().height=80;
        tabhost.getTabWidget().getChildAt(2).getLayoutParams().height=80;
        //tabhost.getTabWidget().getChildAt(3).getLayoutParams().height=80;

    }
}
