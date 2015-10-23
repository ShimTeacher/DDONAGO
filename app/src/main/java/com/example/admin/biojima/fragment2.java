package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 10. 24..
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by adslbna2 on 15. 10. 24..
 */
public class fragment2 extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page2,container,false);


        return view;
    }
}