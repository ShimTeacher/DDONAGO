package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 8. 28..
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class AttractionFragment extends Fragment {

    static ProgressDialog progressDialog;
    String[] settings= new String [10];
    private static final String PREFERENCE_KEY = "seekBarPreference";
    EditText editText;
    ImageButton button;
    ImageButton MapButton;
    ImageButton settingButton;

    //TestCode
    static Double X = 127.0409111; //경도
    static Double Y = 37.65508056; //위도
    //TestCode

    private ArrayAdapter<String> mForecastAdapter;
    private View rootView;
    public AttractionFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }
    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh)
        {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        settingButton = (ImageButton)rootView.findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().overridePendingTransition(R.xml.anim100to0, R.xml.anim0to_100);
            }
        });

        editText = (EditText) rootView.findViewById(R.id.editText);
        button = (ImageButton)rootView.findViewById(R.id.findButton);
        MapButton = (ImageButton)rootView.findViewById(R.id.mapButton);

        //이 부분은 검색을 하는 부분으로 지도로검색부분과 구별하기 위하여 "0"을 앞에 붙였다.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConn())
                {
                    new AlertDialog.Builder(getContext())
                            .setTitle("인터넷 설정")
                            .setMessage("인터넷이 꺼져 있습니다. \nWIFI를 켜시겠습니까?")
                            .setPositiveButton("WIFI 켜기", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                else {
                    if(editText.getText().toString().isEmpty())
                    {
                        editText.setText("광운대학교");
                    }

                    Intent intent = new Intent(getActivity(), ResultActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, "0" + editText.getText().toString()) //ResultActivity로 EditText값을 넘겨줌.
                            .putExtra("gettitle", "nodata"); //ResultActivity로 EditText값을 넘겨줌.
                    startActivity(intent);
                }
            }
        });

        MapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isConn())
                {
                    new AlertDialog.Builder(getContext())
                            .setTitle("인터넷 설정")
                            .setMessage("인터넷이 꺼져 있습니다. \nWIFI를 켜시겠습니까?")
                            .setPositiveButton("WIFI 켜기", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), FindMapActivity.class);
                    startActivity(intent);
                }
            }
        });

        final ImageView Image = (ImageView)rootView.findViewById(R.id.walkerImage);
        Image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Image.setImageResource(R.drawable.mywalker2);
                return true;
            }
        });

        return rootView;
    }

}
