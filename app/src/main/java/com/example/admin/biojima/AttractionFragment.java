package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 8. 28..
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class AttractionFragment extends Fragment {

    //TestCode
    static Double X = 127.0409111; //경도
    static Double Y = 37.65508056; //위도

    //TestCode

    private ArrayAdapter<String> mForecastAdapter;
    private View rootView;
    public AttractionFragment() {
    }



    TextView textview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if this is set true,
        // Activity.onCreateOptionsMenu will call Fragment.onCreateOptionsMenu
        // Activity.onOptionsItemSelected will call Fragment.onOptionsItemSelected
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        String[] a = getLastKnownLocation();
        Hyunbo hyunbo = new Hyunbo(a);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh)
        {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tabSetting();//Rootview가 설정이 된 후에 셋팅이되어야한다.


        return rootView;
    }





    public String[] getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        Double latitude = bestLocation.getLatitude();
        Double longitude = bestLocation.getLongitude();

        String[] array = new String[2];
        array[0] = latitude.toString();
        array[1] = longitude.toString();


        return array;
    }



    public void tabSetting() //메뉴 기본탭을 셋팅한다.
    {
        TabHost tabhost = (TabHost)rootView.findViewById(R.id.tabHost);
        tabhost.setup();

        TabHost.TabSpec spec1 = tabhost.newTabSpec("Tab1").setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        TabHost.TabSpec spec2 = tabhost.newTabSpec("Tab2").setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        TabHost.TabSpec spec3 = tabhost.newTabSpec("Tab3").setContent(R.id.tab3).setIndicator(getString(R.string.tab3));
        TabHost.TabSpec spec4 = tabhost.newTabSpec("Tab3").setContent(R.id.tab4).setIndicator(getString(R.string.tab4));

        tabhost.addTab(spec1);
        tabhost.addTab(spec2);
        tabhost.addTab(spec3);
        tabhost.addTab(spec4);

        tabhost.getTabWidget().getChildAt(0).getLayoutParams().height=80;
        tabhost.getTabWidget().getChildAt(1).getLayoutParams().height=80;
        tabhost.getTabWidget().getChildAt(2).getLayoutParams().height=80;
        tabhost.getTabWidget().getChildAt(3).getLayoutParams().height=80;

    }






}