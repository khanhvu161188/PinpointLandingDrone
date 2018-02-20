package jp.mydns.diams.pinpointlandingdrone.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * Created by daimon on 2017/03/02.
 */

public class OkCancelDialogFragment extends DialogFragment {

    private String mTitle;
    private String mMessage;
    private DialogInterface.OnClickListener OkClick;
    private DialogInterface.OnClickListener CancelClick;

    public void setOkClickListener(DialogInterface.OnClickListener okClick) {
        OkClick = okClick;
    }

    public void setCancelClickListener(DialogInterface.OnClickListener cancelClick) {
        CancelClick = cancelClick;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mTitle = bundle.getString("title", "");
        mMessage = bundle.getString("message", "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton("OK", OkClick)
                .setNegativeButton("キャンセル", CancelClick)
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();

        dismiss();
    }
}
