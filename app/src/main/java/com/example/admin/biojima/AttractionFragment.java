package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 8. 28..
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class AttractionFragment extends Fragment {



    EditText editText;
    Geocoder coder;
    TextView textView;
    Button button;


    //TestCode
    static Double X = 127.0409111; //경도
    static Double Y = 37.65508056; //위도
    String[] location = new String[2];
    String[] settings= new String [10];
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

       update();




    }

    private void update() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        String site = prefs.getString(getString(R.string.search_criteria_key),
                       getString(R.string.pref_location_default));


        settings[0] = "37.65508056";
        settings[1] = "127.0409111";
        settings[2] = location;
        settings[3] = site;


        Hyunbo hyunbo = new Hyunbo(settings);
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




        editText = (EditText) rootView.findViewById(R.id.editText);
        textView = (TextView) rootView.findViewById(R.id.textView);
        button = (Button)rootView.findViewById(R.id.button5);

        coder = new Geocoder(getActivity(), Locale.KOREAN); //주소를이용해서찾아준다
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editText.getText().toString(); //주소받아옴
                //Toast toastView = Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_LONG);
                try {
                    List<Address> addressList = coder.getFromLocationName(address, 3); //name을통해인식 동일한이름으로 최대 3개까지 반환하겠다
                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address curAddress = addressList.get(i);
                            StringBuffer buffer = new StringBuffer();
                            for (int k = 0; k <= curAddress.getMaxAddressLineIndex(); k++) {
                                buffer.append(curAddress.getAddressLine(k));
                            }

                            Hyunbo.lat=new Double(curAddress.getLatitude()).toString();
                            Hyunbo.lon=new Double(curAddress.getLongitude()).toString();
                            buffer.append("\n\tlatitude: " + curAddress.getLatitude());
                            buffer.append("\n\tlongitude: " + curAddress.getLongitude());

                            textView.append("\nAddress #" + i + " : " + buffer.toString());
                            String[] list = getLastKnownLocation();
                            Hyunbo hyunbo = new Hyunbo(list);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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




    public void onButton1Clicked(View v){
        String address = editText.getText().toString(); //주소받아옴
        //Toast toastView = Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_LONG);
        try {
            List<Address> addressList= coder.getFromLocationName(address, 3); //name을통해인식 동일한이름으로 최대 3개까지 반환하겠다
            if(addressList != null)
            {
                for(int i=0; i< addressList.size(); i++){
                    Address curAddress = addressList.get(i);
                    StringBuffer buffer = new StringBuffer();
                    for(int k=0; k<= curAddress.getMaxAddressLineIndex(); k++){
                        buffer.append(curAddress.getAddressLine(k));
                    }
                    buffer.append("\n\tlatitude: " + curAddress.getLatitude());
                    buffer.append("\n\tlongitude: " + curAddress.getLongitude());

                    textView.append("\nAddress #"+i+" : "+buffer.toString());
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }





}
