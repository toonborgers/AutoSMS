package be.borgers.autosms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import be.borgers.autosms.domain.AutoSMSEntry;

public class SendSMSTask extends AsyncTask<AutoSMSEntry, Void, Void> {
    public static final String ACTION_SMS_SENT = "be.borgers.autosms.SMS_SENT";

    private final PendingIntent pendingIntent;

    public SendSMSTask(Context ctx) {
        pendingIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_SMS_SENT), 0);
    }

    @Override
    protected Void doInBackground(AutoSMSEntry... smsEntries) {
        AutoSMSEntry entry = smsEntries[0];
        SmsManager.getDefault().sendTextMessage(
                entry.getNumber(),
                null,
                entry.getText(),
                pendingIntent,
                null);

        return null;
    }
}
