package jp.mydns.diams.pinpointlandingdrone.layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jp.mydns.diams.pinpointlandingdrone.MainActivity;
import jp.mydns.diams.pinpointlandingdrone.R;

public class SettingFragment extends Fragment {

    private View view;

    private Button mBeacon_btn;

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
}
