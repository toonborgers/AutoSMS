package be.borgers.autosms.widget;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import be.borgers.autosms.R;
import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;

public class WidgetConfigure extends ListActivity {
    static final String ACTION = "be.borgers.autosms.WIDGET_CLICK";
    static final String SELECTED_ITEM = "be.borgers.WIDGET_SELECTED_ITEM";

    private int mAppWidgetId;
    private List<AutoSMSEntry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppWidgetId();
        setUpList();
    }

    private void getAppWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    private void setUpList() {
        entries = new SMSEntryDBHelper(this).getEntries();
        List<String> names = new ArrayList<String>();
        for (AutoSMSEntry entry : entries) {
            names.add(entry.getName());
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                close(position);
            }
        });
    }


    private void close(int position) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.widget_button, clickIntent(getApplicationContext(), position));
        views.setTextViewText(R.id.widget_label, entries.get(position).getName());
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private PendingIntent clickIntent(Context context, int position) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(ACTION);
        intent.putExtra(SELECTED_ITEM, entries.get(position).getId());
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
