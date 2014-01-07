package be.borgers.autosms;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import be.borgers.autosms.async.GetContactsTask;
import be.borgers.autosms.domain.Contact;

public class SelectContactsActivity extends ListActivity implements GetContactsTask.GetContactsListener {

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void contactsRetrieved(List<Contact> contacts) {
        setListAdapter(new SelectContactsAdapter(this, contacts));
    }
}
