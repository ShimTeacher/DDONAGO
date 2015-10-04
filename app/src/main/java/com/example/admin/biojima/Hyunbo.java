package com.example.admin.biojima;


import android.app.ProgressDialog;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by adslbna2 on 15. 8. 24..
 */
public class Hyunbo {

    static String[] sigunguList = null;

    Hyunbo(String[] str) //str 위도경도
    {
        FetchAttractionTask fetchAttractionTask = new FetchAttractionTask();
        fetchAttractionTask.execute(str);
     }


       public class FetchAttractionTask extends AsyncTask<String[], Void, String[]> {

           @Override
           protected void onPreExecute() {

               super.onPreExecute();
           }

           ProgressDialog asyncDialog;

           private final String LOG_TAG = FetchAttractionTask.class.getSimpleName();

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

           String radious =null;
           String totalCount = null;

           HashMap<String , String[]> map = new HashMap<String , String[]>();
           HashMap<String , String[]> map2 = new HashMap<String , String[]>();


            private String[] getAttractionDataFromJson(String forecastJsonStr)
                    throws JSONException {

                String[] List = null; // 최종적으로 윤호에게 넘기게 될 데이터가 들어갈 String배열

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
                    Log.v(LOG_TAG,"관광지 정보가 0개 입니다. JSON 쿼리를 다시 확인.");
                    return null;
                } else
                {
                    JSONObject itemsObject = bodyObject.getJSONObject(ITEMS);
                    JSONArray itemArray = itemsObject.getJSONArray(ITEM);
                    int val = Integer.parseInt(totalCount);
                    String[] test;
                    String title;
                    for (int i = 0; i < val; i++) {
                        JSONObject AttracionObject = itemArray.getJSONObject(i);
                        try{
                            mapx = AttracionObject.getString(MAPX);
                            mapy = AttracionObject.getString(MAPY);
                            addr = AttracionObject.getString(ADDR);
                            title = AttracionObject.getString(TITLE);
                            areacode = AttracionObject.getString(AREACODE);
                            sigungucode = AttracionObject.getString(SIGUNGUCODE);
                        }
                        catch (JSONException e)
                        {
                            Log.v(LOG_TAG,"json exception");
                            break;
                        }

                        String[] locationSet = {mapx, mapy};// mapx = 127~~~~~~~~~ , mapy = 37~~~~~~~~~~~~~~
                        String[] sigunCode = {areacode, sigungucode, title};
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
                        sigunguList[i] = k2.getValue()[0]+","+k2.getValue()[1]+ "," + k2.getValue()[2];
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

                    String x= null;;
                    String y= null;;
                    String id = null;

                    x = params[0][1];
                    y = params[0][0];
                    radious = params[0][2];
                    id = params[0][3];

                    URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey="
                            +myKey+"&contentTypeId="+ id +"&mapX="
                            +x+"&mapY="
                            +y+"&radius="
                            +radious+"&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=1000&pageNo=1&_type=json");

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
            protected void onPostExecute(String[] strings) {


                if(Integer.parseInt(totalCount)==0)
                {
                    Log.v("checkValue"," total count = 0 정보가 없음");
                }
                else
                {
                    try {

                        String[] AttrStr = new String[strings.length];
                        int i = 0;
                        for (String str : strings) {
                            Double lon = new Double(str.split(",")[0]);
                            Double lat = new Double(str.split(",")[1]);
                            AttrStr[i] = Change.changeLonLat(lon, lat);
                            i++;
                        }


                        /****************** test code ******************/
                        /****************** test code ******************/
                        /****************** test code ******************/


                        Log.v("checkValue", totalCount + "개의 정보가 검색됨");
                        Log.v("checkValue", radious +"m 반경에서 중복 지역을 제외한 "+strings.length+ "개의 지역만 검색");
                        for (int j = 0; j < sigunguList.length; j++)
                        {
                            Log.v("check", sigunguList[j].toString()+"//"+  strings[j].toString());
                        }

                        /****************** test code ******************/
                        /****************** test code ******************/
                        /****************** test code ******************/

                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */

                        YoonHo a = new YoonHo(AttrStr);

                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */
                        /* 윤호 코드 생성자 삽입 부분 */


                    }
                    catch (Exception e)
                    {
                        Log.v(LOG_TAG, "ERROR??");
                    }


                    if (strings != null) {
                        ResultActivity.mlistAdapter.clear();
                        for(String a : sigunguList) {
                            ResultActivity.mlistAdapter.add(a);
                        }

                        // New data is back from the server.  Hooray!
                    }

                }


            }
        }
}
