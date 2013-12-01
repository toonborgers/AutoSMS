package be.borgers.autosms;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import be.borgers.autosms.domain.AutoSMSEntry;

import static be.borgers.autosms.MainActivity.ACTION_SMS_SENT;

public class SMSEntryAdapter extends ArrayAdapter<AutoSMSEntry> {
    private List<AutoSMSEntry> items;
    private LayoutInflater layoutInflater;

    public SMSEntryAdapter(Context context, List<AutoSMSEntry> items) {
        super(context, R.layout.entries_item, items);
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.entries_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nameView = (TextView) rowView.findViewById(R.id.tv_entry_naam);
            viewHolder.button = (ImageButton) rowView.findViewById(R.id.bt_entry_send);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        final AutoSMSEntry item = items.get(position);
        holder.nameView.setText(item.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendSMSTextTask().execute(item);
            }
        });
        return rowView;
    }

    private static final class ViewHolder {
        private TextView nameView;
        private ImageButton button;
    }

    private class SendSMSTextTask extends AsyncTask<AutoSMSEntry, Void, Void> {

        @Override
        protected Void doInBackground(AutoSMSEntry... smsEntries) {
            AutoSMSEntry entry = smsEntries[0];
            SmsManager.getDefault().sendTextMessage(
                    entry.getName(),
                    null,
                    entry.getText(),
                    PendingIntent.getBroadcast(getContext(),0, new Intent(ACTION_SMS_SENT),0),
                    null);

            return null;
        }
    }
}
