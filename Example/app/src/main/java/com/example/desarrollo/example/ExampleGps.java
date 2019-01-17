package com.example.desarrollo.example;


import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.desarrollo.example.adapter.TrackingAdapter;
import com.google.android.gms.location.LocationListener;
import com.example.desarrollo.example.R;
import com.library.modulo.core.db.database.gpsChip.Gps;
import com.library.modulo.core.gps.GpsUtils;
import com.library.modulo.features.getGps.GpsPresenter;
import com.library.modulo.features.getGps.data.loadGps.LoadGpsGoogleRepository;
import com.library.modulo.features.getGps.data.loadGps.LoadGpsRepository;
import com.library.modulo.features.getGps.domain.usescases.LoadGps;
import com.library.modulo.features.getGps.ui.GpsContract;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class ExampleGps extends AppCompatActivity implements View.OnClickListener, GpsContract.View,
        EasyPermissions.PermissionCallbacks, LocationListener {

    private static final int RC_DEVICES = 123;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK};
    private Integer permissionsCode;
    private String rationale;
    private Switch switchGps;
    private Button initTracking, btnRefresh, initTrackingGoogle;
    private RecyclerView recyclerView;
    private EditText editTracking;
    private TrackingAdapter trackingAdapter;
    private GpsContract.Presenter ubicacionPresenter;
    private GpsContract.Presenter ubicacionGooglePresenter;
    private Boolean isTracking = false;
    private Boolean isGoogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_gps);
        ubicacionPresenter = new GpsPresenter(this,
                new LoadGps(new LoadGpsRepository(getApplicationContext())));
        ubicacionGooglePresenter = new GpsPresenter(this,
                new LoadGps(new LoadGpsGoogleRepository(getApplicationContext())));
        switchGps = (Switch) findViewById(R.id.switchGps);
        initTracking = (Button) findViewById(R.id.btnInitTracking);
        initTrackingGoogle = (Button) findViewById(R.id.btnInitTrackingGoogle);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerTracking);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        editTracking = (EditText) findViewById(R.id.edtPeriodo);
        if (EasyPermissions.hasPermissions(getApplicationContext(), permissions)) {
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "Requiere permisos",
                    RC_DEVICES, permissions);
        }
        switchGps.setOnClickListener(this);
        initTracking.setOnClickListener(this);
        initTrackingGoogle.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);

        ubicacionPresenter.getListTracking();

        stateGps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateGps();
    }

    public void stateGps() {
        if (GpsUtils.isGPSEnabled(this)) {
            switchGps.setChecked(true);
        } else {
            switchGps.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.switchGps) {
            if (switchGps.isChecked()) {
                ubicacionPresenter.onOrOffGps(true);
            } else {
                ubicacionPresenter.onOrOffGps(false);
            }
            stateGps();

        } else if (i == R.id.btnInitTracking) {
            isGoogle = false;
            if (isTracking) {
                ubicacionPresenter.stopTrackingGps();
            } else {
                int periodTrack = Integer.valueOf(editTracking.getText().toString());
                ubicacionPresenter.intervalTimeGps(periodTrack);
                ubicacionPresenter.starTrackingGps();
            }

        } else if (i == R.id.btnRefresh) {
            ubicacionPresenter.refreshTracking();
        } else if (i == R.id.btnInitTrackingGoogle) {
            isGoogle = true;
            if (isTracking) {
                ubicacionGooglePresenter.stopTrackingGps();
            } else {
                int periodTrack = Integer.valueOf(editTracking.getText().toString());
                ubicacionGooglePresenter.intervalTimeGps(periodTrack);
                ubicacionGooglePresenter.starTrackingGps();
            }
        }
    }


    @Override
    public void goToNext() {

    }

    public void refreshTrackingAdapter(List<Gps> list) {
        runOnUiThread(() -> {
            trackingAdapter.setItems(list);
            trackingAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void getListTracking(List<Gps> list) {
        runOnUiThread(() -> {
            trackingAdapter = new TrackingAdapter(this, list);
            recyclerView.setAdapter(trackingAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.scrollToPosition(trackingAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void succesStartrackingGps(Boolean isStart) {
        if (isStart) {
            runOnUiThread(() -> {
                isTracking = true;
                if (isGoogle) {
                    initTrackingGoogle.setText("PARAR TRACKING GOOGLE");
                    initTrackingGoogle.setBackgroundColor(getResources().getColor(R.color.colorRed));
                } else {
                    initTracking.setText("PARAR TRACKING GPS");
                    initTracking.setBackgroundColor(getResources().getColor(R.color.colorRed));
                }

                editTracking.setEnabled(false);
                editTracking.setFocusable(false);
            });
        }
    }

    @Override
    public void succesStoprackingGps(Boolean isStop) {
        if (isStop) {
            runOnUiThread(() -> {
                isTracking = false;
                if (isGoogle) {
                    initTrackingGoogle.setText("INICIAR TRACKING GOOGLE");
                    initTrackingGoogle.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                } else {
                    initTracking.setText("INICIAR TRACKING GPS");
                    initTracking.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                }

                editTracking.setEnabled(true);
            });
        }
    }

    /**
     * Permissions request with EasyPermissions
     *
     * @param permissions     String[] array of the desired permissions
     * @param permissionsCode Given request code in order to handle the result
     */
    public void requestCustomPermissions(@NonNull String[] permissions, @NonNull String rationale, @NonNull Integer permissionsCode) {
        this.permissionsCode = permissionsCode;
        this.rationale = rationale;
        this.permissions = permissions;
        EasyPermissions.requestPermissions(this, rationale, permissionsCode, permissions);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms.size() < permissions.length) {
            EasyPermissions.requestPermissions(this, rationale, permissionsCode, permissions);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermissions.requestPermissions(this, rationale, permissionsCode, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
