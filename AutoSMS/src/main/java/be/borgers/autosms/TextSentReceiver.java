package be.borgers.autosms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TextSentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(
                context,
                getResultCode() == Activity.RESULT_OK ? R.string.textsent : R.string.textsenterror,
                Toast.LENGTH_LONG
        ).show();
    }
}