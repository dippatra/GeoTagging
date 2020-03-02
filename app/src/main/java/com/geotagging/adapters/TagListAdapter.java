package com.geotagging.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.geotagging.R;
import com.geotagging.interfaces.TagListItemClickListener;
import com.geotagging.models.GeoInfo;
import com.geotagging.utils.CommonMethods;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.MyViewHolder>  {
    private static final String TAG=TagListAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<GeoInfo>geoInfos;
    private TagListItemClickListener tagListItemClickListener;
    public TagListAdapter(Context context, ArrayList<GeoInfo>geoInfos, TagListItemClickListener tagListItemClickListener){
        this.context=context;
        this.geoInfos=geoInfos;
        this.tagListItemClickListener=tagListItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tag_list_item, viewGroup, false);
        return new MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            GeoInfo geoInfo;
            geoInfo=geoInfos.get(position);
            holder.lat.setText("Lat "+String.valueOf(geoInfo.getLat()));
            CommonMethods.setFontRegular(holder.lat);
            holder.lon.setText("Lon "+String.valueOf(geoInfo.getLon()));
            CommonMethods.setFontRegular(holder.lon);
            holder.address.setText(geoInfo.getAddress());
            holder.address.setSelected(true);
            CommonMethods.setFontBold(holder.address);
            Glide.with(context)
                    .load(geoInfo.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.camera)
                            .error(R.drawable.camera))
                    .into(holder.tagImage);
             holder.container.setTag(geoInfo);
             holder.container.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     try{
                         if(tagListItemClickListener!=null){
                             tagListItemClickListener.onTagListItemClickListener((GeoInfo)v.getTag());
                         }

                     }catch (Exception ex){
                         Log.e(TAG,ex.getMessage());
                     }
                 }
             });


        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        try {
            if(geoInfos.size()>0){
                return geoInfos.size();
            }

        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView tagImage;
        TextView lat, lon,address;
        RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            try{
                tagImage=(ImageView)view.findViewById(R.id.tag_photo);
                lat=(TextView)view.findViewById(R.id.lat_text);
                lon=(TextView)view.findViewById(R.id.lon_text);
                container=(RelativeLayout)view.findViewById(R.id.container);
                address=(TextView)view.findViewById(R.id.address);

            }catch (Exception ex){
                Log.e(TAG,ex.getMessage());
            }

        }


    }
}
