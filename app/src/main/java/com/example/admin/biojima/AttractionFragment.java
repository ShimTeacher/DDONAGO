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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class AttractionFragment extends Fragment {


    private static final String PREFERENCE_KEY = "seekBarPreference";
    EditText editText;
    Geocoder coder;
    TextView textView;
    ImageButton button;

    ImageButton settingButton;


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
    }

    private void update(String lat,String lon) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        String site = prefs.getString(getString(R.string.search_criteria_key),
                       getString(R.string.pref_location_default));
        int value = prefs.getInt(PREFERENCE_KEY, 0);

        String ChooseTime = prefs.getString(getString(R.string.time_Selection_key),
                getString(R.string.pref_location_default));

        String ChooseDate = prefs.getString(getString(R.string.date_Selection_key),
                getString(R.string.pref_location_default));

        settings[0] = lat;
        settings[1] = lon;
        settings[2] = new Integer(value).toString();
        settings[3] = site;

        YoonHo.ChooseTime = new Integer(ChooseTime);


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

        settingButton = (ImageButton)rootView.findViewById(R.id.settingButton);

        settingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        editText = (EditText) rootView.findViewById(R.id.editText);
        //textView = (TextView) rootView.findViewById(R.id.textView);
        button = (ImageButton)rootView.findViewById(R.id.findButton);

        coder = new Geocoder(getActivity(), Locale.KOREAN); //주소를이용해서찾아준다
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editText.getText().toString(); //주소받아옴

                Double[] Source = getLatLng(getLocationFormGoogle(address));

                Log.d("gggg",new Double(Source[0]).toString());
                Log.d("gggg", new Double(Source[1]).toString());

                //Toast toastView = Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_LONG);
//                try {
//                    List<Address> addressList = coder.getFromLocationName(address, 3); //name을통해인식 동일한이름으로 최대 3개까지 반환하겠다
//                    if (addressList != null) {
//                        for (int i = 0; i < addressList.size(); i++) {
//                            Address curAddress = addressList.get(i);
//                            StringBuffer buffer = new StringBuffer();
//                            for (int k = 0; k <= curAddress.getMaxAddressLineIndex(); k++) {
//                                buffer.append(curAddress.getAddressLine(k));
//                            }
//
////                            Hyunbo.lat = new Double(curAddress.getLatitude()).toString();
////                            Hyunbo.lon = new Double(curAddress.getLongitude()).toString();
//                            buffer.append("\n\tlatitude: " + curAddress.getLatitude());
//                            buffer.append("\n\tlongitude: " + curAddress.getLongitude());
//
//                            // textView.append("\nAddress #" + i + " : " + buffer.toString());
//                            Log.d("gggg", "\nAddress #" + i + " : " + buffer.toString());
//                            String[] list = getLastKnownLocation();
//                            Log.d("gggg",list[0]);
//                            Log.d("gggg", list[1]);
//
//                            update(list[0],list[1]);
//                        }
//                    }
//                } catch (IllegalArgumentException e) {
//                    Log.d("gggg", "argumentError");
//                    e.printStackTrace();
//                } catch(IOException e){
//                    Log.d("gggg","ioExecpion");
//                }


            }
        });
        return rootView;
    }

    public  JSONObject getLocationFormGoogle(String placesName) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" +placesName+"&ka&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonObject;
    }

    public Double[] getLatLng(JSONObject jsonObject) {

        Double[] a = new Double[2];

        Double lon = new Double(0);
        Double lat = new Double(1);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        a[0] = lat;
        a[1] = lon;

        return a;

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
