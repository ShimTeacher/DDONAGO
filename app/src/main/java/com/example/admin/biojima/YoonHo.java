package com.example.admin.biojima;

import android.net.Uri;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by seoyoonho on 2015-08-25.
 */


public class YoonHo {

    //each case of time
    static final int MORNING = 0;
    static final int AFTERNOON = 1;
    static final int NIGHT= 2 ;
    static final int MID_NIGHT = 3;

    public YoonHo(){
        updateWeather();
    }

    public void updateWeather(){
        String[] a = new String[2];
        a[0] = "61,129";
        a[1] = "89,91";
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(a);
    }

    private boolean isMorning(String time){
        if(time.equals("0600") || time.equals("0900")){
            return true;
        }
        return false;
    }

    private boolean isAfternoon(String time){
        if(time.equals("1200") || time.equals("1500")){
            return true;
        }
        return false;
    }

    private boolean isMidnight(String time){
        if(time.equals("0000") || time.equals("0300")){
            return true;
        }
        return false;
    }

    private boolean isNight(String time){
        if(time.equals("1800") || time.equals("2100")){
            return true;
        }
        return false;
    }

    private String announceTime(String CurrentTime){
        Integer CurTimeInt = new Integer(CurrentTime);

        if(CurTimeInt < 200){
            return "0000";
        }

        if(CurTimeInt < 500){
            return "0200";
        }

        if(CurTimeInt < 800){
            return "0500";
        }

        if(CurTimeInt < 1100){
            return "0800";
        }

        if(CurTimeInt < 1400){
            return "1100";
        }

        if(CurTimeInt < 1700){
            return "1400";
        }

        if(CurTimeInt < 2000){
            return "1700";
        }

        if(CurTimeInt < 2300){
            return "2000";
        }

        return "2300";
    }


    private String[] getRainproDataFromJson(String forecastJsonStr) throws JSONException, ParseException {

        final String WD_RESPONSE = "response";
        final String WD_BODY = "body";
        final String WD_ITEMS = "items";
        final String WD_ITEM = "item";
        final String WD_CATEGORY = "category";
        final String WD_FCSTDATA = "fcstValue";
        final String WD_BASEDATE = "baseDate";
        final String WD_BASETIME = "baseTime";
        final String WD_FCSTDATE = "fcstDate";
        final String WD_FCSTTIME = "fcstTime";

        JSONObject weatherData = new JSONObject(forecastJsonStr);
        JSONObject responseData = weatherData.getJSONObject(WD_RESPONSE);
        JSONObject bodyData = responseData.getJSONObject(WD_BODY);
        JSONObject itemsData = bodyData.getJSONObject(WD_ITEMS);
        JSONArray itemListData = itemsData.getJSONArray(WD_ITEM);

        //choice time of example
        final int CHOOSE_TIME = MID_NIGHT;

        //Date calculate
        String baseDate = itemListData.getJSONObject(0).getString(WD_BASEDATE);
        String baseTime = itemListData.getJSONObject(0).getString(WD_BASETIME);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date todayDate = formatter.parse(baseDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);

        String today = formatter.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        String tomorrow = formatter.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        String afterTomorrow = formatter.format(calendar.getTime());

        //Data's name
        double todayData = 0,tomorrowData = 0,afterTomorrowData = 0;

        //Data's cnt
        double todayCnt = 0,tomorrowCnt = 0,afterTomorrowCnt = 0;



        for(int i = 0;i < itemListData.length(); i++){
            //Forecast Type and Value
            String fcstType,fcstTime,fcstDate;
            double fcstValue;

            JSONObject itemData = itemListData.getJSONObject(i);

            fcstType = itemData.getString(WD_CATEGORY);
            fcstTime = itemData.getString(WD_FCSTTIME);
            fcstDate = itemData.getString(WD_FCSTDATE);
            fcstValue = itemData.getDouble(WD_FCSTDATA);

            if(fcstType.equals("POP")){

                //each case of time and each Code
                switch(CHOOSE_TIME) {
                    case MORNING:
                        if(isMorning(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayData += fcstValue;
                                todayCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowData += fcstValue;
                                tomorrowCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowData += fcstValue;
                                afterTomorrowCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case AFTERNOON:
                        if(isAfternoon(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayData += fcstValue;
                                todayCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowData += fcstValue;
                                tomorrowCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowData += fcstValue;
                                afterTomorrowCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case NIGHT:
                        if(isNight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayData += fcstValue;
                                todayCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowData += fcstValue;
                                tomorrowCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowData += fcstValue;
                                afterTomorrowCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case MID_NIGHT:
                        if(isMidnight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayData += fcstValue;
                                todayCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowData += fcstValue;
                                tomorrowCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowData += fcstValue;
                                afterTomorrowCnt++;
                            }
                            else{

                            }
                        }
                        break;
                }

            }
        }

        String afterTomorrowDataString;
        String todayDataString;
        String tomorrowDataString;

        if(todayCnt !=0){
            todayData = todayData/todayCnt;
            todayDataString = new Double(todayData).toString();
        }else{
            todayDataString ="no data";
        }

        if(tomorrowCnt != 0){
            tomorrowData = tomorrowData/tomorrowCnt;
            tomorrowDataString = new Double(tomorrowData).toString();
        }else{
            tomorrowDataString = "no data";
        }

        if(afterTomorrowCnt!=0){
            afterTomorrowData = afterTomorrowData/afterTomorrowCnt;
            afterTomorrowDataString = new Double(afterTomorrowData).toString();
        }else{
            afterTomorrowDataString = "no data";
        }



        String[] resultDataSet = new String[3];
        resultDataSet[0] = todayDataString;
        resultDataSet[1] = tomorrowDataString;
        resultDataSet[2] = afterTomorrowDataString;

        return resultDataSet;
    }

    public class FetchWeatherTask extends AsyncTask<String[],Void,String[]> {

        @Override
        protected String[] doInBackground(String[]... strings) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            //CurrentTime
            Calendar cal = new GregorianCalendar(Locale.KOREA);
            cal.setTime(new Date());

            SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat fm2 = new SimpleDateFormat("HHmm");
            String Today = fm.format(cal.getTime());
            String Hour = fm2.format(cal.getTime());

            //Announce Time
            String CurrentTime = announceTime(Hour);

            //Return Type is json
            String JsonType = "json";

            //number Of Rows
            String numberOfRows = "300";

            //resultJsonStringList
            String[] resultJsonList = new String[strings[0].length];

            for(int i=0;i<strings[0].length;i++) {
                //get nx,ny from parameters
                String nx = strings[0][i].split(",")[0];
                String ny = strings[0][i].split(",")[1];

                try {
                    //Construct the URL
                    final String FORECAST_BASE_URL =
                            "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService/ForecastSpaceData?ServiceKey=Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";
                    final String BASE_DATE_PARM = "base_date";
                    final String BASE_TIME_PARM = "base_time";
                    final String NX_PARM = "nx";
                    final String NY_PARM = "ny";
                    final String TYPE_PARM = "_type";
                    final String NUM_OF_LOWS = "numOfRows";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(BASE_DATE_PARM, Today)
                            .appendQueryParameter(BASE_TIME_PARM, CurrentTime)
                            .appendQueryParameter(NX_PARM, nx)
                            .appendQueryParameter(NY_PARM, ny)
                            .appendQueryParameter(TYPE_PARM, JsonType)
                            .appendQueryParameter(NUM_OF_LOWS, numberOfRows)
                            .build();

                    URL url = new URL(builtUri.toString());

                    //Create the request
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
                    forecastJsonStr = buffer.toString();

                } catch (IOException e) {
                    Log.e("ffff", "error", e);
                    return null;

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("ffff", "Error closing stream", e);
                        }
                    }
                }
                resultJsonList[i] = forecastJsonStr;

            }

            return resultJsonList;
        }

        @Override
        protected void onPostExecute(String[] WeatherDataList){
            try{
                for(int i=0;i<WeatherDataList.length;i++) {
                    String[] a = getRainproDataFromJson(WeatherDataList[i]);
                    Log.d("ffff", a[0]);
                    Log.d("ffff", a[1]);
                    Log.d("ffff", a[2]);
                }
            }catch(JSONException e){
                Log.d("ffff","JSONEception");
            }catch(ParseException e){
                Log.d("ffff","ParseExecption");
            }
        }
    }
}
