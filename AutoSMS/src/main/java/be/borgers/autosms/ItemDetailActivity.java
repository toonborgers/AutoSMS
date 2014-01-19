package be.borgers.autosms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;
import be.borgers.autosms.domain.ParcelableContact;
import butterknife.ButterKnife;
import butterknife.InjectView;

import static be.borgers.autosms.MainActivity.KEY_ITEM_TO_MODIFY_ID;
import static be.borgers.autosms.MainActivity.KEY_REQUEST_TYPE;
import static be.borgers.autosms.MainActivity.REQUEST_ADD;
import static be.borgers.autosms.MainActivity.REQUEST_MODIFY;

public class ItemDetailActivity extends Activity {
    private static final int GET_CONTACT = 1;
    private static final int GET_CONTACTS = 2;

    @InjectView(R.id.add_et_naam)
    EditText et_naam;
    @InjectView(R.id.add_et_nummer)
    EditText et_nummer;
    @InjectView(R.id.add_et_bericht)
    EditText et_bericht;
    @InjectView(R.id.add_bt_store)
    Button storeButton;

    private SMSEntryDBHelper dbHelper;
    private int requestType;
    private AutoSMSEntry entryUnderModification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.inject(this);
        dbHelper = new SMSEntryDBHelper(this);

        if (getIntent().hasExtra(KEY_REQUEST_TYPE)) {
            requestType = getIntent().getIntExtra(KEY_REQUEST_TYPE, REQUEST_ADD);
            if (requestType == REQUEST_MODIFY) {
                int entryUnderModificationId = getIntent().getIntExtra(KEY_ITEM_TO_MODIFY_ID, -1);
                entryUnderModification = dbHelper.getEntry(entryUnderModificationId);
                updateUIForModify();
            }
        }

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeItem();
            }
        });
    }

    private void updateUIForModify() {
        storeButton.setText(R.string.add_modify_item);
        et_naam.setText(entryUnderModification.getName());
        et_bericht.setText(entryUnderModification.getText());
        et_nummer.setText(entryUnderModification.getNumber());
    }

    void storeItem() {
        String naam = et_naam.getText().toString();
        String nummer = et_nummer.getText().toString();
        String bericht = et_bericht.getText().toString();

        if (naam.isEmpty() || nummer.isEmpty() || bericht.isEmpty()) {
            Toast.makeText(ItemDetailActivity.this, R.string.add_fill_in_all_fields, Toast.LENGTH_SHORT).show();
        } else {
            storeAndReturn(naam, nummer, bericht);
        }
    }

    private void storeAndReturn(String name, String number, String text) {
        if (requestType == REQUEST_ADD) {
            dbHelper.addEntry(new AutoSMSEntry(name, number, text));
        } else {
            dbHelper.updateEntry(new AutoSMSEntry(entryUnderModification.getId(), name, number, text));
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, GET_CONTACT);
        } else {
            startActivityForResult(new Intent(this, SelectContactsActivity.class), GET_CONTACTS);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_CONTACT) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    if (cursor.getInt(index) == 1) {
                        index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        et_naam.setText(cursor.getString(index));

                        index = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                        getNumber(cursor.getInt(index));
                    }
                }
                cursor.close();
            } else if (requestCode == GET_CONTACTS) {
                if (data.hasExtra(SelectContactsActivity.SELECTED_CONTACTS)) {
                    for (Parcelable parcelable : data.getParcelableArrayListExtra(SelectContactsActivity.SELECTED_CONTACTS)) {
                        Log.e("XXXX", ((ParcelableContact) parcelable).getContact().toString());
                    }
                }
            }
        }
    }

    private void getNumber(int contactId) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone._ID + " = " + contactId,
                null,
                null);
        final String[] numbers = new String[phones.getCount()];
        int pos = 0;
        phones.moveToFirst();
        while (!phones.isAfterLast()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numbers[pos++] = number;
            phones.moveToNext();
        }
        phones.close();

        if (numbers.length == 1) {
            et_nummer.setText(numbers[0]);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.add_select_number)
                    .setSingleChoiceItems(numbers, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            et_nummer.setText(numbers[i]);
                        }
                    }).show();
        }
    }
}
