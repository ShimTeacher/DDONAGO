package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 8. 28..
 */

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.List;

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

        String[] a = new String[3];
        a[0] = "33.27635833";
           a[1] =    "126.7220889";
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




//        editText = (EditText) rootView.findViewById(R.id.editText);
//        textView = (TextView) rootView.findViewById(R.id.textView);
//        button = (Button)rootView.findViewById(R.id.button5);
//
//        coder = new Geocoder(getActivity(), Locale.KOREAN); //주소를이용해서찾아준다
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String address = editText.getText().toString(); //주소받아옴
//                //Toast toastView = Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_LONG);
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
//                            Hyunbo.lat=new Double(curAddress.getLatitude()).toString();
//                            Hyunbo.lon=new Double(curAddress.getLongitude()).toString();
//                            buffer.append("\n\tlatitude: " + curAddress.getLatitude());
//                            buffer.append("\n\tlongitude: " + curAddress.getLongitude());
//
//                            textView.append("\nAddress #" + i + " : " + buffer.toString());
//                            String[] list = getLastKnownLocation();
//                            Hyunbo hyunbo = new Hyunbo(list);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


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
