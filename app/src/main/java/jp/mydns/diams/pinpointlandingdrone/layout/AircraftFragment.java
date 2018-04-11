package jp.mydns.diams.pinpointlandingdrone.layout;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import jp.mydns.diams.pinpointlandingdrone.Common.PinpointLandingDroneApplication;
import jp.mydns.diams.pinpointlandingdrone.R;

public class AircraftFragment extends Fragment {

    private View view;

    private Button mTakeOff_btn, mStop_btn;

    private EditText mX_edt, mY_edt, mZ_edt;
    private Button mFlight_btn;

    private EditText mTime_edt;
    private Button mMeasurement_btn;

    private TextView mConnect_txt;

    private FlightController mFlightController;

    public AircraftFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AircraftFragment newInstance() {
        AircraftFragment fragment = new AircraftFragment();
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
        view = inflater.inflate(R.layout.fragment_aircraft, container, false);

        mTakeOff_btn = (Button) view.findViewById(R.id.aircraft_takeOff_button);
        mTakeOff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlightController != null) {
                    mFlightController.sendDataToOnboardSDKDevice("takeoff\0".getBytes(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    });
                }
            }
        });
        mStop_btn = (Button) view.findViewById(R.id.aircraft_stop_button);
        mStop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlightController != null) {
                    mFlightController.sendDataToOnboardSDKDevice("stop\0".getBytes(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    });
                }
            }
        });

        mX_edt = (EditText) view.findViewById(R.id.aircraft_x_editText);
        mY_edt = (EditText) view.findViewById(R.id.aircraft_y_editText);
        mZ_edt = (EditText) view.findViewById(R.id.aircraft_z_editText);
        mFlight_btn = (Button) view.findViewById(R.id.aircraft_flight_button);
        mFlight_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlightController != null) {
                    String control_data = mX_edt.getText() + "," + mY_edt.getText() + "," + mZ_edt.getText();
                    mFlightController.sendDataToOnboardSDKDevice(("control:" + control_data + "\0").getBytes(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    });
                }
                mX_edt.setText("0");
                mY_edt.setText("0");
                mZ_edt.setText("0");
            }
        });

        mTime_edt = (EditText) view.findViewById(R.id.aircraft_time_editText);
        mMeasurement_btn = (Button) view.findViewById(R.id.aircraft_measurement_button);
        mMeasurement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlightController != null) {
                    String data = "measurement:" + mTime_edt.getText() + "\0";
                    mFlightController.sendDataToOnboardSDKDevice(data.getBytes(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    });
                }
            }
        });

        mConnect_txt = (TextView) view.findViewById(R.id.aircraft_connect_textView);
        mConnect_txt.setText("接続ナシ");
        mConnect_txt.setBackgroundColor(Color.RED);

        initFlightController();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Aircraft.onAttach", "onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Aircraft.onStart", "onStart");

        if (mFlightController != null) {
            mFlightController.sendDataToOnboardSDKDevice("isConnected".getBytes(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                }
            });
        }
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
            String data;
            try {
                data = new String(bytes, "SJIS");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                data = e.getMessage() + "\n";
            }

            if (data.indexOf("isConnected") == 0) {
                boolean status = Boolean.parseBoolean(data.split(":")[1]);
                final String status_str;
                final int status_color;
                if (status) {
                    status_str = "接続アリ";
                    status_color = Color.GREEN;
                } else {
                    status_str = "接続ナシ";
                    status_color = Color.RED;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnect_txt.setText(status_str);
                        mConnect_txt.setBackgroundColor(status_color);
                    }
                });
            } else {
                final String msg = data;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
