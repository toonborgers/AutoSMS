package be.borgers.autosms.db;

import android.provider.BaseColumns;

public class AutoSMSContract {
    public static final String DB_NAME = "SMSDB";

    public static final class SMSEntries implements BaseColumns {
        public static final String TABLE_NAME = "entries";
        public static final String COL_NAME = "name";
        public static final String COL_NUMBER = "number";
        public static final String COL_TEXT = "text";
    }
}
