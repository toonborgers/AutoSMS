package be.borgers.autosms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.timroes.android.listview.EnhancedListView;

public class MainActivity extends Activity {
    static final String KEY_REQUEST_TYPE = "request_type";
    static final String KEY_ITEM_TO_MODIFY_ID = "item_to_modify";

    static final int REQUEST_ADD = 1;
    static final int REQUEST_MODIFY = 2;

    @InjectView(R.id.main_list)
    EnhancedListView listView;
    private SMSEntryDBHelper dbHelper;
    private SMSEntryAdapter adapter;
    private ActionMode actionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        dbHelper = new SMSEntryDBHelper(this);

        updateAdapter();
        setUpSwipeyStuff();
        setUpCABStuff();
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
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(KEY_REQUEST_TYPE, REQUEST_ADD);
            startActivityForResult(intent, REQUEST_ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD || requestCode == REQUEST_MODIFY) {
                updateAdapter();
            }
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

    private void modify(AutoSMSEntry autoSMSEntry) {
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(KEY_REQUEST_TYPE, REQUEST_MODIFY);
        intent.putExtra(KEY_ITEM_TO_MODIFY_ID, autoSMSEntry.getId());
        startActivityForResult(intent, REQUEST_MODIFY);
    }

    private void remove(List<AutoSMSEntry> entries) {
        for (AutoSMSEntry entry : entries) {
            adapter.remove(entry);
            dbHelper.remove(entry);
        }
    }

    private void setUpCABStuff() {
        listView.setOnItemLongClickListener(new ListLongClickListener());
        listView.setOnItemClickListener(new ListClickListener());
    }

    private void itemClicked(int position) {
        adapter.toggleSelection(position);
        boolean hasSelectedItems = adapter.getSelectedCount() > 0;

        if (hasSelectedItems && actionMode == null) {
            actionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
        } else if (!hasSelectedItems && actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }

        if (actionMode != null) {
            Menu menu = actionMode.getMenu();
            if (adapter.getSelectedCount() > 1) {
                menu.findItem(R.id.main_cab_modify).setVisible(false);
            } else {
                menu.findItem(R.id.main_cab_modify).setVisible(true);
            }
        }
    }

    private class ListLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            itemClicked(position);
            return true;
        }
    }

    private class ListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (actionMode != null) {
                itemClicked(position);
            }
        }
    }

    private class ActionBarCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            listView.disableSwipeToDismiss();
            actionMode.getMenuInflater().inflate(R.menu.main_context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.main_cab_modify) {
                modify(adapter.getSelectedItems().get(0));
                actionMode.finish();
                return true;
            } else if (menuItem.getItemId() == R.id.main_cab_delete) {
                remove(adapter.getSelectedItems());
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.clearSelection();
            listView.enableSwipeToDismiss();
        }
    }

}
