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
        super(ctx, SMSContract.DB_NAME, null, 1);
    }

    public void addEntry(AutoSMSEntry entry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMSContract.AutoSMSEntries.COL_NAME, entry.getName());
        contentValues.put(SMSContract.AutoSMSEntries.COL_NUMBER, entry.getNumber());
        contentValues.put(SMSContract.AutoSMSEntries.COL_TEXT, entry.getText());

        getWritableDatabase().insert(SMSContract.AutoSMSEntries.TABLE_NAME, null, contentValues);
    }

    public List<AutoSMSEntry> getEntries() {
        Cursor cursor = getReadableDatabase().query(SMSContract.AutoSMSEntries.TABLE_NAME, null, null, null, null, null, null);
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

    public void remove(AutoSMSEntry item) {
        getWritableDatabase().delete(SMSContract.AutoSMSEntries.TABLE_NAME,
                SMSContract.AutoSMSEntries._ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreate = new StringBuilder()
                .append("CREATE TABLE ")
                .append(SMSContract.AutoSMSEntries.TABLE_NAME)
                .append("(")
                .append(SMSContract.AutoSMSEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(SMSContract.AutoSMSEntries.COL_NAME + " TEXT, ")
                .append(SMSContract.AutoSMSEntries.COL_NUMBER + " TEXT, ")
                .append(SMSContract.AutoSMSEntries.COL_TEXT + " TEXT)")
                .toString();
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
