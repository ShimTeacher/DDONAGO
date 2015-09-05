package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 8. 28..
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
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
        FetchAttractionTask fetchAttractionTask  = new FetchAttractionTask();
        fetchAttractionTask.execute();
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


        String[] data = {};
        List<String> Attractiondata = new ArrayList<String>(Arrays.asList(data));


        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        Attractiondata);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tabSetting();//Rootview가 설정이 된 후에 셋팅이되어야한다.

        textview = (TextView) rootView.findViewById(R.id.textView);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        Button btn = (Button)rootView.findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
            }
        });


        return rootView;
    }


    private void findLocation(){
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); //매니저


        long minTime = 10000; //10초간격업데이트 밀리세컨드
        float minDistance = 0;   //움직엿을때업데이트 항상업데이트

        MyLocationListener listener = new MyLocationListener();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, listener);  //위치요청 GPSPROVIDER는오차가크다
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, listener);
        Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation != null)
        {
            Double latitude = lastLocation.getLatitude();
            Double longitude = lastLocation.getLongitude();


            Log.d("ffff", new Double(latitude).toString());
            Log.d("ffff", new Double(longitude).toString());

//            textview.setText("가장 최근 내 위치 : " +latitude + " , " + longitude );
//            textview.invalidate();
        }
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {    //location manager가 이메소드를참고하고업데이트함
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

//            textview.setText("내 위치 : " +latitude + " , " + longitude );
//            textview.invalidate();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
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
    String totalCount;
    public class FetchAttractionTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchAttractionTask.class.getSimpleName();

        private String[] getAttractionDataFromJson(String forecastJsonStr)
            throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESPONSE = "response";
            final String BODY = "body";
            final String PAGE_NUM = "pageNo";
            final String NUM_OF_ROWS = "numOfRows";
            final String TOTAL_COUNT = "totalCount";
            final String ITEMS = "items";
            final String ITEM = "item";

            String[] List = null;
            String numOfRows;
            String mapx;
            String mapy;


            JSONObject attractionJson = new JSONObject(forecastJsonStr);
            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            totalCount = bodyObject.getString(TOTAL_COUNT);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            if (Integer.parseInt(totalCount) == 0) {
                return null;
            } else if (Integer.parseInt(totalCount) == 1)
            {
                JSONObject itemObject = itemsObject.getJSONObject(ITEM);
                mapx = itemObject.getString("mapx");
                mapy = itemObject.getString("mapy");
                StringBuilder PointObject = new StringBuilder(mapx);
                PointObject.append(", "+mapy);
                List = new String[1];
                List[0] = PointObject.toString();

                return List;
            } else
            {
                JSONArray itemArray = itemsObject.getJSONArray(ITEM);

                int val = Integer.parseInt(totalCount);
                List = new String[val];

                for (int i = 0; i < val; i++)
                {
                    JSONObject AttracionObject = itemArray.getJSONObject(i);
                    mapx = AttracionObject.getString("mapx");
                    mapy = AttracionObject.getString("mapy");
                    StringBuilder PointObject = new StringBuilder(mapx);
                    PointObject.append(", "+mapy);
                    List[i] = PointObject.toString();
                }

                return List;
            }


        }





        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String myKey = "Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";
            // Will contain the raw JSON response as a string.
            String AttracionJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                Double x = 127.0409111;
                Double y = 37.65508056;
                String radious = "20000";

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey="
                        +myKey+"&contentTypeId=12&mapX="
                        +x.toString()+"&mapY="
                        +y.toString()+"&radius="
                        +radious+"&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=1000&pageNo=1&_type=json");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                AttracionJsonStr = buffer.toString();


                //Log.v(LOG_TAG,"JSON-==-=-=--= "+AttracionJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            } try {
                return getAttractionDataFromJson(AttracionJsonStr);
            } catch (JSONException e) {


                Log.e(LOG_TAG, e.getMessage(), e);

                e.printStackTrace();

            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;}

        protected void onPostExecute(String[] result) {


            if(Integer.parseInt(totalCount)==0)
            {
                Toast.makeText(getActivity().getApplicationContext(),"관광지 정보가 없습니다 ㅜㅜ",Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getActivity().getApplicationContext(),totalCount.toString()+"개의 관광지가 검색됨",Toast.LENGTH_LONG).show();

            if (result != null) {
                mForecastAdapter.clear();
                for(String AttractionStr : result) {
                    mForecastAdapter.add(AttractionStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }


}
