package be.borgers.autosms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;
import de.timroes.android.listview.EnhancedListView;

public class MainActivity extends Activity {
    private static final int REQUEST_ADD = 1;

    private SMSEntryDBHelper dbHelper;
    private SMSEntryAdapter adapter;
    private EnhancedListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SMSEntryDBHelper(this);
        listView = (EnhancedListView) findViewById(R.id.main_list);

        updateAdapter();
        setUpSwipeyStuff();
    }

    @Override
    protected void onStop() {
        listView.discardUndo();
        super.onStop();
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

    private void updateAdapter() {
        adapter = new SMSEntryAdapter(this, dbHelper.getEntries());
        listView.setAdapter(adapter);
    }

    private void setUpSwipeyStuff() {
        listView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int postition) {
                final AutoSMSEntry item = adapter.getItem(postition);
                adapter.remove(item);
                dbHelper.remove(item);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        adapter.insert(item, postition);
                        dbHelper.addEntry(item);
                    }
                };
            }
        });
        listView.enableSwipeToDismiss();
        listView.setSwipingLayout(R.layout.entries_item);
        listView.setUndoStyle(EnhancedListView.UndoStyle.COLLAPSED_POPUP);
        listView.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
    }

    public static class TextSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(
                    context,
                    getResultCode() == RESULT_OK ? R.string.textsent : R.string.textsenterror,
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
