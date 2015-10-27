package com.example.admin.biojima;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;



public class ResultActivity extends FragmentActivity {
    static Context context;
    static ProgressDialog progressDialog;
    static ArrayAdapter mlistAdapter;
    String[] settings = new String[10];
    private static final String PREFERENCE_KEY = "seekBarPreference";
    FindLocationTaskFromGoogle findLocationTask = new FindLocationTaskFromGoogle();
    static String editTexts = null;
    static String siteTexts = null;
    static int value;
    static String ChooseTime;
    static String ChooseDate;
    static ArrayList<ResultData> m_orders= new ArrayList<ResultData>();
    static ResultDataAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ResultFragment())
                    .commit();
        }

        Intent intent = this.getIntent();

        m_adapter = new ResultDataAdapter(context = getBaseContext(), R.layout.row, m_orders);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            editTexts = intent.getStringExtra(Intent.EXTRA_TEXT);
            siteTexts = intent.getStringExtra("gettitle");
            Log.v("090909",siteTexts);

        }
        findLocationTask.execute(editTexts);
    }

    @Override
    protected void onStart() {


        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   public class ResultDataAdapter extends ArrayAdapter<ResultData> {

        private ArrayList<ResultData> items;

        public ResultDataAdapter(Context context, int textViewResourceId, ArrayList<ResultData> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            ResultData p = items.get(position);
            if (p != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(p.getName());
                }
                if (bt != null) {
                    bt.setText("체감온도 : " + p.getDetailInfo() + "℃    강수확률 : "+ p.getPopInfo()+"%");
                }
            }
            return v;
        }


        public void clear() {
            super.clear();
            items.clear();
        }
    }
        public static class ResultFragment extends Fragment {

            public ResultFragment() {
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

//
//                mlistAdapter =
//                        new ArrayAdapter<String>(
//                                getActivity(), // The current context (this activity)
//                                R.layout.list_item_in_result, // The name of the layout ID.
//                                R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                                new ArrayList<String>());
                View rootView = inflater.inflate(R.layout.fragment_result, container, false);

                ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
                listView.setAdapter(m_adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), semiResultActivity.class)
                                .putExtra(Intent.EXTRA_SHORTCUT_NAME, YoonHo.sigunguCodeArrList.get(position));
                        startActivity(intent);

                    }
                });

                return rootView;
            }
        }

        public class FindLocationTaskFromGoogle extends AsyncTask<String, Void, Void> {

            @Override
            protected void onPreExecute() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String site = prefs.getString(getString(R.string.search_criteria_key),
                        getString(R.string.search_criteria_attraction));
                value = prefs.getInt(PREFERENCE_KEY, 10000);
                ChooseTime = prefs.getString(getString(R.string.time_Selection_key),
                        getString(R.string.time_Selection_12_18));
                ChooseDate = prefs.getString(getString(R.string.date_Selection_key),
                        getString(R.string.date_Selection_tomorrow));

                progressDialog = new ProgressDialog(ResultActivity.this);
                switch (site)
                {
                    case "12":
                        site = "관광지";
                        break;
                    case "14":
                        site = "문화시설";
                        break;
                    case "15":
                        site = "축제 및 공연";
                        break;
                }
                switch (ChooseDate)
                {
                    case "today":
                        ChooseDate = "오늘";
                        break;
                    case "tomorrow":
                        ChooseDate = "내일";
                        break;
                    case "afterTomorrow":
                        ChooseDate = "모레";
                        break;
                }
                switch (ChooseTime)
                {
                    case "3":
                        ChooseTime = "0-6 시";
                        break;
                    case "0":
                        ChooseTime = "6-12 시";
                        break;
                    case "1":
                        ChooseTime = "12-18 시";
                        break;
                    case "2":
                        ChooseTime = "18-24 시";
                        break;
                }
                String str = editTexts.replace("0","");
                progressDialog.setMessage("잠시만 기다려주세요...................");
                progressDialog.show();
 context = getApplicationContext();
                super.onPreExecute();
            }


            private void update(String lat, String lon) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String site = prefs.getString(getString(R.string.search_criteria_key),
                        getString(R.string.search_criteria_attraction));
                int value = prefs.getInt(PREFERENCE_KEY, 10000);
                String ChooseTime = prefs.getString(getString(R.string.time_Selection_key),
                        getString(R.string.time_Selection_12_18));
                String ChooseDate = prefs.getString(getString(R.string.date_Selection_key),
                        getString(R.string.date_Selection_tomorrow));

                settings[0] = lat;
                settings[1] = lon;
                settings[2] = new Integer(value).toString();
                settings[3] = site;

                YoonHo.ChooseTime = new Integer(ChooseTime);
                YoonHo.ChooseDate = ChooseDate;

                new Hyunbo(settings);
            }

            public JSONObject getLocationFormGoogle(String placesName) {
                StringBuilder stringBuilder = null;
                try {
                    if (placesName.contains(" "))
                        placesName = placesName.replace(" ", "%20");

                    HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyC-JarPhSJxvThZ8Vx3FrhuyIHJ4k_mftU&address=" + placesName + "&sensor=false");
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response;
                    stringBuilder = new StringBuilder();


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
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jsonObject1 = results.getJSONObject(0);
                    JSONObject geometry = jsonObject1.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    lon = location.getDouble("lng");
                    lat = location.getDouble("lat");


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                a[0] = lat;
                a[1] = lon;

                return a;

            }

            @Override
            protected Void doInBackground(String... params) {

                final int LAT = 0;
                final int LNG = 1;

                //주소로 검색을 했을 경우
                if ('0' == params[0].charAt(0)) {

                    //주어진 주소로부터 위도경도를 구하는 부분
                    JSONObject jsonObject = getLocationFormGoogle(params[0].substring(1));
                    Double[] latlng = getLatLng(jsonObject);

                    update(latlng[LAT].toString(), latlng[LNG].toString());

                    return null;
                }
                //지도로 검색했을 경우
                else {
                    String[] LatLon;
                    LatLon = params[0].split(",");

                    update(LatLon[0], LatLon[1]);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
            }
        }

    }
