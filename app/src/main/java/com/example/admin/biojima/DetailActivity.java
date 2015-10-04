package com.example.admin.biojima;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DetailActivity extends FragmentActivity {
    static ProgressDialog progressDialog;
    static ArrayAdapter mDetailAdapter;


    private static final String PREFERENCE_KEY = "seekBarPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
        Intent intent = this.getIntent();
        String editText=null;

        if (intent != null && intent.hasExtra(Intent.EXTRA_SHORTCUT_NAME)) {
            editText= intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
            Toast.makeText(getApplicationContext(), editText, Toast.LENGTH_SHORT).show();
        }
        FindLocationfFromResult findLocationfFromResult = new FindLocationfFromResult();
        findLocationfFromResult.execute(editText);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mDetailAdapter =
            new ArrayAdapter<String>(
                    getActivity(), // The current context (this activity)
                    R.layout.list_item_forecast2, // The name of the layout ID.
                    R.id.list_item_forecast_textview2, // The ID of the textview to populate.
                    new ArrayList<String>());
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast2);
            listView.setAdapter(mDetailAdapter);
            return rootView;
        }
    }


    public class FindLocationfFromResult extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        ProgressDialog asyncDialog;

        private final String LOG_TAG = FindLocationfFromResult.class.getSimpleName();

        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";
        static final String ADDR = "addr1";
        static final String AREACODE = "areacode";
        static final String SIGUNGUCODE = "sigungucode";
        static final String MAPX = "mapx";
        static final String MAPY = "mapy";
        static final String TITLE = "title";

        String totalCount = null;

        HashMap<String , String[]> map = new HashMap<String , String[]>();


        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {

            String[] List = null; // 최종적으로 윤호에게 넘기게 될 데이터가 들어갈 String배열

            String mapx;
            String mapy;
            String addr;
            String areacode;
            String sigungucode;
            String title;

            ArrayList<String> arrayList = new ArrayList<String>();


                    JSONObject attractionJson = new JSONObject(forecastJsonStr);
                    JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
                    JSONObject bodyObject = responseObject.getJSONObject(BODY);
                JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                JSONArray itemArray = itemsObject.getJSONArray(ITEM);
            totalCount = bodyObject.getString(TOTAL_COUNT);

            int val = Integer.parseInt(totalCount);
                String[] test;

                for (int i = 0; i < val; i++) {
                    JSONObject AttracionObject = itemArray.getJSONObject(i);
                    try{
                        mapx = AttracionObject.getString(MAPX);
                        mapy = AttracionObject.getString(MAPY);
                        addr = AttracionObject.getString(ADDR);
                        title = AttracionObject.getString(TITLE);

                    }
                    catch (JSONException e)
                    {
                        Log.v(LOG_TAG,"json exception");
                        break;
                    }


                    arrayList.add(title);

                }


                return arrayList;

        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String myKey = "Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";
            // Will contain the raw JSON response as a string.
            String AttracionJsonStr = null;
            String input= params[0];
            try {


                String areaCode = input.split(",")[0];
                String sigunguCode = input.split(",")[1];



                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String site = prefs.getString(getString(R.string.search_criteria_key),
                        getString(R.string.search_criteria_attraction));

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="
                        +myKey+"&contentTypeId="+ site +
                        "&areaCode="+areaCode+"&sigunguCode="+sigunguCode+"&listYN=Y&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&arrange=B&numOfRows=100&pageNo=1&_type=json");


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

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

            return null;}

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if(Integer.parseInt(totalCount)==0)
            {
                Log.v("qkadp"," total count = 0 정보가 없음");
            }
            else
            {
                try {

                    for(String a : strings)
                    {
                        Log.v("zxcv",a);
                    }
                }catch (Exception e)
                {

                }

            }
            mDetailAdapter.clear();
            for(String str : strings)
            {
                mDetailAdapter.add(str);
            }
            super.onPostExecute(strings);
        }

    }

}