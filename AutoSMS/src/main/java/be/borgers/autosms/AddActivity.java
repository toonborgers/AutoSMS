package be.borgers.autosms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import be.borgers.autosms.db.SMSEntryDBHelper;
import be.borgers.autosms.domain.AutoSMSEntry;

public class AddActivity extends Activity {
    private static final int GET_CONTACT = 1;

    private EditText et_naam;
    private EditText et_nummer;
    private EditText et_bericht;
    private SMSEntryDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        dbHelper = new SMSEntryDBHelper(this);

        et_naam = (EditText) findViewById(R.id.et_naam);
        et_nummer = (EditText) findViewById(R.id.et_nummer);
        et_bericht = (EditText) findViewById(R.id.et_bericht);
        Button storeButton = (Button) findViewById(R.id.store);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String naam = et_naam.getText().toString();
                String nummer = et_nummer.getText().toString();
                String bericht = et_bericht.getText().toString();

                if (naam.isEmpty() || nummer.isEmpty() || bericht.isEmpty()) {
                    Toast.makeText(AddActivity.this, "Alle velden invullen aub", Toast.LENGTH_SHORT).show();
                } else {
                    storeAndReturn(naam, nummer, bericht);
                }
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_CONTACT && resultCode == Activity.RESULT_OK) {
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
                    .setTitle(R.string.selectnumber)
                    .setSingleChoiceItems(numbers, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            et_nummer.setText(numbers[i]);
                        }
                    }).show();
        }
    }

    private void storeAndReturn(String name, String number, String text) {
        dbHelper.addEntry(new AutoSMSEntry(name, number, text));
        finish();
    }
}
