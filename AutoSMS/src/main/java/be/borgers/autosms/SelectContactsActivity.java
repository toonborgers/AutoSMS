package be.borgers.autosms;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Function;

import java.util.List;

import be.borgers.autosms.async.GetContactsTask;
import be.borgers.autosms.domain.Contact;
import be.borgers.autosms.domain.ParcelableContact;

public class SelectContactsActivity extends ListActivity implements GetContactsTask.GetContactsListener {
    public static final String SELECTED_CONTACTS = "SELECTED_CONTACTS";

    private SelectContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetContactsTask(this, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.contacts_done) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(SELECTED_CONTACTS, adapter.getSelectedItems());
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Function<? super Contact, ParcelableContact> toParcelableContact() {
        return new Function<Contact, ParcelableContact>() {
            @Override
            public ParcelableContact apply(Contact contact) {
                return new ParcelableContact(contact);
            }
        };
    }

    @Override
    public void contactsRetrieved(List<Contact> contacts) {
        adapter = new SelectContactsAdapter(this, contacts);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        adapter.toggleSelection(position);
    }
}
