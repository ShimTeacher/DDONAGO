package com.example.admin.biojima;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

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



    NMapView mMapView;
    private NMapViewerResourceProvider mMapViewerResourceProvider;


    int Is12=0;
    int Is14=0;
    int Is15=0;



    String infocenter= null;
    String usetime= null;
    String parking= null;
    String restdate= null;

    String infocenterculture= null;
    String usetimeculture= null;
    String parkingculture= null;
    String restdateculture= null;
    String spendtimeculture = null;

     String eventstartdate = null;
     String eventenddate = null;
     String eventplace = null;
     String usetimefestival= null;
     String spendtimefestival= null;
     String bookingplace = null;
    String agelimit = null;
    String placeinfo = null;


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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String site = prefs.getString(getString(R.string.search_criteria_key),
                getString(R.string.search_criteria_attraction));

        if(site.compareTo("12")==0)
        {
            GetDetailIntroFromResult_12 getDetailIntroFromResult12 = new GetDetailIntroFromResult_12();
            getDetailIntroFromResult12.execute(editText);
            Is12=1;
            Is14=0;
            Is15=0;
        }
        else if(site.compareTo("14")==0)
        {
            GetDetailIntroFromResult_14 getDetailIntroFromResult14 = new GetDetailIntroFromResult_14();
            getDetailIntroFromResult14.execute(editText);
            Is12=0;
            Is14=1;
            Is15=0;
        }
        else if(site.compareTo("15")==0)
        {
            GetDetailIntroFromResult_15 getDetailIntroFromResult15 = new GetDetailIntroFromResult_15();
            getDetailIntroFromResult15.execute(editText);
            Is12=0;
            Is14=0;
            Is15=1;
        }
        Log.v("1222","???");



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
        View view2 = null;


        switch (mPage)
        {
            case 1:
                view = inflater.inflate(R.layout.fragment_page1, container, false);
                break;
            case 2:
                view2 = inflater.inflate(R.layout.fragment_page2, container, false);
                Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();


                break;

            default:
                view = inflater.inflate(R.layout.fragment_page1, container, false);

        }

//
//
//        mMapView = new NMapView(view.getContext());
//
//        // set a registered API key for Open MapViewer Library
//        mMapView.setApiKey("04edde0f95d089e814106c10960aca70");
//
//        // set the activity content to the map view
//       setContentView(mMapView);
//

        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);



        return view;
    }







    public class GetDetailFromResult extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        private final String LOG_TAG = GetDetailFromResult.class.getSimpleName();


        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        static final String MAPX = "mapx";
        static final String MAPY = "mapy";
        static final String TITLE = "title";
        static final String FIRSTIMAGE = "firstimage";
        static final String OVERVIEW = "overview";
        static final String ADDR1 = "addr1";
        static final String ADDR2 = "addr2";

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
                str = "\r\n  장소 : "+ strings.get(0)+" \r\n ";
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








            if(Is12==1) {

                if (infocenter != null) {
                    infocenter = infocenter.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 문의 및 안내 : " + infocenter + " \r\n ";
                }

                if (usetime != null) {
                    usetime = usetime.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용시간 : " + usetime + " \r\n ";
                }

                if (parking != null) {
                    parking = parking.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 주차시설 유무 : " + parking + " \r\n ";
                }

                if (restdate != null) {
                    restdate = restdate.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 쉬는 날 : " + restdate + " \r\n ";
                }


            }

            else if(Is14 == 1)
            {
                if (infocenterculture != null) {
                    infocenterculture = infocenterculture.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 문의 및 안내 : " + infocenterculture + " \r\n ";
                }

                if (usetimeculture != null) {
                    usetimeculture = usetimeculture.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용시간 : " + usetimeculture + " \r\n ";
                }

                if (parkingculture != null) {
                    parkingculture = parkingculture.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 주차시설 유무 : " + parkingculture + " \r\n ";
                }

                if (restdateculture != null) {
                    restdateculture = restdateculture.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 쉬는 날 : " + restdateculture + " \r\n ";
                }
                if (spendtimeculture != null) {
                    spendtimeculture = spendtimeculture.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용시간 : " + spendtimeculture + " \r\n ";
                }

            }
            else if(Is15 == 1)
            {
                if (eventplace != null) {
                    eventplace = eventplace.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이벤트 장소 : " + eventplace + " \r\n ";
                }
                if (eventstartdate != null) {
                    eventstartdate = eventstartdate.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이벤트 시작일 : " + eventstartdate + " \r\n ";
                }

                if (eventenddate != null) {
                    eventenddate = eventenddate.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이벤트 종료일 : " + eventenddate + " \r\n ";
                }
                if (placeinfo != null) {
                    placeinfo = placeinfo.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용정보 : " + placeinfo + " \r\n ";
                }


                if (usetimefestival != null) {
                    usetimefestival = usetimefestival.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용금액 : " + usetimefestival + " \r\n ";
                }
                if (spendtimefestival != null) {
                    spendtimefestival = spendtimefestival.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 이용시간 : " + spendtimefestival + " \r\n ";
                }
                if (agelimit != null) {
                    agelimit = agelimit.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 관람가능 연령대 : " + agelimit + " \r\n ";
                }
                if (bookingplace != null) {
                    bookingplace = bookingplace.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|br|&nbsp|&gt;", "");
                    str += " 예매처 : " + bookingplace + " \r\n ";
                }



            }


            str+="\r\n";

            String editStr = strings.get(1).replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("&lt;|&nbsp|&gt;","\r\n");
            str += editStr;
            textView.setText(str);

            super.onPostExecute(strings);
        }



    }




    public class GetDetailIntroFromResult_12 extends AsyncTask<String, Void, ArrayList<String>>{
        private final String LOG_TAG = GetDetailIntroFromResult_12.class.getSimpleName();
        static final String INFOCENTER = "infocenter";
        static final String USETIME = "usetime";
        static final String PARKING = "parking";
        static final String RESTDATE = "restdate";

        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {
            ArrayList<String> arrayList = new ArrayList<String>();



            JSONObject attractionJson = new JSONObject(forecastJsonStr);

            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            JSONObject itemObject = itemsObject.getJSONObject(ITEM);



            try
            {
                infocenter =  itemObject.getString(INFOCENTER);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"문의 및 정보 없음");
            }
            try
            {
                usetime =  itemObject.getString(USETIME);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이용시간 정보 없음");
            }
            try
            {
                parking =  itemObject.getString(PARKING);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"주차시설 정보 없음");
            }
            try
            {
                restdate =  itemObject.getString(RESTDATE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG, "쉬는 날 정보 없음");
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

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="+myKey+ "&contentId="+ contentid +"&contentTypeId=12&imageYN=Y&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&_type=json");


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
    }






    public class GetDetailIntroFromResult_14 extends AsyncTask<String, Void, ArrayList<String>>{
        private final String LOG_TAG = GetDetailIntroFromResult_12.class.getSimpleName();
        static final String INFOCENTERCULTURE = "infocenterculture";
        static final String USETIMECULTURE = "usetimeculture";
        static final String PARKINGCULTURE = "parkingculture";
        static final String RESTDATECULTURE = "restdateculture";
        static final String SPENDTIME = "spendtime";

        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        String totalCount = null;


        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {
            ArrayList<String> arrayList = new ArrayList<String>();



            JSONObject attractionJson = new JSONObject(forecastJsonStr);

            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            JSONObject itemObject = itemsObject.getJSONObject(ITEM);



            try
            {
                infocenterculture =  itemObject.getString(INFOCENTERCULTURE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"문의 및 정보 없음");
            }
            try
            {
                usetimeculture =  itemObject.getString(USETIMECULTURE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이용시간 정보 없음");
            }
            try
            {
                parkingculture =  itemObject.getString(PARKINGCULTURE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"주차시설 정보 없음");
            }
            try
            {
                restdateculture =  itemObject.getString(RESTDATECULTURE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"쉬는 날 정보 없음");
            }
            try
            {
                spendtimeculture =  itemObject.getString(SPENDTIME);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"관람 소요시간 정보 없음");
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

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="+myKey+ "&contentId="+ contentid +"&contentTypeId=14&imageYN=Y&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&_type=json");


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

    }



    public class GetDetailIntroFromResult_15 extends AsyncTask<String, Void, ArrayList<String>>{
        private final String LOG_TAG = GetDetailIntroFromResult_12.class.getSimpleName();
        static final String EVENTSTARTDATE = "eventstartdate";
        static final String EVENTENDDATE = "eventenddate";
        static final String EVENTPLACE = "eventplace";
        static final String USEFEEFESTIVAL = "usetimefestival";
        static final String SPENDTIMEFESTIVAL = "spendtimefestival";
        static final String BOOKINGPLACE = "bookingplace";
        static final String AGELIMIT = "agelimit";
        static final String PLACEINFO = "placeinfo";

        static final String RESPONSE = "response";
        static final String BODY = "body";
        static final String TOTAL_COUNT = "totalCount";
        static final String ITEMS = "items";
        static final String ITEM = "item";

        String totalCount = null;


        private ArrayList<String> getAttractionDataFromJson(String forecastJsonStr)
                throws JSONException {
            ArrayList<String> arrayList = new ArrayList<String>();



            JSONObject attractionJson = new JSONObject(forecastJsonStr);

            JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
            JSONObject bodyObject = responseObject.getJSONObject(BODY);
            JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
            JSONObject itemObject = itemsObject.getJSONObject(ITEM);



            try
            {
                eventstartdate =  itemObject.getString(EVENTSTARTDATE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이벤트 시작 정보 없음");
            }
            try
            {
                eventenddate =  itemObject.getString(EVENTENDDATE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이벤트 종료 정보 없음");
            }
            try
            {
                eventplace =  itemObject.getString(EVENTPLACE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이벤트 장소 정보 없음");
            }
            try
            {
                usetimefestival =  itemObject.getString(USEFEEFESTIVAL);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이벤트 가격 정보 없음");
            }
            try
            {
                spendtimefestival =  itemObject.getString(SPENDTIMEFESTIVAL);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"이벤트 시간정보 없음");
            }
            try
            {
                bookingplace =  itemObject.getString(BOOKINGPLACE);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"예약처정보 없음");
            }
            try
            {
                agelimit =  itemObject.getString(AGELIMIT);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"연령대 정보 없음");
            }
            try
            {
                placeinfo =  itemObject.getString(PLACEINFO);
            }catch (JSONException e)
            {
                e.printStackTrace();
                Log.v(LOG_TAG,"연령대 정보 없음");
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

                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="+myKey+ "&contentId="+ contentid +"&contentTypeId=15&imageYN=Y&MobileOS=ETC&MobileApp=TourAPI2.0_Guide&_type=json");


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
            Log.v("66666", totalCount);


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