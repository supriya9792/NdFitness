package com.ndfitnessplus.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NDFitnessDB.db";

    private Cursor resultset;
    private ContentValues contentValues;

    private static SQLiteDataBaseHelper dbHelper;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();

    public static synchronized SQLiteDataBaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new SQLiteDataBaseHelper(context);
        }
        return dbHelper;
    }
    private SQLiteDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Company Information table
        db.execSQL("CREATE TABLE `Branch` (\n" +
                "  `Branch_id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `Branch_Name` varchar(500) DEFAULT NULL,\n" +
                "  `Address` varchar(500) DEFAULT NULL,\n" +
                "  `City` bigint(20) DEFAULT NULL,\n" +
                "  `Contact` varchar(500) DEFAULT NULL,\n" +
                "  `Email` varchar(200) DEFAULT NULL,\n" +
                "  `DailyCollection` varchar(500) DEFAULT NULL,\n" +
                "  `MonthlyCollection` varchar(500) DEFAULT NULL,\n" +
                "  `CreatedDate` datetime DEFAULT NULL,\n" +
                "  `UpdatedDate` datetime DEFAULT NULL,\n" +
                "  PRIMARY KEY (`Auto_Id`)\n" +
                ") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;");

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Branch");
        onCreate(db);
    }

    // --------------------------------- Branches/Company Information table ---------------------------------------------------------
    public boolean insertBranches(int branch_AutoID, String branchName, String contact, String email, String Branch,
                                  String city,String dailyCollection,String monthlyCollection) {
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();

        contentValues.put("Branch_Name",branchName);
        contentValues.put("Contact",contact);
        contentValues.put("Email",email);
        contentValues.put("Address",Branch);
        contentValues.put("City",city);
        contentValues.put("DailyCollection",dailyCollection);
        contentValues.put("MonthlyCollection",monthlyCollection);
        contentValues.put("CreatedDate",dateFormat.format(date));

        if (db.insert("Branch", null, contentValues) == -1)
            return false;
        return true;
    }

    public boolean updateBranches(String branch_AutoID, String branchName, String contact, String email, String address,
                                  String city,String dailyCollection,String monthlyCollection) {
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put("Branch_Name",branchName);
        contentValues.put("Contact",contact);
        contentValues.put("Email",email);
        contentValues.put("Address",address);
        contentValues.put("City",city);
        contentValues.put("DailyCollection",dailyCollection);
        contentValues.put("MonthlyCollection",monthlyCollection);
        contentValues.put("UpdatedDate",dateFormat.format(date));
        return db.update("Branch", contentValues, "Branch_id = ?", new String[]{String.valueOf(branch_AutoID)}) != -1;
    }

    public Cursor getBranches() {
        SQLiteDatabase db = this.getReadableDatabase();
        resultset = db.rawQuery("SELECT Branch_id,Branch_Name,Contact,Email,Address,City,DailyCollection,MonthlyCollection from Branch", null);
        return resultset;
    }

    public Cursor getBranchId() {
        SQLiteDatabase db = this.getReadableDatabase();
        resultset = db.rawQuery("SELECT distinct(Branch_id) from Branch ", null);
        return resultset;
    }

    public void deleteBranchTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Branch");
        onCreate(db);
    }

}
