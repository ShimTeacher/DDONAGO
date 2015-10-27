package com.example.admin.biojima;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    //사용자가 지정한 시간과 날짜
    static int ChooseTime;
    static int value;
    static String ChooseDate;
    int[] Finalrank ;
    static ArrayList<String> sigunguCodeArrList;
    String[] args;

    //시간과 날짜 디폴트값
    static{
        ChooseTime = 2;
        ChooseDate = "tomorrow";
    }

    public YoonHo(String[] args1){

        args = args1;
        updateWeather();

    }

    public void updateWeather(){

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(args);
    }

    //강수확률이 낮은 순으로 Sorting해주는 함수
    //강수확률 정렬 함수
    public int[] YoonHoPopSort(double[] arr){
        int[] rank = new int[arr.length];
        double[] copyarr = new double[arr.length];
        int[] sortData = new int[arr.length];
        int[] RealRankData = new int[arr.length];
        for (int i=0;i<arr.length;i++){
            rank[i] = i;
            sortData[i] = 1;
            copyarr[i] = arr[i];
        }



        for(int i=0; i<copyarr.length - 1; i++){

            for(int k = copyarr.length - 1;k>i;k--){
                if(copyarr[k] < copyarr[k-1]){
                    double swap = copyarr[k-1];
                    copyarr[k-1] = copyarr[k];
                    copyarr[k] = swap;

                    int rankSwap = rank[k-1];
                    rank[k-1] = rank[k];
                    rank[k] = rankSwap;
                }
            }
        }

        for(int i = 0;i<copyarr.length - 1;i++){
            if(copyarr[i] < copyarr[i+1]){
                sortData[i+1] = sortData[i] + 1;
            }
            else{
                sortData[i+1] = sortData[i];
            }
        }

        for(int i=0;i<copyarr.length;i++){
            RealRankData[rank[i]] = sortData[i];
        }



        return RealRankData;
    }

    //체감기온 정렬 함수
    public int[] YoonHoTempSort(double[] arr){

        double[] newTempArr = new double[arr.length];

        for(int i=0;i<arr.length;i++){
            newTempArr[i] = Math.abs(22-arr[i]);
        }

        return YoonHoPopSort(newTempArr);

    }

    //강수확률과 체감기온을 통합하여 정렬하는 함수
    public int[] FinalSort(int[] PopRank,int[] TempRank){

        int[] SortArr = new int[PopRank.length];

        //실제 순위를 담기 위한 데이터
        int[] rank = new int[PopRank.length];


        for(int i=0;i<PopRank.length;i++){
            SortArr[i] = PopRank[i] * 2 + TempRank[i];
        }

        //rank데이터 초기화
        for (int i=0;i<SortArr.length;i++){
            rank[i] = i;
        }


        //데이터가 6개 이상일 경우 상위 데이터 5개를 뽑아낸다
        if(rank.length > 5) {
            int[] returnrank = new int[5];
            for (int i = 0; i < 5; i++) {

                for (int k = SortArr.length - 1; k > i; k--) {
                    if (SortArr[k] < SortArr[k - 1]) {
                        int swap = SortArr[k - 1];
                        SortArr[k - 1] = SortArr[k];
                        SortArr[k] = swap;

                        int rankSwap = rank[k - 1];
                        rank[k - 1] = rank[k];
                        rank[k] = rankSwap;
                    }
                }
                returnrank[i] = rank[i];
            }
            return returnrank;
        }
        else{
            for (int i = 0; i < SortArr.length; i++) {

                for (int k = SortArr.length - 1; k > i; k--) {
                    if (SortArr[k] < SortArr[k - 1]) {
                        int swap = SortArr[k - 1];
                        SortArr[k - 1] = SortArr[k];
                        SortArr[k] = swap;

                        int rankSwap = rank[k - 1];
                        rank[k - 1] = rank[k];
                        rank[k] = rankSwap;
                    }
                }
            }
        }

        return rank;
    }

    //체감온도를 계산하는 함수
    public double CalSenTemp(double Temp,double wind){

        double newWind = wind * 3.6;

        double SenTemp;

        SenTemp = 13.12 + 0.6215 * Temp - 11.37 * Math.pow(newWind,0.15) + 0.3965 * Math.pow(newWind, 0.15) * Temp;

        return SenTemp;
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
            return "-1";
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
        final int CHOOSE_TIME;
        if(ChooseTime == 0){CHOOSE_TIME = MORNING;}
        else if(ChooseTime == 1){CHOOSE_TIME = AFTERNOON;}
        else if(ChooseTime == 2){CHOOSE_TIME = NIGHT;}
        else{CHOOSE_TIME = MID_NIGHT;}

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
        double todayPopData = 0,tomorrowPopData = 0,afterTomorrowPopData = 0;
        double todayT3hData = 0,tomorrowT3hData = 0,afterTomorrowT3hData = 0;
        double todayWsdData = 0,tomorrowWsdData = 0,afterTomorrowWsdData = 0;

        //Data's cnt
        double todayPopCnt = 0,tomorrowPopCnt = 0,afterTomorrowPopCnt = 0;
        double todayT3hCnt = 0,tomorrowT3hCnt = 0,afterTomorrowT3hCnt = 0;
        double todayWsdCnt = 0,tomorrowWsdCnt = 0,afterTomorrowWsdCnt = 0;


        for(int i = 0;i < itemListData.length(); i++){
            //Forecast Type and Value
            String fcstType,fcstTime,fcstDate;
            double fcstValue;

            JSONObject itemData = itemListData.getJSONObject(i);

            fcstType = itemData.getString(WD_CATEGORY);
            fcstTime = itemData.getString(WD_FCSTTIME);
            fcstDate = itemData.getString(WD_FCSTDATE);
            fcstValue = itemData.getDouble(WD_FCSTDATA);

            //강수확률 계산하는 부분
            if(fcstType.equals("POP")){

                //each case of time and each Code
                switch(CHOOSE_TIME) {
                    case MORNING:
                        if(isMorning(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayPopData += fcstValue;
                                todayPopCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowPopData += fcstValue;
                                tomorrowPopCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowPopData += fcstValue;
                                afterTomorrowPopCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case AFTERNOON:
                        if(isAfternoon(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayPopData += fcstValue;
                                todayPopCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowPopData += fcstValue;
                                tomorrowPopCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowPopData += fcstValue;
                                afterTomorrowPopCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case NIGHT:
                        if(isNight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayPopData += fcstValue;
                                todayPopCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowPopData += fcstValue;
                                tomorrowPopCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowPopData += fcstValue;
                                afterTomorrowPopCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case MID_NIGHT:
                        if(isMidnight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayPopData += fcstValue;
                                todayPopCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowPopData += fcstValue;
                                tomorrowPopCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowPopData += fcstValue;
                                afterTomorrowPopCnt++;
                            }
                            else{

                            }
                        }
                        break;
                }

            }

            //체감온도를 위한 온도 계산하는 부분
            if(fcstType.equals("T3H")) {
                //each case of time and each Code
                switch(CHOOSE_TIME) {
                    case MORNING:
                        if(isMorning(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayT3hData += fcstValue;
                                todayT3hCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowT3hData += fcstValue;
                                tomorrowT3hCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowT3hData += fcstValue;
                                afterTomorrowT3hCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case AFTERNOON:
                        if(isAfternoon(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayT3hData += fcstValue;
                                todayT3hCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowT3hData += fcstValue;
                                tomorrowT3hCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowT3hData += fcstValue;
                                afterTomorrowT3hCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case NIGHT:
                        if(isNight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayT3hData += fcstValue;
                                todayT3hCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowT3hData += fcstValue;
                                tomorrowT3hCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowT3hData += fcstValue;
                                afterTomorrowT3hCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case MID_NIGHT:
                        if(isMidnight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayT3hData += fcstValue;
                                todayT3hCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowT3hData += fcstValue;
                                tomorrowT3hCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowT3hData += fcstValue;
                                afterTomorrowT3hCnt++;
                            }
                            else{

                            }
                        }
                        break;
                }
            }


            //체감온도를 위한 풍속 계산하는 부분
            if(fcstType.equals("WSD")){
                //each case of time and each Code
                switch(CHOOSE_TIME) {
                    case MORNING:
                        if(isMorning(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayWsdData += fcstValue;
                                todayWsdCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowWsdData += fcstValue;
                                tomorrowWsdCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowWsdData += fcstValue;
                                afterTomorrowWsdCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case AFTERNOON:
                        if(isAfternoon(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayWsdData += fcstValue;
                                todayWsdCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowWsdData += fcstValue;
                                tomorrowWsdCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowWsdData += fcstValue;
                                afterTomorrowWsdCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case NIGHT:
                        if(isNight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayWsdData += fcstValue;
                                todayWsdCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowWsdData += fcstValue;
                                tomorrowWsdCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowWsdData += fcstValue;
                                afterTomorrowWsdCnt++;
                            }
                            else{

                            }
                        }
                        break;
                    case MID_NIGHT:
                        if(isMidnight(fcstTime)){
                            if(fcstDate.equals(today)){
                                todayWsdData += fcstValue;
                                todayWsdCnt ++;
                            }
                            else if(fcstDate.equals(tomorrow)){
                                tomorrowWsdData += fcstValue;
                                tomorrowWsdCnt++;
                            }
                            else if(fcstDate.equals(afterTomorrow)){
                                afterTomorrowWsdData += fcstValue;
                                afterTomorrowWsdCnt++;
                            }
                            else{

                            }
                        }
                        break;
                }

            }


        }

        String afterTomorrowPopDataString;
        String todayPopDataString;
        String tomorrowPopDataString;

        String afterTomorrowT3hDataString;
        String todayT3hDataString;
        String tomorrowT3hDataString;

        String afterTomorrowWsdDataString;
        String todayWsdDataString;
        String tomorrowWsdDataString;

        //강수확률 평균 구하는 부분
        if(todayPopCnt !=0){
            todayPopData = todayPopData/todayPopCnt;
            todayPopDataString = new Double(todayPopData).toString();
        }else{
            todayPopDataString ="no data";
        }

        if(tomorrowPopCnt != 0){
            tomorrowPopData = tomorrowPopData/tomorrowPopCnt;
            tomorrowPopDataString = new Double(tomorrowPopData).toString();
        }else{
            tomorrowPopDataString = "no data";
        }

        if(afterTomorrowPopCnt!=0){
            afterTomorrowPopData = afterTomorrowPopData/afterTomorrowPopCnt;
            afterTomorrowPopDataString = new Double(afterTomorrowPopData).toString();
        }else{
            afterTomorrowPopDataString = "no data";
        }

        //기온 평균 구하는 부분
        if(todayT3hCnt !=0){
            todayT3hData = todayT3hData/todayT3hCnt;
            todayT3hDataString = new Double(todayT3hData).toString();
        }else{
            todayT3hDataString ="no data";
        }

        if(tomorrowT3hCnt != 0){
            tomorrowT3hData = tomorrowT3hData/tomorrowT3hCnt;
            tomorrowT3hDataString = new Double(tomorrowT3hData).toString();
        }else{
            tomorrowT3hDataString = "no data";
        }

        if(afterTomorrowT3hCnt!=0){
            afterTomorrowT3hData = afterTomorrowT3hData/afterTomorrowT3hCnt;
            afterTomorrowT3hDataString = new Double(afterTomorrowT3hData).toString();
        }else{
            afterTomorrowT3hDataString = "no data";
        }

        //풍속 평균 구하는 부분
        if(todayWsdCnt !=0){
            todayWsdData = todayWsdData/todayWsdCnt;
            todayWsdDataString = new Double(todayWsdData).toString();
        }else{
            todayWsdDataString ="no data";
        }

        if(tomorrowWsdCnt != 0){
            tomorrowWsdData = tomorrowWsdData/tomorrowWsdCnt;
            tomorrowWsdDataString = new Double(tomorrowWsdData).toString();
        }else{
            tomorrowWsdDataString = "no data";
        }

        if(afterTomorrowWsdCnt!=0){
            afterTomorrowWsdData = afterTomorrowWsdData/afterTomorrowWsdCnt;
            afterTomorrowWsdDataString = new Double(afterTomorrowWsdData).toString();
        }else{
            afterTomorrowWsdDataString = "no data";
        }

        //체감기온 계산하는 부분
        if(!todayT3hDataString.equals("no data")){
            todayT3hDataString = new Double(CalSenTemp(todayT3hData,todayWsdData)).toString();
        }

        if(!tomorrowT3hDataString.equals("no data")){
            tomorrowT3hDataString = new Double(CalSenTemp(tomorrowT3hData, tomorrowWsdData)).toString();
        }

        if(!afterTomorrowT3hDataString.equals("no data")){
            afterTomorrowT3hDataString = new Double(CalSenTemp(afterTomorrowT3hData,afterTomorrowWsdData)).toString();
        }

        //게산된 강수확률을 배열에 담아 반환한다.
        String[] resultDataSet = new String[6];
        resultDataSet[0] = todayPopDataString;
        resultDataSet[1] = tomorrowPopDataString;
        resultDataSet[2] = afterTomorrowPopDataString;

        //계산된 체감기온을 배열에 담아 반환한다.
        resultDataSet[3] = todayT3hDataString;
        resultDataSet[4] = tomorrowT3hDataString;
        resultDataSet[5] = afterTomorrowT3hDataString;

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

            //if current time 00 ~ 02
            if(CurrentTime.equals("-1")){
                cal.add(Calendar.DATE,-1);
                Today = fm.format(cal.getTime());
                CurrentTime = "2300";

            }

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

                String serviceKey1 ="JAqSoGioZENbhApPM6hgbP5nxxdEg%2FKgtSy%2BK%2BcyFR7Ckk%2Fav13Hoh4tzckekFe60m82sHoUMCwJ1Hzp1GPWGA%3D%3D";
                String serviceKey2 ="Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";

                try {

                    final String FORECAST_BASE_URL =
                            "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService/ForecastSpaceData?ServiceKey="+serviceKey1;
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


            double[] PopDataArr = new double[WeatherDataList.length];
            double[] TempDataArr = new double[WeatherDataList.length];

            try{
                for(int i=0;i<WeatherDataList.length;i++) {
                    String[] a = getRainproDataFromJson(WeatherDataList[i]);
                    Log.d("ffff", a[0]);
                    Log.d("ffff", a[1]);
                    Log.d("ffff", a[2]);
                    Log.d("ffff","-----------");

                    Log.d("ffff", a[3]);
                    Log.d("ffff", a[4]);
                    Log.d("ffff", a[5]);
                    Log.d("ffff","===========");


                    try {
                        if (ChooseDate.equals("today")) {
                            PopDataArr[i] = new Double(a[0]);
                            TempDataArr[i] = new Double(a[3]);
                        } else if (ChooseDate.equals("tomorrow")) {
                            PopDataArr[i] = new Double(a[1]);
                            TempDataArr[i] = new Double(a[4]);
                        } else {
                            PopDataArr[i] = new Double(a[2]);
                            TempDataArr[i] = new Double(a[5]);
                        }
                    }catch (Exception e)
                    {

                    }
                }

//                for(int i=0;i<WeatherDataList.length;i++){
//                    Log.d("testData",new Double(DataArr[i]).toString());
//                }

                Finalrank = FinalSort(YoonHoPopSort(PopDataArr), YoonHoTempSort(TempDataArr));

                for(int i=0;i<Finalrank.length;i++){
                    Log.d("rank",new Integer(Finalrank[i]).toString());
                }

                for(int i=0;i<Finalrank.length;i++){
                    Log.d("Pop",new Double(PopDataArr[Finalrank[i]]).toString());
                }

                for(int i=0;i<Finalrank.length;i++){

                    Log.d("Temp",new Double(TempDataArr[Finalrank[i]]).toString());
                }

            }catch(JSONException e){
                Log.d("ffff","JSONEception");
            }catch(ParseException e){
                Log.d("ffff","ParseExecption");
            }


            if (WeatherDataList != null) {
                ResultActivity.m_adapter.clear();


                ArrayList<String> arrayList = new ArrayList<String>();
                sigunguCodeArrList = new ArrayList<String>();



                try {
                    for (int i = 0; i < Finalrank.length; i++) {
                        /************************ 진짜 코드**********************/
                        arrayList.add(Hyunbo.sigunguName[Finalrank[i]]);
                        sigunguCodeArrList.add(Hyunbo.sigunguList[Finalrank[i]]);
                        /**************************        ********************/
                    }

                    ResultActivity.m_orders.clear();

                    for(int i =0; i<arrayList.size() ;i++) {

                        String str = new Double(Math.round(new Double(TempDataArr[Finalrank[i]]))).toString();
                        String pop = new Double(new Double(PopDataArr[Finalrank[i]])).toString();
                        ResultData resultData = new ResultData(arrayList.get(i),pop,str);
                        ResultActivity.m_orders.add(resultData);

//                    ResultActivity.mlistAdapter.add(a);
                    }

                }catch (Exception e)
                {

                    int i=0;
                    while(i<Hyunbo.sigunguName.length)
                    {
                        arrayList.add(Hyunbo.sigunguName[i]);
                        sigunguCodeArrList.add(Hyunbo.sigunguList[i]);
                        i++;
                    }
                    ResultActivity.m_orders.clear();

                    for(int k=0; k<Hyunbo.sigunguName.length; k++)
                    {

                        String str = "온도 정보 없음.";
                        String pop = "강수량 정보 없음";
                        ResultData resultData = new ResultData(arrayList.get(k),pop,str);
                        ResultActivity.m_orders.add(resultData);

//                    ResultActivity.mlistAdapter.add(a);
                    }

                    Log.v("jssssssssson", "결과 가짜정보 출력2");


                }


            }



            ResultActivity.progressDialog.dismiss();

            String str = ResultActivity.editTexts.replace("0","");

            if(ResultActivity.siteTexts.compareTo("nodata")==0)
                Toast.makeText(ResultActivity.context, ResultActivity.ChooseDate + ", " + ResultActivity.ChooseTime + ", " + str + " 주변\r\n " + ResultActivity.value + "m 이내의 날씨 좋은 지역입니다.", Toast.LENGTH_LONG).show();
            else
            {
                Toast.makeText(ResultActivity.context,ResultActivity.ChooseDate+", "+ResultActivity.ChooseTime+", "+ResultActivity.siteTexts+" 주변\r\n "+ResultActivity.value+"m 이내의 날씨 날씨 좋은 지역입니다.",Toast.LENGTH_LONG).show();
            }
        }
    }
}
