package be.borgers.autosms.async;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import be.borgers.autosms.domain.Contact;

public class GetContactsTask extends AsyncTask<Void, Void, Set<Contact>> {
    private static final String TAG = GetContactsTask.class.getSimpleName();

    private Context context;
    private GetContactsListener listener;

    public GetContactsTask(Activity context, GetContactsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Set<Contact> doInBackground(Void... voids) {
        Map<Long, Contact> contactsTmp = new HashMap<Long, Contact>();
        List<String> contactIds = new ArrayList<String>();
        Cursor contactsCursor = queryContacts();
        for (contactsCursor.moveToFirst(); !contactsCursor.isAfterLast(); contactsCursor.moveToNext()) {
            int indexId = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
            int indexName = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int indexPhotoId = contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);

            Long contactId = contactsCursor.getLong(indexId);
            String contactName = contactsCursor.getString(indexName);
            Long photoId = contactsCursor.getLong(indexPhotoId);
            boolean hasPhoto = photoId != 0l;
            Uri photoUri = null;
            if (hasPhoto) {
                photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
            }
            contactsTmp.put(contactId, new Contact(contactId, contactName, photoUri, hasPhoto));
            contactIds.add(contactId.toString());
        }
        contactsCursor.close();
        filterPlaceholder = filterPlaceholder.substring(1);
        Set<Contact> contacts = new TreeSet<Contact>();
        SetMultimap<Long, String> numbers = getNumbers(contactIds);
        for (Long contactId : numbers.keySet()) {
            Contact contact = contactsTmp.get(contactId);
            for (String number : numbers.get(contactId)) {
                contacts.add(new Contact(contactId, contact.getName(), number, contact.getPhotoUri(), contact.hasPhoto()));
            }
        }

        return contacts;
    }

    @Override
    protected void onPostExecute(Set<Contact> contacts) {
        listener.contactsRetrieved(new ArrayList<Contact>(contacts));
    }

    private Cursor queryContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID
        };
        String filter = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?";
        String[] filterArgs = new String[]{"1"};

        return context.getContentResolver().query(uri, projection, filter, filterArgs, null);
    }

    private SetMultimap<Long, String> getNumbers(List<String> contactIds) {
        SetMultimap<Long, String> result = TreeMultimap.create();
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " in (" + filterPlaceholder(contactIds.size()) + ")",
                contactIds.toArray(new String[contactIds.size()]),
                null);
        phones.moveToFirst();
        for (; !phones.isAfterLast(); phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Long contactId = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            result.put(contactId, number);
        }
        phones.close();

        return result;
    }

    private String filterPlaceholder(int count) {
        String result = "";

        if (count > 0) {
            for (int i = 1; i < count; i++) {
                result += ",?"
            }
            result = result.substring(1);
        }

        return result;
    }

    public interface GetContactsListener {
        void contactsRetrieved(List<Contact> contacts);
    }
}
