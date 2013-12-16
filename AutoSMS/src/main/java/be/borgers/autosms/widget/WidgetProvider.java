package be.borgers.autosms.widget;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import be.borgers.autosms.SendSMSTask;
import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WidgetConfigure.ACTION.equals(intent.getAction())) {
            if (intent.hasExtra(WidgetConfigure.SELECTED_ITEM)) {
                int id = intent.getIntExtra(WidgetConfigure.SELECTED_ITEM, -1);
                AutoSMSEntry entry = new SMSEntryDBHelper(context).getEntry(id);
                if (entry != null) {
                    new SendSMSTask(context).execute(entry);
                }
            }
        }
        super.onReceive(context, intent);
    }
}
