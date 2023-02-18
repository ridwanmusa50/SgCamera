package com.example.sgcamera.list;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgcamera.R;
import com.example.sgcamera.camera.model.ListTrafficCamera;
import com.example.sgcamera.camera.model.TrafficCamera;
import com.example.sgcamera.camera.network.ApiClient;
import com.example.sgcamera.camera.network.ApiService;
import com.example.sgcamera.room.AppDatabase;
import com.example.sgcamera.roomFavourite.Camera;
import com.example.sgcamera.roomFavourite.CameraDao;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    private Context context;
    private List<Camera> list;

    public CameraAdapter(Context context, List<Camera> list){
        this.context = context;
        this.list = list;
    }

    // Method to set the camera list and notify adapter of data set change
    public void setCameras(List<Camera> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Camera> getCameras() {
        return list;
    }

    public void clearCameras() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.favourite_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Camera camera = list.get(position);
        AppDatabase cameraDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        CameraDao cameraDao = cameraDatabase.cameraDao();
        holder.cameraId.setText(camera.getCamera_id());

        if (camera.getFavourite().equals(Boolean.TRUE))
        {
            holder.ikon.setImageResource(R.drawable.heart_color_icon);
        }
        else
        {
            holder.ikon.setImageResource(R.drawable.heart_icon);
        }

        holder.ikon.setOnClickListener(v -> new Thread(() -> {
            // Perform database operation on non-UI thread
            Camera cameraGet = cameraDao.getCamera(camera.getCamera_id());
            if (cameraGet != null && cameraGet.getFavourite().equals(Boolean.TRUE)) {
                cameraGet.setCamera_id(camera.getCamera_id());
                cameraGet.setId(cameraGet.getId());
                cameraGet.setFavourite(Boolean.FALSE);
                cameraDao.update(cameraGet);
                // Update UI on main thread
                holder.ikon.post(() -> holder.ikon.setImageResource(R.drawable.heart_icon));
            } else if (cameraGet != null && cameraGet.getFavourite().equals(Boolean.FALSE)) {
                cameraGet.setCamera_id(camera.getCamera_id());
                cameraGet.setId(cameraGet.getId());
                cameraGet.setFavourite(Boolean.TRUE);
                cameraDao.update(cameraGet);
                // Update UI on main thread
                holder.ikon.post(() -> holder.ikon.setImageResource(R.drawable.heart_color_icon));
            }
        }).start());

        holder.row.setOnClickListener(v -> {
            // create the dialog
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.info_popup);
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            Call<ListTrafficCamera> call = apiService.getAllLocation();

            call.enqueue(new Callback<ListTrafficCamera>() {
                @Override
                public void onResponse(Call<ListTrafficCamera> call, Response<ListTrafficCamera> response) {
                    List<TrafficCamera> mListMarker;
                    mListMarker = response.body().getmData();
                    List<TrafficCamera.CameraDetails> cameraDetailsList;
                    // looping for items
                    for (int i = 0; i < mListMarker.size(); i++)
                    {
                        // assign the list cameras
                        cameraDetailsList = mListMarker.get(i).getCameraDetails();

                        // looping for each camera
                        for (int j = 0; j < cameraDetailsList.size(); j++)
                        {
                            if (cameraDetailsList.get(j).getCamera_id().equals(camera.getCamera_id()))
                            {
                                // call the element used in layout
                                TextView timestamp = dialog.findViewById(R.id.timestamp);
                                ImageView imageView = dialog.findViewById(R.id.image);
                                TextView latitudeV = dialog.findViewById(R.id.latitude);
                                TextView longitudeV = dialog.findViewById(R.id.longitude);
                                ImageView loveView = dialog.findViewById(R.id.imageLove);
                                TextView cameraId = dialog.findViewById(R.id.cameraId);
                                TextView height = dialog.findViewById(R.id.height);
                                TextView width = dialog.findViewById(R.id.width);
                                TextView md5 = dialog.findViewById(R.id.md5);

                                // set value to each information
                                timestamp.setText(cameraDetailsList.get(j).getTimestampinside());
                                Picasso.get()
                                        .load(cameraDetailsList.get(j).getImage())
                                        .resize(cameraDetailsList.get(j).getImage_metadata().getWidth(), cameraDetailsList.get(j).getImage_metadata().getHeight())
                                        .into(imageView);
                                latitudeV.setText(String.valueOf(cameraDetailsList.get(j).getLocations().getLatitude()));
                                longitudeV.setText(String.valueOf(cameraDetailsList.get(j).getLocations().getLongitude()));
                                loveView.setImageResource(R.drawable.heart_color_icon);
                                cameraId.setText(cameraDetailsList.get(j).getCamera_id());
                                height.setText(String.valueOf(cameraDetailsList.get(j).getImage_metadata().getHeight()));
                                width.setText(String.valueOf(cameraDetailsList.get(j).getImage_metadata().getWidth()));
                                md5.setText(cameraDetailsList.get(j).getImage_metadata().getMd5());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListTrafficCamera> call, Throwable t) {
                    Toast.makeText(dialog.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // set the text in the dialog
            TextView cameraIdText = dialog.findViewById(R.id.cameraId);
            cameraIdText.setText(camera.getCamera_id());

            // show the dialog
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView cameraId;
        private final ImageView ikon;
        private final LinearLayout row;

        public ViewHolder(View itemView){
            super(itemView);

            cameraId = itemView.findViewById(R.id.cameraId);
            ikon = itemView.findViewById(R.id.favouriteIcon);
            row = itemView.findViewById(R.id.row);
        }
    }
}