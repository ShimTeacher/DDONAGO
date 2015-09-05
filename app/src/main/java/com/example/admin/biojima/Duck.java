package com.example.admin.biojima;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Duck extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duck);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_duck, menu);
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

            findLocation();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findLocation(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //매니저


        long minTime = 10000; //10초간격업데이트 밀리세컨드
        float minDistance = 0;   //움직엿을때업데이트 항상업데이트

        MyLocationListener listener = new MyLocationListener();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, listener);  //위치요청 GPSPROVIDER는오차가크다
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, listener);
        Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation != null)
        {
            Double latitude = lastLocation.getLatitude();
            Double longitude = lastLocation.getLongitude();


            Log.d("ffff", new Double(latitude).toString());
            Log.d("ffff",new Double(longitude).toString());

//            textview.setText("가장 최근 내 위치 : " +latitude + " , " + longitude );
//            textview.invalidate();
        }
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {    //location manager가 이메소드를참고하고업데이트함
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

//            textview.setText("내 위치 : " +latitude + " , " + longitude );
//            textview.invalidate();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
