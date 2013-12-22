package be.borgers.autosms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.timroes.android.listview.EnhancedListView;

public class MainActivity extends Activity {
    private static final int REQUEST_ADD = 1;

    @InjectView(R.id.main_list)
    EnhancedListView listView;
    private SMSEntryDBHelper dbHelper;
    private SMSEntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        dbHelper = new SMSEntryDBHelper(this);

        updateAdapter();
        setUpSwipeyStuff();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
        listView.setSwipingLayout(R.layout.main_list_item);
        listView.setUndoStyle(EnhancedListView.UndoStyle.COLLAPSED_POPUP);
        listView.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
    }
}
