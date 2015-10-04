package com.example.admin.biojima;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Arrays;
import java.util.List;



public class ResultActivity extends FragmentActivity {
    static ProgressDialog progressDialog;
    static ArrayAdapter mlistAdapter;
    String[] settings = new String[10];
    private static final String PREFERENCE_KEY = "seekBarPreference";
    FindLocationTaskFromGoogle findLocationTask = new FindLocationTaskFromGoogle();
    static String editTexts = null;
    {

    }
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

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            editTexts= intent.getStringExtra(Intent.EXTRA_TEXT);
//            Log.v("Test",editText);
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


    public static class ResultFragment extends Fragment {

        public ResultFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mlistAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_forecast, // The name of the layout ID.
                            R.id.list_item_forecast_textview, // The ID of the textview to populate.
                            new ArrayList<String>());
            View rootView = inflater.inflate(R.layout.fragment_result, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mlistAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
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
                progressDialog = new ProgressDialog(ResultActivity.this);
                progressDialog.setMessage("쪼매만 기다려 주쇼잉?");
                progressDialog.show();
                super.onPreExecute();
            }


        private void update(String lat,String lon) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String site = prefs.getString(getString(R.string.search_criteria_key),
                    getString(R.string.search_criteria_attraction));
            int value = prefs.getInt(PREFERENCE_KEY, 10000);
            String ChooseTime = prefs.getString(getString(R.string.time_Selection_key),
                    getString(R.string.time_Selection_12_18));
            String ChooseDate = prefs.getString(getString(R.string.date_Selection_key),
                    getString(R.string.date_Selection_today));

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

                    HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + placesName + "&ka&sensor=false");
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
                    lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
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

            @Override
            protected Void doInBackground(String... params) {

                final int LAT = 0;
                final int LNG = 1;
                JSONObject jsonObject = getLocationFormGoogle(params[0]);
                Double[] latlng = getLatLng(jsonObject);

                update(latlng[LAT].toString(), latlng[LNG].toString());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
            }
        }

    }
