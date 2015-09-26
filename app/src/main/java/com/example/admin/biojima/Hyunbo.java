package com.example.admin.biojima;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by adslbna2 on 15. 8. 24..
 */
public class Hyunbo {

    static String lat ;
    static String lon ;
    String[] sigunguList = null;

    Hyunbo()
    {

        FetchAttractionTask fetchAttractionTask = new FetchAttractionTask();
        fetchAttractionTask.execute();

    }
    Hyunbo(String[] str) //str 위도경도
    {
        FetchAttractionTask fetchAttractionTask = new FetchAttractionTask();
        fetchAttractionTask.execute(str);
    }


        public class FetchAttractionTask extends AsyncTask<String[], Void, String[]> {

            HashMap<String , String[]> map = new HashMap<String , String[]>();
            HashMap<String , String[]> map2 = new HashMap<String , String[]>();

            String totalCount;
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
                final String ADDR = "addr1";
                final String AREACODE = "areacode";
                final String SIGUNGUCODE = "sigungucode";

                ArrayList<String> arrayList = new ArrayList<String>();
                Boolean[] checked = null;
                String[] List = null;

                String mapx;
                String mapy;
                String addr;
                String areacode;
                String sigungucode;

                JSONObject attractionJson = new JSONObject(forecastJsonStr);
                JSONObject responseObject = attractionJson.getJSONObject(RESPONSE);
                JSONObject bodyObject = responseObject.getJSONObject(BODY);
                totalCount = bodyObject.getString(TOTAL_COUNT);

                if (Integer.parseInt(totalCount) == 0) {
                    return null;
                } else if (Integer.parseInt(totalCount) == 1)
                {
                    String[] test;
                    JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                    JSONObject itemObject = itemsObject.getJSONObject(ITEM);
                    try {
                        mapx = itemObject.getString("mapx");
                        mapy = itemObject.getString("mapy");
                        addr = itemObject.getString(ADDR);
                        areacode = itemObject.getString(AREACODE);
                        sigungucode = itemObject.getString(SIGUNGUCODE);
                        String[] locationSet = {mapx,mapy};
                        String[] sigunCode = {areacode, sigungucode};

                        test= addr.split(" ");
                        if (test.length > 1) {
                            map.put(test[1], locationSet);
                            map2.put(test[1], sigunCode);

                        }


                        Set<Entry<String, String[]>> set = map.entrySet();
                        Set<Entry<String, String[]>> set2 = map2.entrySet();
                        Iterator<Entry<String, String[]>> it = set.iterator();
                        Iterator<Entry<String, String[]>> it2 = set2.iterator();
                        List = new String[set.size()];
                        sigunguList= new String[set2.size()];

                        int i = 0;
                        while (it.hasNext()) {
                            Map.Entry<String, String[]> k = (Map.Entry<String, String[]>)it.next();
                            Map.Entry<String, String[]> k2 = (Map.Entry<String, String[]>)it2.next();
                            List[i] = k.getValue()[0]+","+k.getValue()[1];
                            sigunguList[i] = k2.getValue()[0]+","+k2.getValue()[1];
                            i++;
                        }

                    }
                    catch (JSONException e)
                    {
                        Log.v("JSON!!!","관광지가 1개인데 ...?");
                    }


                    return List;
                } else
                {
                    JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                    JSONArray itemArray = itemsObject.getJSONArray(ITEM);
                    int val = Integer.parseInt(totalCount);
                    String[] test;
                    for (int i = 0; i < val; i++) {

                        JSONObject AttracionObject = itemArray.getJSONObject(i);
                        try{
                            mapx = AttracionObject.getString("mapx");
                            mapy = AttracionObject.getString("mapy");
                            addr = AttracionObject.getString(ADDR);
                            areacode = AttracionObject.getString(AREACODE);
                            sigungucode = AttracionObject.getString(SIGUNGUCODE);
                        }
                        catch (JSONException e)
                        {
                            break;
                        }

                        String[] locationSet = {mapx, mapy};// mapx = 127~~~~~~~~~ , mapy = 37~~~~~~~~~~~~~~
                        String[] sigunCode = {areacode, sigungucode};
                        test = addr.split(" ");

                        if (test.length > 1) {
                            map.put(test[1], locationSet);
                            map2.put(test[1], sigunCode);
                        }
                    }

                    Set<Entry<String, String[]>> set = map.entrySet();
                    Set<Entry<String, String[]>> set2 = map2.entrySet();
                    Iterator<Entry<String, String[]>> it = set.iterator();
                    Iterator<Entry<String, String[]>> it2 = set2.iterator();
                    List = new String[set.size()];
                    sigunguList = new String[set2.size()];
                    int i = 0;
                    while (it.hasNext()) {
                        Map.Entry<String, String[]> k = (Map.Entry<String, String[]>)it.next();
                        Map.Entry<String, String[]> k2 = (Map.Entry<String, String[]>)it2.next();
                        List[i] = k.getValue()[0]+","+k.getValue()[1];
                        sigunguList[i] = k2.getValue()[0]+","+k2.getValue()[1];
                        i++;
                    }
                    return List;
                }
            }

            @Override
            protected String[] doInBackground(String[]... params) {
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
                    String x;
                    String y;
                    String id = null;
                    String radious;
                    x = params[0][1];
                    y = params[0][0];

                    radious = params[0][2];

                    id = params[0][3];



                    URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey="
                            +myKey+"&contentTypeId="+ id +"&mapX="
                            +x+"&mapY="
                            +y+"&radius="
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

            @Override
            protected void onPostExecute(String[] strings) {

                if(Integer.parseInt(totalCount)==0)
                {
                    Log.v("ffff"," total count = 0 관광지 정보가 없음");
                }
                else
                {
                    try {
                        String[] AttrStr = new String[strings.length];
                        int i = 0;

                        Log.v("관광지 정보", totalCount + "개의 관광지가 검색됨");
                        for (String str : strings)
                            Log.v("관광지 정보", str);

                        for (String str : strings) {
                            Double lon = new Double(str.split(",")[0]);
                            Double lat = new Double(str.split(",")[1]);

                            AttrStr[i] = Change.changeLonLat(lon, lat);
                            i++;
                        }
                        YoonHo a = new YoonHo(AttrStr);
                    }
                    catch (Exception e)
                    {
                        Log.v("에러러러러", totalCount + "개의 관광지가 검색됨");
                    }
                }
            }
        }
}
