package com.example.admin.biojima;

/**
 * Created by seoyoonho on 2015-09-08.
 */
public class Change {

    private static double  PI, DEGRAD, RADDEG;
    private static double  re, olon, olat, sn, sf, ro;
    private static double         slat1, slat2, alon, alat, xn, yn, ra, theta;


    static String changeLonLat(double lon, double lat){

        PI = Math.asin(1.0)*2.0;
        DEGRAD = PI/180.0;
        RADDEG = 180.0/PI;

        re = map.Re/map.grid;
        slat1 = map.slat1 * DEGRAD;
        slat2 = map.slat2 * DEGRAD;
        olon = map.olon * DEGRAD;
        olat = map.olat * DEGRAD;

        sn = Math.tan(PI*0.25 + slat2*0.5)/Math.tan(PI*0.25 + slat1*0.5);
        sn = Math.log(Math.cos(slat1)/Math.cos(slat2))/Math.log(sn);
        sf = Math.tan(PI*0.25 + slat1*0.5);
        sf = Math.pow(sf,sn)*Math.cos(slat1)/sn;
        ro = Math.tan(PI*0.25 + olat*0.5);
        ro = re*sf/Math.pow(ro,sn);
        map.first = 1;



        ra = Math.tan(PI*0.25+lat*DEGRAD*0.5);
        ra = re*sf/Math.pow(ra,sn);
        theta = lon*DEGRAD - olon;
        if (theta >  PI) theta -= 2.0*PI;
        if (theta < -PI) theta += 2.0*PI;
        theta *= sn;

        int x = (int)(((float)(ra*Math.sin(theta)) + map.xo) + 1.5);
        int y = (int)(((float)(ro - ra*Math.cos(theta)) + map.yo) + 1.5);

        String a;
        a = new Integer(x).toString() + "," + new Integer(y).toString();

        return a;
    }
}

class map{
    static double Re,grid,slat1,slat2,olon,olat,xo,yo,first;

    static{
        Re    = 6371.00877;     // 지도반경
        grid  = 5.0;            // 격자간격 (km)
        slat1 = 30.0;           // 표준위도 1
        slat2 = 60.0;           // 표준위도 2
        olon  = 126.0;          // 기준점 경도
        olat  = 38.0;           // 기준점 위도
        xo    = 210/grid;   // 기준점 X좌표
        yo    = 675/grid;   // 기준점 Y좌표
        first = 0;
    }
}
