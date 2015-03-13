package in.ac.iiitd.zenatix.billpredict.Database;

import android.provider.BaseColumns;

/**
 * Created by vedantdasswain on 03/03/15.
 */
public class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    /* Inner class that defines the table contents */
    public static abstract class WaterEntry implements BaseColumns {
        public static final String TABLE_NAME = "Water_Meter";
        public static final String METER_READING = "Meter_Reading";
        public static final String READING_DATE = "Meter_Date";
        public static final String CYCLE_START_ID = "Cycle_Start_ID";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DatabaseContract.WaterEntry.TABLE_NAME + " (" +
                        DatabaseContract.WaterEntry._ID + " INTEGER PRIMARY KEY," +
                        DatabaseContract.WaterEntry.METER_READING + REAL_TYPE + COMMA_SEP +
                        DatabaseContract.WaterEntry.READING_DATE + TEXT_TYPE +COMMA_SEP +
                        DatabaseContract.WaterEntry.CYCLE_START_ID+ INT_TYPE+
                        " )";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DatabaseContract.WaterEntry.TABLE_NAME;

    }

    public static abstract class ElectricityEntry implements BaseColumns {
        public static final String TABLE_NAME = "Electricity_Meter";
        public static final String METER_READING = "Meter_Reading";
        public static final String READING_DATE = "Meter_Date";
        public static final String CYCLE_START_ID = "Cycle_Start_ID";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DatabaseContract.ElectricityEntry.TABLE_NAME + " (" +
                        DatabaseContract.ElectricityEntry._ID + " INTEGER PRIMARY KEY," +
                        DatabaseContract.ElectricityEntry.METER_READING + REAL_TYPE + COMMA_SEP +
                        DatabaseContract.ElectricityEntry.READING_DATE + TEXT_TYPE +COMMA_SEP +
                        DatabaseContract.ElectricityEntry.CYCLE_START_ID+ INT_TYPE+
                        " )";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DatabaseContract.ElectricityEntry.TABLE_NAME;
    }



}

