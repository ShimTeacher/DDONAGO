package com.example.admin.biojima;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adslbna2 on 15. 10. 6..
 */
// In this case, the fragment displays simple text based on the page
public class PageFragment extends Fragment  {
    public static final String ARG_PAGE = "ARG_PAGE";
    String editText;
    static TextView textView;
    ImageView imageView;
    private int mPage;
    private SliderLayout mDemoSlider;
    HashMap<String,String> url_maps = new HashMap<String, String>();

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("DETAILDESC")) {
            editText= intent.getStringExtra("DETAILDESC");
        }
        GetDetailFromResult getDetailFromResult = new GetDetailFromResult();
        getDetailFromResult.execute(editText);
        GetDetailImageFromResult getDetailImageFromResult = new GetDetailImageFromResult();
        getDetailImageFromResult.execute(editText);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);




    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;

        switch (mPage)
        {
            case 1:
                view = inflater.inflate(R.layout.fragment_page1, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_page2, container, false);
                break;
//            case 3:
//                view = inflater.inflate(R.layout.fragment_page3, container, false);
//                break;

            default:
                view = inflater.inflate(R.layout.fragment_page1, container, false);



        }

        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);












        return view;
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
        static final String ADDR1 = "addr1";
        static final String ADDR2 = "addr2";

        String totalCount = null;

        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {

            String title=null;
            String mapx=null;
            String mapy=null;
            String firstimage=null;
            String overview=null;
            String addr1=null;
            String addr2=null;

            JSONObject attractionJson = new JSONObject(forecastJsonStr);
            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            JSONObject itemObject = itemsObject.getJSONObject(ITEM);

            try {
                mapx = itemObject.getString(MAPX);
                mapy = itemObject.getString(MAPY);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG, " 좌표 없음 ");
            }

            try {
                firstimage = itemObject.getString(FIRSTIMAGE);
                url_maps.put("0", firstimage);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG, " 대표 이미지 없음");
            }

            try {
                overview = itemObject.getString(OVERVIEW);
                title = itemObject.getString(TITLE);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG, " 설명 또는 타이틀 없음 ");
            }

            try {
                addr1 =  itemObject.getString(ADDR1);
                addr2 =  itemObject.getString(ADDR2);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG," 주소 없음 ");
            }



            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.add(title);//0
            arrayList.add(overview);//1
            arrayList.add(mapx);//2
            arrayList.add(mapy);//3
            arrayList.add(addr1);//4
            arrayList.add(addr2);//5
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
            TextView textView = (TextView)getActivity().findViewById(R.id.detailtextView);


            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            /************************** mapx=string.get(2), mapy=string.get(3); ***************/
            String str = null;

            if(strings.get(0)!=null)
            {
                str = " 장소 : "+ strings.get(0)+" \r\n ";
            }


            if(strings.get(4)!=null)
            {
                if(strings.get(5)!=null)
                {

                    str +=" 주소 : "+ strings.get(4)+" "+ strings.get(5) +" \r\n ";

                }
                else
                    str += " 주소 : " + strings.get(4) +" \r\n ";
            }


            String editStr = strings.get(1).replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|&nbsp|&gt;","");
            str += editStr;
            textView.setText(str);

            super.onPostExecute(strings);
        }



    }


    public class GetDetailImageFromResult extends AsyncTask<String, Void, ArrayList<String>>implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

        private final String LOG_TAG = GetDetailFromResult.class.getSimpleName();

        static final String ORIGINALURL = "originimgurl";
        static final String smallimageurl = "smallimageurl";

        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        String totalCount = null;
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            Log.d("Slider Demo", "Page Changed: " + position);
        }
        @Override
        public void onSliderClick(BaseSliderView slider) {
            Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        }
        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {
            ArrayList<String> arrayList = new ArrayList<String>();
            String url=null;

            JSONObject attractionJson = new JSONObject(forecastJsonStr);
            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            totalCount = bodyObject.getString(TOTAL_COUNT);
            if(Integer.parseInt(totalCount)==1)
            {
                JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                JSONObject itemObject = itemsObject.getJSONObject(ITEM);

                arrayList.add(itemObject.getString(ORIGINALURL));
            }
            else
            {
                JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                JSONArray itemArray = itemsObject.getJSONArray(ITEM);

                int val = Integer.parseInt(totalCount);
                if( val >9)
                {
                    val = 9;
                }
                for(int i = 0 ; i<val;i++)
                {
                    JSONObject AttracionObject = itemArray.getJSONObject(i);
                    url = AttracionObject.getString(ORIGINALURL);
                    arrayList.add(url);
                    Log.v("6666",url);
                }
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
            String contentid= params[0];
            try {

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage?ServiceKey="+myKey+ "&contentId="+ contentid +"&imageYN=Y&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&_type=json");


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
            Log.v("66666",totalCount);


            try
            {



                int val = Integer.parseInt(totalCount);
                if( val >5)
                {
                    val = 5;
                }
                for(int i = 0 ;i<val;i++)
                {
                    url_maps.put(new Integer(i+1).toString(), strings.get(i));
                    Log.v("??",strings.get(i));
                }

                for(String name : url_maps.keySet()){
                    TextSliderView textSliderView = new TextSliderView(getActivity());
                    // initialize a SliderLayout
                    textSliderView
                            .image(url_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", name);

                    mDemoSlider.addSlider(textSliderView);
                }
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
                mDemoSlider.addOnPageChangeListener(this);

            }catch (Exception e)
            {

            }


        }



    }

}