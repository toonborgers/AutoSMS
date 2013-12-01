package be.borgers.autosms;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;


import be.borgers.autosms.db.SMSEntryDBHelper;

public class MainActivity extends ListActivity {
    private static final int REQUEST_ADD = 1;
    public static final String ACTION_SMS_SENT = "be.borgers.autosms.SMS_SENT";

    private SMSEntryDBHelper dbHelper;
    private SMSEntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new SMSEntryDBHelper(this);
        getListView().setEmptyView(findViewById(android.R.id.empty));
        updateAdapter();
        setUpReceiver();
        setupSwipeToDismiss();
    }

    private void updateAdapter() {
        adapter = new SMSEntryAdapter(this, dbHelper.getEntries());
        getListView().setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            startActivityForResult(new Intent(this, AddActivity.class), REQUEST_ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            updateAdapter();
        }
    }

    private void setUpReceiver() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(
                        MainActivity.this,
                        getResultCode() == RESULT_OK ? R.string.textsent : R.string.textsenterror,
                        Toast.LENGTH_LONG
                ).show();
            }
        }, new IntentFilter(ACTION_SMS_SENT));
    }

    private void setupSwipeToDismiss() {
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        getListView(),
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for(int i : reverseSortedPositions){
                                    dbHelper.remove(adapter.getItem(i));
                                }
                                updateAdapter();
                            }
                        });
        getListView().setOnTouchListener(touchListener);
        getListView().setOnScrollListener(touchListener.makeScrollListener());
    }
}
