package com.geotagging.ui;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geotagging.R;
import com.geotagging.models.GeoDatabase;
import com.geotagging.models.GeoInfo;
import com.geotagging.utils.CommonMethods;
import com.geotagging.utils.DatabaseClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final String TAG=MapsActivity.class.getSimpleName();
    private Marker marker;
    private Handler handler=new Handler();
    private static final int TAG_LIST_INTENT=110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_maps);
            initializeActivity();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }

    }
    private void initializeActivity(){
        TextView header,openTag;
        try {
            header=(TextView)findViewById(R.id.header);
            CommonMethods.setFontBold(header);
            openTag=(TextView)findViewById(R.id.open_tag_list);
            CommonMethods.setFontRegular(openTag);
            openTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGeoTagList(0);
                }
            });
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerClickListener(this);
            showMarkerOnMap(new LatLng(19.045548, 72.895136),0);

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }

    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        try {
            showMarkerOnMap(latLng,0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        pickImageFromCamera(latLng);
                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }

                }
            },1000);

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            if((Integer)marker.getTag()!=0){
                openGeoTagList((Integer)marker.getTag());
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return false;
    }
    private void showMarkerOnMap(LatLng latLng,int id){
        try{
            if(marker!=null){
                marker.remove();
                marker=null;
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            marker = mMap.addMarker(markerOptions);
            marker.setTag(id);

            //move map camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void pickImageFromCamera(final LatLng latLng){
        try{
            PickSetup setup = new PickSetup()
                    .setTitle(getString(R.string.image_pick_text))
                    .setTitleColor(getResources().getColor(R.color.grey_text_color))
                    .setSystemDialog(false)
                    .setPickTypes(EPickType.CAMERA);

            PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                @Override
                public void onPickResult(PickResult r) {
                    try {
                        saveUserDataInDataBase(latLng,r.getPath());

                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                }
            }).show(this);




        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    private void saveUserDataInDataBase(final LatLng latLng, final String imagePath){
        AsyncTask<Void,Void,Void>saveData;
        try{
            saveData=new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showProgressbar();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try{
                        String address=getLocationFromLatLng(latLng);
                        GeoInfo geoInfo= new GeoInfo();
                        geoInfo.setImageUrl(imagePath);
                        geoInfo.setLat(latLng.latitude);
                        geoInfo.setLon(latLng.longitude);
                        geoInfo.setAddress(address);
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .geoTaggingDao()
                                .insert(geoInfo);

                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try{
                        hideProgressbar();
                        Toast.makeText(MapsActivity.this, getString(R.string.tag_save_text), Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openGeoTagList(0);
                            }
                        },200);


                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    private void showProgressbar(){
        CircularProgressBar progressBar;
        try{
            progressBar=(CircularProgressBar)findViewById(R.id.loader);
            if(progressBar.getVisibility()!= View.VISIBLE){
                progressBar.setVisibility(View.VISIBLE);
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void hideProgressbar(){
        CircularProgressBar progressBar;
        try{
            progressBar=(CircularProgressBar)findViewById(R.id.loader);
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void openGeoTagList(int selectedMarkerID){
        try{
            if(marker!=null){
                marker.remove();
                marker=null;
            }
            Intent intent = new Intent(this, GeoTaggingListActivity.class);
            intent.putExtra("markerID",selectedMarkerID);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);
            startActivityForResult(intent, TAG_LIST_INTENT);
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode==112){
                showMarkerOnMap(new LatLng(data.getExtras().getDouble("lat"),data.getExtras().getDouble("lon")),data.getExtras().getInt("id"));
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private String getLocationFromLatLng(LatLng latLng){
        String address="";
        List<Address> addresses;
        Geocoder geocoder;
        try{
            address=getString(R.string.unknown_location_label);
            geocoder = new Geocoder(getApplicationContext());
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                strAddress.append(fetchedAddress.getAddressLine(fetchedAddress.getMaxAddressLineIndex()));
                address = strAddress.toString();
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return address;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
