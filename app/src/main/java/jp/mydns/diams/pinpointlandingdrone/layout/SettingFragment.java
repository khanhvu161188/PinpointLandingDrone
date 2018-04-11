package jp.mydns.diams.pinpointlandingdrone.layout;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import jp.mydns.diams.pinpointlandingdrone.Common.PinpointLandingDroneApplication;
import jp.mydns.diams.pinpointlandingdrone.MainActivity;
import jp.mydns.diams.pinpointlandingdrone.R;

public class SettingFragment extends Fragment {

    private View view;

    private EditText mThresholdA1_edt, mThresholdA2_edt, mThresholdB1_edt, mThresholdB2_edt;
    private EditText mMoveSpeed_edt, mUpDownSpeed_edt;

    private Button mBeacon_btn, mSave_btn, mDefault_btn;

    private FlightController mFlightController;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        mThresholdA1_edt = (EditText) view.findViewById(R.id.setting_thresholdA1_editText);
        mThresholdA2_edt = (EditText) view.findViewById(R.id.setting_thresholdA2_editText);
        mThresholdB1_edt = (EditText) view.findViewById(R.id.setting_thresholdB1_editText);
        mThresholdB2_edt = (EditText) view.findViewById(R.id.setting_thresholdB2_editText);

        mMoveSpeed_edt = (EditText) view.findViewById(R.id.setting_parallel_editText);
        mUpDownSpeed_edt = (EditText) view.findViewById(R.id.setting_updown_editText);

        mBeacon_btn = (Button) view.findViewById(R.id.setting_beacon_button);
        mBeacon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BeaconListFragment nextFragment = new BeaconListFragment();
                transaction.replace(R.id.fragment_container, nextFragment, BeaconListFragment.class.getName());
                transaction.commit();
            }
        });

        mSave_btn = (Button) view.findViewById(R.id.setting_save_button);
        mSave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        mDefault_btn = (Button) view.findViewById(R.id.setting_default_button);
        mDefault_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mThresholdA1_edt.setText("-63");
                mThresholdA2_edt.setText("-63");
                mThresholdB1_edt.setText("-63");
                mThresholdB2_edt.setText("-63");

                mMoveSpeed_edt.setText("0.7");
                mUpDownSpeed_edt.setText("0.5");
            }
        });

        initFlightController();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initFlightController() {
        Aircraft aircraft = PinpointLandingDroneApplication.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_LONG).show();
            mFlightController = null;
        } else {
            mFlightController = aircraft.getFlightController();
            mFlightController.setOnboardSDKDeviceDataCallback(onboardSDKDeviceDataCallback);
        }
    }

    private FlightController.OnboardSDKDeviceDataCallback onboardSDKDeviceDataCallback = new FlightController.OnboardSDKDeviceDataCallback() {
        @Override
        public void onReceive(byte[] bytes) {

        }
    };

    private void save() {
        String data = "setting:" + mThresholdA1_edt.getText().toString() + ",";
        data += mThresholdA2_edt.getText().toString() + ",";
        data += mThresholdB1_edt.getText().toString() + ",";
        data += mThresholdB2_edt.getText().toString() + ",";
        data += mMoveSpeed_edt.getText().toString() + ",";
        data += mUpDownSpeed_edt.getText().toString();
        if (mFlightController != null) {
            mFlightController.sendDataToOnboardSDKDevice(data.getBytes(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                }
            });
        }
    }
}
