package com.example.sgcamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sgcamera.camera.model.ListTrafficCamera;
import com.example.sgcamera.camera.model.TrafficCamera;
import com.example.sgcamera.camera.model.TrafficCameraMarkerData;
import com.example.sgcamera.camera.network.ApiClient;
import com.example.sgcamera.camera.network.ApiService;
import com.example.sgcamera.room.AppDatabase;
import com.example.sgcamera.roomFavourite.Camera;
import com.example.sgcamera.roomFavourite.CameraDao;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsLocation extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<TrafficCamera> mListMarker = new ArrayList<>();
    private CameraDao cameraDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps_location, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getAllDataLocaion();
    }

    private void getAllDataLocaion() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        Call<ListTrafficCamera> call = apiService.getAllLocation();

        call.enqueue(new Callback<ListTrafficCamera>() {
            @Override
            public void onResponse(Call<ListTrafficCamera> call, Response<ListTrafficCamera> response) {
                progressDialog.dismiss();
                mListMarker = response.body().getmData();
                initMarker(mListMarker);
            }

            @Override
            public void onFailure(Call<ListTrafficCamera> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMarker(List<TrafficCamera> mListMarker) {
        // looping for items
        for (int i = 0; i < mListMarker.size(); i++)
        {
            // assign the list cameras
            List<TrafficCamera.CameraDetails> cameraDetailsList = mListMarker.get(i).getCameraDetails();

            // looping for each camera
            for (int j = 0; j < cameraDetailsList.size(); j++)
            {
                AppDatabase cameraDatabase = AppDatabase.getAppDatabase(getContext());
                cameraDao = cameraDatabase.cameraDao();
                Camera cameraEntity = new Camera();

                int finalJ = j;
                new Thread(() -> {
                    Camera cameraGet = cameraDao.getCamera(cameraDetailsList.get(finalJ).getCamera_id());

                    if (cameraGet == null)
                    {
                        cameraEntity.setCamera_id(cameraDetailsList.get(finalJ).getCamera_id());
                        cameraEntity.setFavourite(Boolean.FALSE);
                        cameraDao.insert(cameraEntity);
                    }
                }).start();

                LatLng location = new LatLng(
                    cameraDetailsList.get(j).getLocations().getLatitude(),
                    cameraDetailsList.get(j).getLocations().getLongitude());

                Marker marker = mMap.addMarker(
                        new MarkerOptions().position(location));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 11.0f));

                marker.setTag(new TrafficCameraMarkerData(cameraDetailsList.get(j).getTimestampinside(), cameraDetailsList.get(j).getImage(),
                        cameraDetailsList.get(j).getLocations().getLatitude(), cameraDetailsList.get(j).getLocations().getLongitude(), cameraDetailsList.get(j).getCamera_id(),
                        cameraDetailsList.get(j).getImage_metadata().getHeight(), cameraDetailsList.get(j).getImage_metadata().getWidth(), cameraDetailsList.get(j).getImage_metadata().getMd5()));
                mMap.setInfoWindowAdapter(new TestInfoWindowAdapter(getContext()));
            }
        }
    }

    private class TestInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        private Context context;

        public TestInfoWindowAdapter(Context context)
        {
            this.context = context;
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            TrafficCameraMarkerData data = (TrafficCameraMarkerData) marker.getTag();
            View view = LayoutInflater.from(context).inflate(R.layout.info_popup, null);

            AppDatabase cameraDatabase = AppDatabase.getAppDatabase(getContext());
            cameraDao = cameraDatabase.cameraDao();
            Camera cameraEntity = new Camera();

            // call the element used in layout
            TextView timestamp = view.findViewById(R.id.timestamp);
            ImageView imageView = view.findViewById(R.id.image);
            TextView latitudeV = view.findViewById(R.id.latitude);
            TextView longitudeV = view.findViewById(R.id.longitude);
            ImageView loveView = view.findViewById(R.id.imageLove);
            TextView cameraId = view.findViewById(R.id.cameraId);
            TextView height = view.findViewById(R.id.height);
            TextView width = view.findViewById(R.id.width);
            TextView md5 = view.findViewById(R.id.md5);

            // set value to each information
            timestamp.setText(data.getTimestampinside());
            Picasso.get()
                    .load(data.getImage())
                    .resize(data.getWidth(), data.getHeight())
                    .into(imageView);
            latitudeV.setText(String.valueOf(data.getLatitude()));
            longitudeV.setText(String.valueOf(data.getLongitude()));

            new Thread(() -> {
                Camera cameraGet = cameraDao.getCamera(data.getCamera_id());
                if (cameraGet != null && cameraGet.getFavourite().equals(Boolean.TRUE))
                {
                    loveView.setImageResource(R.drawable.heart_color_icon);
                }
            }).start();

            cameraId.setText(data.getCamera_id());
            height.setText(String.valueOf(data.getHeight()));
            width.setText(String.valueOf(data.getWidth()));
            md5.setText(data.getMd5());

            mMap.setOnInfoWindowClickListener(marker1 -> new Thread(() -> {
                Camera cameraGet = cameraDao.getCamera(data.getCamera_id());
                if (cameraGet != null && cameraGet.getFavourite().equals(Boolean.TRUE))
                {
                    cameraGet.setCamera_id(data.getCamera_id());
                    cameraGet.setId(cameraGet.getId());
                    cameraGet.setFavourite(Boolean.FALSE);
                    cameraDao.update(cameraGet);
                    loveView.setImageResource(R.drawable.heart_icon);
                }
                else if (cameraGet != null && cameraGet.getFavourite().equals(Boolean.FALSE))
                {
                    cameraGet.setCamera_id(data.getCamera_id());
                    cameraGet.setId(cameraGet.getId());
                    cameraGet.setFavourite(Boolean.TRUE);
                    cameraDao.update(cameraGet);
                    cameraDao.update(cameraGet);
                    loveView.setImageResource(R.drawable.heart_color_icon);
                }
            }).start());

            return view;
        }


        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return null;
        }
    }
}