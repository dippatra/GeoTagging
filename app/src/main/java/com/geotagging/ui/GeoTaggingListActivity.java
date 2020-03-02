package com.geotagging.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geotagging.R;
import com.geotagging.adapters.TagListAdapter;
import com.geotagging.interfaces.TagListItemClickListener;
import com.geotagging.models.GeoInfo;
import com.geotagging.utils.CommonMethods;
import com.geotagging.utils.DatabaseClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class GeoTaggingListActivity extends AppCompatActivity {
    private static final String TAG=GeoTaggingListActivity.class.getSimpleName();
    private RecyclerView tagList;
    private TagListAdapter tagListAdapter;
    private int selectedMarkerID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tagging_list);
        try{
            getIntentData();
            initializeActivityControl();
            getTaggingData();

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void getIntentData(){
        try{
            if(getIntent().hasExtra("markerID")){
                selectedMarkerID=getIntent().getIntExtra("markerID",0);
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void initializeActivityControl(){
        TextView headerText;
        ImageView back;
        try{
            tagList=(RecyclerView)findViewById(R.id.tag_list);
            headerText=(TextView)findViewById(R.id.header_Text);
            CommonMethods.setFontBold(headerText);
            back=(ImageView)findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        onBackPressed();
                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                }
            });


        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void initializeRecyclerView(ArrayList<GeoInfo>geoInfos){
        TagListItemClickListener tagListItemClickListener;
        try{
            if(tagListAdapter==null){
                tagListItemClickListener=new TagListItemClickListener() {
                    @Override
                    public void onTagListItemClickListener(GeoInfo geoInfo) {
                        try {
                            Intent intent=new Intent();
                            intent.putExtra("lat",geoInfo.getLat());
                            intent.putExtra("id",geoInfo.getId());
                            intent.putExtra("lon",geoInfo.getLon());
                            intent.putExtra("imageUrl",geoInfo.getImageUrl());
                            setResult(112,intent);
                            finish();

                        }catch (Exception ex){
                            Log.e(TAG,ex.getMessage());
                        }
                    }
                };
                tagListAdapter = new TagListAdapter(GeoTaggingListActivity.this, geoInfos,tagListItemClickListener);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                tagList.setLayoutManager(mLayoutManager);
                tagList.setItemAnimator(new DefaultItemAnimator());
                tagList.setAdapter(tagListAdapter);

            }


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
    private void getTaggingData(){

        try{
         new AsyncTask<Void, Void, List<GeoInfo>>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showProgressbar();
                }

                @Override
                protected List<GeoInfo> doInBackground(Void... voids) {
                    List<GeoInfo> geoTagList=null;
                    try{

                       geoTagList=DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .geoTaggingDao()
                                .getAllGeoInfo();

                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                    return geoTagList;
                }

                @Override
                protected void onPostExecute(List<GeoInfo>result) {
                    super.onPostExecute(result);
                    try{
                        hideProgressbar();
                        if(result!=null&&result.size()>0){
                            hideErrorMessage();
                            if(selectedMarkerID>0){
                                initializeRecyclerView(getSortedTagList((ArrayList<GeoInfo>)result));
                            }else {
                                initializeRecyclerView((ArrayList<GeoInfo>)result);
                            }

                        }else {
                            showErrorMessage();
                        }


                    }catch (Exception ex){
                        Log.e(TAG,ex.getMessage());
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    private void showErrorMessage(){
        TextView errorText;
        try{
            errorText=(TextView) findViewById(R.id.empty_tag);
            if(errorText.getVisibility()!= View.VISIBLE){
                errorText.setVisibility(View.VISIBLE);
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private void hideErrorMessage(){
        TextView errorText;
        try{
            errorText=(TextView) findViewById(R.id.empty_tag);
            if(errorText.getVisibility()== View.VISIBLE){
                errorText.setVisibility(View.GONE);
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            finish();
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
    }
    private ArrayList<GeoInfo>getSortedTagList(ArrayList<GeoInfo>unSortedTagList){
        ArrayList<GeoInfo>sortedTagList=new ArrayList<>();
        GeoInfo geoInfo,selectedMarkerGeoInfo=null;
        try {
            if(unSortedTagList!=null&&unSortedTagList.size()>0){
                for (int i=0;i<unSortedTagList.size();i++){
                    geoInfo=unSortedTagList.get(i);
                    if(geoInfo.getId()!=selectedMarkerID){
                        sortedTagList.add(geoInfo);
                    }else {
                        selectedMarkerGeoInfo=geoInfo;
                    }
                }
                if(selectedMarkerGeoInfo!=null){
                    sortedTagList.add(0,selectedMarkerGeoInfo);
                }
            }
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return sortedTagList;
    }
}
