package be.borgers.autosms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import be.borgers.autosms.domain.AutoSMSEntry;

public class SMSEntryDBHelper extends SQLiteOpenHelper {

    public SMSEntryDBHelper(Context ctx) {
        super(ctx, AutoSMSContract.DB_NAME, null, 1);
    }

    public void addEntry(AutoSMSEntry entry) {
        getWritableDatabase()
                .insert(AutoSMSContract.SMSEntries.TABLE_NAME, null, contentValuesFor(entry));
    }

    public void updateEntry(AutoSMSEntry entry) {
        getWritableDatabase().update(
                AutoSMSContract.SMSEntries.TABLE_NAME,
                contentValuesFor(entry),
                AutoSMSContract.SMSEntries._ID + "=?",
                new String[]{String.valueOf(entry.getId())});
    }

    public List<AutoSMSEntry> getEntries() {
        Cursor cursor = getReadableDatabase().query(AutoSMSContract.SMSEntries.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        List<AutoSMSEntry> result = new ArrayList<AutoSMSEntry>();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            String text = cursor.getString(3);
            result.add(new AutoSMSEntry(id, name, number, text));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public AutoSMSEntry getEntry(int id) {
        Cursor cursor = getReadableDatabase().query(AutoSMSContract.SMSEntries.TABLE_NAME,
                null,
                AutoSMSContract.SMSEntries._ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            int entryId = cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            String text = cursor.getString(3);
            return new AutoSMSEntry(entryId, name, number, text);
        }

        return null;
    }

    public void remove(AutoSMSEntry item) {
        getWritableDatabase().delete(AutoSMSContract.SMSEntries.TABLE_NAME,
                AutoSMSContract.SMSEntries._ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreate = new StringBuilder()
                .append("CREATE TABLE ")
                .append(AutoSMSContract.SMSEntries.TABLE_NAME)
                .append("(")
                .append(AutoSMSContract.SMSEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(AutoSMSContract.SMSEntries.COL_NAME + " TEXT, ")
                .append(AutoSMSContract.SMSEntries.COL_NUMBER + " TEXT, ")
                .append(AutoSMSContract.SMSEntries.COL_TEXT + " TEXT)")
                .toString();
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    private ContentValues contentValuesFor(AutoSMSEntry entry) {
        ContentValues contentValues = new ContentValues();
        if (entry.getId() > 0) {
            contentValues.put(AutoSMSContract.SMSEntries._ID, entry.getId());
        }
        contentValues.put(AutoSMSContract.SMSEntries.COL_NAME, entry.getName());
        contentValues.put(AutoSMSContract.SMSEntries.COL_NUMBER, entry.getNumber());
        contentValues.put(AutoSMSContract.SMSEntries.COL_TEXT, entry.getText());
        return contentValues;
    }
}
