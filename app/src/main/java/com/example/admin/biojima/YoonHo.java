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

/**
 * Created by seoyoonho on 2015-08-25.
 */


public class YoonHo {
    public static String name;

    public YoonHo(){
        updateWeather();
    }

    public void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute("59,125");
    }

    private double[] getRainproDataFromJson(String forecastJsonStr) throws JSONException {

        final String WD_RESPONSE = "response";
        final String WD_BODY = "body";
        final String WD_ITEMS = "items";
        final String WD_ITEM = "item";
        final String WD_CATEGORY = "category";
        final String WD_FCSTDATA = "fcstValue";

        JSONObject weatherData = new JSONObject(forecastJsonStr);
        JSONObject responseData = weatherData.getJSONObject(WD_RESPONSE);
        JSONObject bodyData = responseData.getJSONObject(WD_BODY);
        JSONObject itemsData = bodyData.getJSONObject(WD_ITEMS);
        JSONArray itemListData = itemsData.getJSONArray(WD_ITEM);

        for(int i = 0;i < itemListData.length(); i++){
            //Forceast Type and Value
            String fcstType;
            double fcstValue;

            JSONObject itemData = itemListData.getJSONObject(i);

            fcstType = itemData.getString(WD_CATEGORY);
            fcstValue = itemData.getDouble(WD_FCSTDATA);

            if(fcstType.equals("POP")){
                Log.d("ffff",fcstType + "," + fcstValue );
            }
        }

        return null;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            //get nx,ny from parameters
            String nx = strings[0].split(",")[0];
            String ny = strings[0].split(",")[1];

            //Example Data
            String Today = "20150901";
            String CurrentTime = "0200";

            //Return Type is json
            String JsonType = "json";

            try{
                //Construct the URL
                final String FORECAST_BASE_URL =
                        "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService/ForecastSpaceData?ServiceKey=Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";
                final String BASE_DATE_PARM = "base_date";
                final String BASE_TIME_PARM = "base_time";
                final String NX_PARM = "nx";
                final String NY_PARM = "ny";
                final String TYPE_PARM = "_type";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(BASE_DATE_PARM,Today)
                        .appendQueryParameter(BASE_TIME_PARM,CurrentTime)
                        .appendQueryParameter(NX_PARM,nx)
                        .appendQueryParameter(NY_PARM,ny)
                        .appendQueryParameter(TYPE_PARM,JsonType)
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

            }catch (IOException e){
                Log.e("ffff","error",e);
                return null;

            }finally {
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
            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String WeatherData){
            try{
                getRainproDataFromJson(WeatherData);
            }catch(JSONException e){
                Log.d("ffff","JSONEception");
            }
            //Log.d("ffff",WeatherData);
            name = WeatherData;
        }
    }
}
