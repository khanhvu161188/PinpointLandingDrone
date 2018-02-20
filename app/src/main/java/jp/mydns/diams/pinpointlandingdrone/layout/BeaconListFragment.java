package jp.mydns.diams.pinpointlandingdrone.layout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.mydns.diams.pinpointlandingdrone.MainActivity;
import jp.mydns.diams.pinpointlandingdrone.R;

public class BeaconListFragment extends Fragment {

    private View view;

    private EditText mNewBeacon_edt;
    private Button mRegist_btn;

    private ListView mBeacon_lst;

    private Button mComplete_btn;

    List<String> mBeacons;

    public BeaconListFragment() {
        // Required empty public constructor
    }

    public static BeaconListFragment newInstance(String param1, String param2) {
        BeaconListFragment fragment = new BeaconListFragment();
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
        view = inflater.inflate(R.layout.fragment_beacon_list, container, false);

        mNewBeacon_edt = (EditText) view.findViewById(R.id.list_newBeacon_editText);
        mRegist_btn = (Button) view.findViewById(R.id.list_regist_button);
        mRegist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newBeaconID;
                if (mNewBeacon_edt.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "ビーコンIDを入力してください", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    newBeaconID = mNewBeacon_edt.getText().toString();
                }

                if (newBeaconID.indexOf("BA") != 0 && newBeaconID.indexOf("BB") != 0 || newBeaconID.length() != 8) {
                    Toast.makeText(getContext(), "ビーコンIDの入力値に不備があります", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mBeacons.contains(newBeaconID)) {
                    Toast.makeText(getContext(), "すでに登録されています", Toast.LENGTH_LONG).show();
                } else {
                    mBeacons.add(newBeaconID);
                }

                updateListView();

                mNewBeacon_edt.setText("");
            }
        });

        mBeacon_lst = (ListView) view.findViewById(R.id.list_beacon_listView);
        mBeacon_lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "登録ビーコンの取消し";
                final int position = i;
                String beaconID = (String) mBeacon_lst.getItemAtPosition(i);
                String message = String.format("%sの登録を取り消します", beaconID);

                OkCancelDialogFragment okCancelDialogFragment = new OkCancelDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("message", message);
                okCancelDialogFragment.setArguments(bundle);
                okCancelDialogFragment.setOkClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBeacons.remove(position);
                        updateListView();
                    }
                });

                okCancelDialogFragment.show(getFragmentManager(), OkCancelDialogFragment.class.getName());
            }
        });

        mComplete_btn = (Button) view.findViewById(R.id.list_complete_button);
        mComplete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getActivity().getSharedPreferences("registered_beacon", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor edi = pref.edit();
                for (int i = 0; i < mBeacons.size(); i++) {
                    edi.putString(String.format("beacon%d", i+1), mBeacons.get(i));
                }
                edi.putString(String.format("beacon%d", mBeacons.size() + 1), "");
                edi.commit();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                SettingFragment nextFragment = new SettingFragment();
                transaction.replace(R.id.fragment_container, nextFragment, SettingFragment.class.getName());
                transaction.commit();
            }
        });

        mBeacons = new ArrayList<>();
        SharedPreferences pref = getActivity().getSharedPreferences("registered_beacon", getActivity().MODE_PRIVATE);
        for (int i = 1; true; i++) {
            String beaconID = pref.getString(String.format("beacon%d", i), "");
            if (beaconID.equals("")) {
                break;
            }
            mBeacons.add(beaconID);
        }
        updateListView();

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

    private void updateListView() {
        String[] beacons = mBeacons.toArray(new String[mBeacons.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, beacons);
        mBeacon_lst.setAdapter(adapter);
    }
}
