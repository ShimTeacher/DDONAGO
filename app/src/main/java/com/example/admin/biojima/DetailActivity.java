package com.example.admin.biojima;

import android.app.TabActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

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


public class DetailActivity extends FragmentActivity {

    private TabHost mTabHost;
    static TextView textView;
    String editText;
    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("DETAILDESC")) {
            editText= intent.getStringExtra("DETAILDESC");
        }
        GetDetailFromResult getDetailFromResult = new GetDetailFromResult();
        getDetailFromResult.execute(editText);


// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);


        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "About", "Map", "Related" };

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    public class GetDetailFromResult extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        private final String LOG_TAG = GetDetailFromResult.class.getSimpleName();


        static final String CONTENTID = "contentid";
        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        static final String MAPX = "mapx";
        static final String MAPY = "mapy";
        static final String TITLE = "title";
        static final String FIRSTIMAGE = "firstimage";
        static final String TEL = "tel";
        static final String OVERVIEW = "overview";

        String totalCount = null;

        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {

            String title=null;
            String contentid=null;
            String mapx=null;
            String mapy=null;
            String firstimage=null;
            String overview=null;
            String tel=null;

            JSONObject attractionJson = new JSONObject(forecastJsonStr);
            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            JSONObject itemObject = itemsObject.getJSONObject(ITEM);

            try {
                mapx = itemObject.getString(MAPX);
                mapy = itemObject.getString(MAPY);
                firstimage = itemObject.getString(FIRSTIMAGE);
                overview = itemObject.getString(OVERVIEW);
                title = itemObject.getString(TITLE);
//                    tel = itemObject.getString(TEL);

            }
            catch (JSONException e)
            {
                Log.v("무언가가 없음", "무언가가 없음");
            }

            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.add(title);
            arrayList.add(firstimage);
            arrayList.add(mapx);
            arrayList.add(mapy);
            arrayList.add(overview);

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
            String contentid= params[0];
            try {

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="+myKey+ "&contentId="+ contentid +"&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json");


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
            TextView textView = (TextView)findViewById(R.id.detailtextView);
                    textView.setText(strings.get(4));
            super.onPostExecute(strings);
        }

    }








//    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {
//        final int PAGE_COUNT = 3;
//        private int tabIcons[] = {R.drawable.ic_tab_one, R.drawable.ic_tab_two, R.drawable.ic_tab_three}
//
//        public SampleFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public int getCount() {
//            return PAGE_COUNT;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return PageFragment.newInstance(position + 1);
//        }
//
//        @Override
//        public int getPageIconResId(int position) {
//            return tabIcons[position];
//        }
//    }

}
