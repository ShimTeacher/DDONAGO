package com.example.admin.biojima;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by adslbna2 on 15. 8. 24..
 */
public class Hyunbo extends Activity{





    //h
    static final String myKey = "Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D";
    static ArrayList<String> attraction;
    static Double X = 127.0409111; //경도
    static Double Y = 37.65508056; //위도
    static String appName= "nonennal";


    Hyunbo()
    {

    }



    public void setTouristAttraction(Double x, Double y,int radious) throws JSONException
    {
        final String RESPONSE = "response";
        final String BODY = "body";
        final String PAGE_NUM = "pageNo";
        final String NUM_OF_ROWS = "numOfRows";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        String numOfRows;
        String pageNo ;

        //위치기반 기본주소 : http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=인증키 &
        //사용자의 현재위치를 Parameters로 50km 근방의 여행지 정보를 할당한다.
        String str = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=Si1LZhStHnfooZIH3OW%2BV5kMa9%2BoJy6u7wuOlqfeIXbSAAcBD%2FXOrOvJsKIRNlprnQVfK8%2B2Je%2BgMUXhcEznwg%3D%3D&mapX=127.0409111&mapY=37.65508056&radius=1000&pageNo=1&numOfRows=10&listYN=Y&arrange=A&MobileOS=ETC&MobileApp=biojima";
        JSONObject attractionJson = new JSONObject(str);
        JSONObject responseObject =  attractionJson.getJSONObject(RESPONSE);
        JSONObject bodyObject = responseObject.getJSONObject(BODY);

        pageNo =  bodyObject.getString(PAGE_NUM);
        numOfRows = bodyObject.getString(NUM_OF_ROWS);



        Log.e("CHECKMYVAL","json String : "+pageNo.toString());
        attraction = new ArrayList<String>();

        //Add to code
    }
}
