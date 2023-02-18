package com.example.sgcamera;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgcamera.list.CameraAdapter;
import com.example.sgcamera.room.AppDatabase;
import com.example.sgcamera.roomFavourite.Camera;
import com.example.sgcamera.roomFavourite.CameraDao;

import java.util.ArrayList;
import java.util.List;

public class ListFavourite extends Fragment {
    private RecyclerView recyclerView;
    private AppDatabase cameraDatabase;
    private CameraDao cameraDao;
    private CameraAdapter cameraAdapter;

    @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_favourite, container, false);

        EditText cameraId = v.findViewById(R.id.cameraId);
        ImageView date = v.findViewById(R.id.date);
        ImageView sort = v.findViewById(R.id.sort);
        recyclerView = v.findViewById(R.id.cameraList);

        cameraDatabase = AppDatabase.getAppDatabase(getContext());
        cameraDao = cameraDatabase.cameraDao();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new AsyncTask<Void, Void, List<Camera>>() {
            @Override
            protected List<Camera> doInBackground(Void... voids) {
                cameraDao = cameraDatabase.cameraDao();
                return cameraDao.getAllCamera();
            }

            @Override
            protected void onPostExecute(List<Camera> entities) {
                // Update UI with the retrieved entities
                cameraAdapter = new CameraAdapter(getContext(), entities);
                cameraAdapter.setCameras(entities);
                recyclerView.setAdapter(cameraAdapter);
            }
        }.execute();

        // to sort the list with same value as camera_id will be top of the list
        cameraId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cameraid = s.toString();

                new Thread(() -> {
                    List<Camera> cameras = cameraDao.getAllCamera();

                    if (cameras.size() > 0) {
                        List<Camera> filteredCameras = new ArrayList<>();
                        for (int i = 0; i < cameras.size(); i++) {
                            if (cameras.get(i).getCamera_id().contains(cameraid)) {
                                filteredCameras.add(cameras.get(i));
                            }
                        }
                        if (filteredCameras.isEmpty() && cameraid.isEmpty()) {
                            filteredCameras.addAll(cameras);
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> cameraAdapter.setCameras(filteredCameras));
                    }
                }).start();
            }
        });

        sort.setOnClickListener(v1 -> {
            List<Camera> cameras = cameraAdapter.getCameras(); // get the current list of cameras from the adapter
            boolean isAscending;

            if (cameras != null && cameras.size() > 0) {
                // check if the list is already sorted in ascending order
                if (cameras.get(0).getCamera_id().compareTo(cameras.get(cameras.size() - 1).getCamera_id()) < 0) {
                    isAscending = true;
                } else {
                    isAscending = false;
                }

                // sort the list based on the current sort order
                boolean finalIsAscending = isAscending;
                cameras.sort((c1, c2) -> {
                    if (finalIsAscending) {
                        return c2.getCamera_id().compareTo(c1.getCamera_id()); // sort in descending order
                    } else {
                        return c1.getCamera_id().compareTo(c2.getCamera_id()); // sort in ascending order
                    }
                });

                // set the sorted list of cameras to the adapter and notify the adapter to update the UI
                cameraAdapter.setCameras(cameras);
            }
        });


        return v;
    }
}