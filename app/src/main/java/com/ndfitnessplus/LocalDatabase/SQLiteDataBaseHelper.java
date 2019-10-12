package com.ndfitnessplus.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ndfitnessplus.Activity.POSProductListActivity;
import com.ndfitnessplus.Model.POSItemList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.ndfitnessplus.Activity.POSProductListActivity.TAG;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymtimeDB";

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
    public SQLiteDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Company Information table
        db.execSQL("CREATE TABLE `Cart` (\n" +
                "  `Auto_Id`  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "  `Product_Id` INTEGER DEFAULT NULL ,\n" +
                "  `Product_Code` TEXT DEFAULT NULL,\n" +
                "  `Product_Name` TEXT DEFAULT NULL,\n" +
                "  `Product_Desription` TEXT DEFAULT NULL,\n" +
                "  `Product_Image` TEXT DEFAULT NULL,\n" +
                "  `Company_Id` TEXT DEFAULT NULL,\n" +
                "  `Quantity` TEXT DEFAULT NULL,\n" +
                "  `SelectedQuantity` TEXT DEFAULT NULL,\n" +
                "  `Rate` TEXT DEFAULT NULL,\n" +
                "  `Tax` TEXT DEFAULT NULL,\n" +
                "  `MaxDiscount` TEXT DEFAULT NULL,\n" +
                "  `Purches_Amount` TEXT DEFAULT NULL,\n" +
                "  `CreatedDate` DATETIME DEFAULT NULL,\n" +
                "  `UpdatedDate` DATETIME DEFAULT NULL\n" +
                ")");

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
        db.execSQL("DROP TABLE IF EXISTS Cart");
        onCreate(db);
    }

    // --------------------------------- Cartes/Company Information table ---------------------------------------------------------
//    public long insertCartes(String productid, String prodCode, String prodName, String prodDisc, String pImage,
//                                  String compid,String quantity,String rate,String Tax,String MexDisc,String pAmount) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        contentValues = new ContentValues();
//
//        contentValues.put("Product_Id",productid);
//        contentValues.put("Product_Code",prodCode);
//        contentValues.put("Product_Name",prodName);
//        contentValues.put("Product_Desription",prodDisc);
//        contentValues.put("Product_Image",pImage);
//        contentValues.put("Company_Id",compid);
//        contentValues.put("Quantity",quantity);
//        contentValues.put("Rate",rate);
//        contentValues.put("Tax",Tax);
//        contentValues.put("MaxDiscount",MexDisc);
//        contentValues.put("Purches_Amount",pAmount);
//        contentValues.put("CreatedDate",dateFormat.format(date));
//
//        long id1 = db.insert("Cart", null, contentValues);
//
//        // close db connection
//        db.close();
//
//        // return newly inserted row id
//        return id1;
//    }
    public long insertCartes(String productid, String prodCode, String prodName, String prodDisc, String pImage,
                                  String compid,String quantity,String selqty,String rate,String Tax,String MexDisc,String pAmount) {

        String sql="SELECT * FROM `Cart` WHERE `Product_Code`='" +prodCode+"'";
        SQLiteDatabase sQLiteDatabase2 = getReadableDatabase();
        Cursor cursor = sQLiteDatabase2.rawQuery(sql, null);
        cursor.moveToFirst();

        Log.d("Cursor Count", String.valueOf(cursor.getCount()));
        if (cursor.getCount() == 0) {
            SQLiteDatabase sQLiteDatabase = getWritableDatabase();
            this.contentValues = new ContentValues();
            contentValues.put("Product_Id",productid);
            contentValues.put("Product_Code",prodCode);
            contentValues.put("Product_Name",prodName);
            contentValues.put("Product_Desription",prodDisc);
            contentValues.put("Product_Image",pImage);
            contentValues.put("Company_Id",compid);
            contentValues.put("Quantity",quantity);
            contentValues.put("SelectedQuantity",selqty);
            contentValues.put("Rate",rate);
            contentValues.put("Tax",Tax);
            contentValues.put("MaxDiscount",MexDisc);
            contentValues.put("Purches_Amount",pAmount);
            contentValues.put("CreatedDate",dateFormat.format(date));
            long l = sQLiteDatabase.insert("Cart", null, this.contentValues);
            sQLiteDatabase.close();
            sQLiteDatabase2.close();
            return l;
        }
        return 0;
    }
    public boolean updateCartes(int productid, String prodCode, String prodName, String prodDisc, String pImage,
                                String compid,String quantity,String rate,String Tax,String MexDisc,String pAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();

        contentValues.put("Product_Id",productid);
        contentValues.put("Product_Code",prodCode);
        contentValues.put("Product_Name",prodName);
        contentValues.put("Product_Desription",prodDisc);
        contentValues.put("Product_Image",pImage);
        contentValues.put("Company_Id",compid);
        contentValues.put("Quantity",quantity);
        contentValues.put("SelectedQuantity",quantity);
        contentValues.put("Rate",rate);
        contentValues.put("Tax",Tax);
        contentValues.put("MaxDiscount",MexDisc);
        contentValues.put("Purches_Amount",pAmount);
        contentValues.put("UpdatedDate",dateFormat.format(date));
        return db.update("Cart", contentValues, "Product_Id = ?", new String[]{String.valueOf(productid)}) != -1;
    }

    public Cursor getCartes() {
        SQLiteDatabase db = this.getReadableDatabase();
        resultset = db.rawQuery("SELECT Auto_Id,Product_Code,Product_Name,Product_Desription,Product_Image,Company_Id,Quantity,Rate,Tax,MaxDiscount,Purches_Amount" +
                " from Cart", null);
        return resultset;
    }

    public Cursor getCartId() {
        SQLiteDatabase db = this.getReadableDatabase();
        resultset = db.rawQuery("SELECT distinct(Auto_Id) from Cart ", null);
        return resultset;
    }

    public void updateQuantity(String qty,String prodCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE  Cart set SelectedQuantity='"+qty+"' where Product_Code='"+prodCode+"'";
        db.execSQL(sql );
        db.close();
    }
    public ArrayList<POSItemList> getAllCartProduct() {
        ArrayList<POSItemList> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM  Cart"  ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    POSItemList note = new POSItemList();


                    note.setAutoId(String.valueOf(cursor.getInt(cursor.getColumnIndex("Product_Id"))));
                    note.setProductName(cursor.getString(cursor.getColumnIndex("Product_Name")));
                    note.setProductCode(cursor.getString(cursor.getColumnIndex("Product_Code")));
                    note.setProductDisc(cursor.getString(cursor.getColumnIndex("Product_Desription")));
                    note.setTax(cursor.getString(cursor.getColumnIndex("Tax")));
                    note.setMaxDiscount(cursor.getString(cursor.getColumnIndex("MaxDiscount")));
                    note.setQuantity(cursor.getString(cursor.getColumnIndex("Quantity")));
                    note.setSelectedQuantity(cursor.getString(cursor.getColumnIndex("SelectedQuantity")));
                    String ttl = "₹ " + cursor.getString(cursor.getColumnIndex("Rate"));
                    note.setRate(ttl);
                    note.setProductFinalRate(ttl);
                    note.setPurchaseAmount(cursor.getString(cursor.getColumnIndex("Purches_Amount")));
                    note.setProductImage(cursor.getString(cursor.getColumnIndex("Product_Image")));

                    notes.add(note);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){}

        // close db connection
        db.close();

        // return notes list
        return notes;
    }
    public int remove_from_cart(ArrayList<POSItemList> cartarrayList) {
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues values = new ContentValues();
        // values.put("sv_user_withdrawd_amount", withdraw_amt);
        System.out.print("Remove From Cart");
       for(int i=0;i<cartarrayList.size();i++){
           Log.v(TAG, String.format("doInBackground ::Auto Id = %s", cartarrayList.get(i).getAutoId()));
           Log.v(TAG, String.format("doInBackground ::ProdCode = %s", cartarrayList.get(i).getProductCode()));
           Log.v(TAG, String.format("doInBackground ::Product naame = %s", cartarrayList.get(i).getProductName()));
           String sql ="Delete From Cart WHERE Product_Id ='"+cartarrayList.get(i).getAutoId()+"'";
           db.execSQL(sql);
       }
        db.close();


        System.out.print("removed");
        // return notes list
        return 1;
        // updating row
        //return db.update(TABLE_NAME_SVUSER, values, "sv_user_id" + " = ?",
        //     new String[]{String.valueOf(user_id)});

    }
    public void remove_on_swipe(String paramString) {
        SQLiteDatabase sQLiteDatabase = getWritableDatabase();
        System.out.print("Remove From Cart");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Delete From Cart WHERE Product_Code ='");
        stringBuilder.append(paramString);
        stringBuilder.append("'");
        sQLiteDatabase.execSQL(stringBuilder.toString());
        sQLiteDatabase.close();
        System.out.print("removed");
    }
}
