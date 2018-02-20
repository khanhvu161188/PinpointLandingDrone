package jp.mydns.diams.pinpointlandingdrone.layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import jp.mydns.diams.pinpointlandingdrone.Common.PinpointLandingDroneApplication;
import jp.mydns.diams.pinpointlandingdrone.MainActivity;
import jp.mydns.diams.pinpointlandingdrone.R;

public class BeaconFragment extends Fragment {

    private View view;

    private TableLayout mRssi_tbl;
    private TextView mProximityId_txt, mTrueFalse_txt;

    private String mProximityBeacon;
    private boolean mSignficant;
    private List<String> mBeacons;
    private Map<String, Integer> mRSSI;
    private FlightController mFlightController;
    private RedrawHandler mRedraw;

    public BeaconFragment() {
        // Required empty public constructor
    }

    public static BeaconFragment newInstance() {
        BeaconFragment fragment = new BeaconFragment();
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
        view = inflater.inflate(R.layout.fragment_beacon, container, false);

        // RSSIテーブルを生成
        mRssi_tbl = (TableLayout) view.findViewById(R.id.beacon_rssi_table);
        //// 登録済みのビーコンを読み込む
        mBeacons = new ArrayList<>();
        SharedPreferences pref = getActivity().getSharedPreferences("registered_beacon", getActivity().MODE_PRIVATE);
        for (int i = 1; true; i++) {
            String beaconID = pref.getString(String.format("beacon%d", i), "");
            if (beaconID.equals("")) {
                break;
            }
            mBeacons.add(beaconID);
        }
        //// 登録ビーコンの数だけ行を追加する
        for (int i = 0; i < mBeacons.size(); i++) {
            // 行を生成
            TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.beacon_row, null);
            // ID用のTextViewを生成
            TextView id_txt = (TextView) row.findViewById(R.id.row_id_textView);
            id_txt.setText(mBeacons.get(i));
            // RSSI用のTextViewを生成
            TextView rssi_txt = (TextView) row.findViewById(R.id.row_rssi_textView);
            rssi_txt.setText("--［dBm］");
            // 行をテーブルに追加
            mRssi_tbl.addView(row);
        }

        mProximityId_txt = (TextView) view.findViewById(R.id.beacon_proximityId_textView);
        mTrueFalse_txt = (TextView) view.findViewById(R.id.beacon_truefalse_textView);

        mProximityBeacon = "";
        mSignficant = false;
        mRSSI = new HashMap<>();
        for (String beacon_id : mBeacons) {
            mRSSI.put(beacon_id, 1);
        }

        initFlightController();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRedraw = new RedrawHandler();
        mRedraw.sleep(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRedraw = null;
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

            String[] data_arr = data.split(",");
            for (int i = 0; i < mBeacons.size(); i++) {
                mRSSI.put(mBeacons.get(i), Integer.parseInt(data_arr[i]));
            }
            mProximityBeacon = data_arr[data_arr.length - 2];
            mSignficant = Boolean.parseBoolean(data_arr[data_arr.length - 1]);
        }
    };

    private class RedrawHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            for (int i = 1; i < mRssi_tbl.getChildCount(); i++) {
                TableRow row = (TableRow) mRssi_tbl.getChildAt(i);
                TextView id_txt = (TextView) row.findViewById(R.id.row_id_textView);
                TextView rssi_txt = (TextView) row.findViewById(R.id.row_rssi_textView);
                int rssi = mRSSI.get(id_txt.getText());
                if (rssi > 0) {
                    rssi_txt.setText("--［dBm］");
                } else {
                    rssi_txt.setText(mRSSI.get(id_txt.getText()) + "［dBm］");
                }
            }

            mProximityId_txt.setText(mProximityBeacon);
            if (mSignficant) {
                mTrueFalse_txt.setText("あり");
                mTrueFalse_txt.setBackgroundColor(Color.GREEN);
            } else {
                mTrueFalse_txt.setText("なし");
                mTrueFalse_txt.setBackgroundColor(Color.WHITE);
            }

            if (mRedraw != null) {
                mRedraw.sleep(100);
            }
        }

        public void sleep(long delay) {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delay);
        }
    }
}
