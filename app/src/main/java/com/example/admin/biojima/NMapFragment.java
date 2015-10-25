package com.example.admin.biojima;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

/**
 * NMapFragment 클래스는 NMapActivity를 상속하지 않고 NMapView만 사용하고자 하는 경우에 NMapContext를 이용한 예제임.
 * NMapView 사용시 필요한 초기화 및 리스너 등록은 NMapActivity 사용시와 동일함.
 */
public class NMapFragment extends Fragment {
	NMapViewerResourceProvider mMapViewerResourceProvider;

	private NMapContext mMapContext;

	/**
	 * Fragment에 포함된 NMapView 객체를 반환함
	 */
	private NMapView findMapView(View v) {

	    if (!(v instanceof ViewGroup)) {
	        return null;
	    }

	    ViewGroup vg = (ViewGroup)v;
	    if (vg instanceof NMapView) {
	        return (NMapView)vg;
	    }

	    for (int i = 0; i < vg.getChildCount(); i++) {

	        View child = vg.getChildAt(i);
		    if (!(child instanceof ViewGroup)) {
		        continue;
		    }

		    NMapView mapView = findMapView(child);
		    if (mapView != null) {
		    	return mapView;
		    }
	    }
	    return null;
	}

	/* Fragment 라이프사이클에 따라서 NMapContext의 해당 API를 호출함 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    mMapContext =  new NMapContext(super.getActivity());

	    mMapContext.onCreate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


		//throw new IllegalArgumentException("onCreateView should be implemented in the subclass of NMapFragment.");
		View view = inflater.inflate(R.layout.fragment_page2,container,false);

        NMapView mMapView = (NMapView)view.findViewById(R.id.mapView);

        mMapView.setApiKey("04edde0f95d089e814106c10960aca70");
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

//
//		mMapViewerResourceProvider = new NMapViewerResourceProvider(mMapContext);
//
//// create overlay manager
//		NMapOverlayManager mOverlayManager = new NMapOverlayManager(this.getContext(), mMapView, mMapViewerResourceProvider);
//		int markerId = NMapPOIflagType.PIN;
//
//// set POI data
//		NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
//		poiData.beginPOIdata(2);
//
//		poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerId, 0);
//		poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
//		poiData.endPOIdata();
//
//// create POI data overlay
//		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
//		poiDataOverlay.showAllPOIdata(0);

		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    // Fragment에 포함된 NMapView 객체 찾기
	    NMapView mapView = findMapView(super.getView());
	    if (mapView == null) {
	    	throw new IllegalArgumentException("NMapFragmen dose not have an instance of NMapView.");
	    }

	    // NMapActivity를 상속하지 않는 경우에는 NMapView 객체 생성후 반드시 setupMapView()를 호출해야함.
	    mMapContext.setupMapView(mapView);
	}

	@Override
	public void onStart(){
	    super.onStart();

	    mMapContext.onStart();
	}

	@Override
	public void onResume() {
	    super.onResume();

	    mMapContext.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();

	    mMapContext.onPause();
	}

	@Override
	public void onStop() {

		mMapContext.onStop();

	    super.onStop();
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		mMapContext.onDestroy();

	    super.onDestroy();
	}
}
