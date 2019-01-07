package com.marmeto.user.tredy.whislist.whislistDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.whislist.AddWhislistModel;
import com.shopify.buy3.Storefront;

import java.util.ArrayList;
import java.util.List;

public class DBWhislist   extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "shopifyCheckOut1";
    private static final String TABLE_ADDTOCART = "addtowhislist";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_VARIENT_ID = "product_varient_id";
    private static final String COLUMN_PRODUCT_VARIENT_TITLE = "product_varient_title";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_QTY = "qty";
//    private static final String COLUMN_TAG= "tag";
//    private static final String COLUMN_SHIPPING= "shipping";
    private static final String COLUMN_PRODUCT_ID= "product_id";

     Context mContext;


    public DBWhislist(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ADD_TO_CARD = "CREATE TABLE " + TABLE_ADDTOCART + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PRODUCT_NAME + " TEXT,"
                + COLUMN_PRODUCT_VARIENT_ID + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_PRODUCT_VARIENT_TITLE + " TEXT,"
                + COLUMN_PRODUCT_ID + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT" + ")";
        db.execSQL(CREATE_ADD_TO_CARD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_ADD_TO_CART = "DROP TABLE IF EXISTS " + TABLE_ADDTOCART;
        db.execSQL(DROP_ADD_TO_CART);

        // Create tables again
        onCreate(db);
    }


    public void insertToDo(String productid,Storefront.ProductVariant listItem, String Product_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, Product_name);
        values.put(COLUMN_PRODUCT_VARIENT_ID, String.valueOf(listItem.getId()));
        values.put(COLUMN_PRICE, Double.parseDouble(String.valueOf(listItem.getPrice())));
        values.put(COLUMN_PRODUCT_VARIENT_TITLE, listItem.getTitle());
//        values.put(COLUMN_IMAGE_URL, listItem.getImage().getSrc());
        values.put(COLUMN_PRODUCT_ID, productid);
        if(listItem.getImage()==null){
            values.put(COLUMN_IMAGE_URL, R.drawable.ic_placeholder);
        }else {
            values.put(COLUMN_IMAGE_URL, listItem.getImage().getSrc());
        }
//
//        // Inserting Row
        db.insert(TABLE_ADDTOCART, null, values);
        db.close();

//        ((AddRemoveCartItem) mContext).AddCartItem();
    }

    public List<AddWhislistModel> getCartList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<AddWhislistModel> userList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADDTOCART, null);

        if (cursor.moveToFirst()) {
            do {
                AddWhislistModel user = new AddWhislistModel();
                user.setCol_id(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                user.setProduct_name(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
                user.setProduct_varient_id(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_VARIENT_ID)));
                user.setProduct_price(Double.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PRICE))));
                user.setProduct_varient_title(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_VARIENT_TITLE)));
                user.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                user.setProduct_id(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID)));

                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }
    public void deletDuplicates(){
        getWritableDatabase().execSQL("delete from addtowhislist where id not in (SELECT MIN(id ) FROM addtowhislist GROUP BY product_varient_id)");
    }

    public void update(String id, int qty) {
        String qty2;
        SQLiteDatabase db = this.getWritableDatabase();
        String qty1 = "select qty from "
                + TABLE_ADDTOCART        + " where "
                + COLUMN_PRODUCT_VARIENT_ID      + " = "  + "'"   + id  + "'";
        @SuppressLint("Recycle") Cursor  cursor = db.rawQuery(qty1,null);
        if (cursor.moveToFirst()) {
            qty2 =  cursor.getString(cursor.getColumnIndex("qty"));
            Log.e("qty2",""+qty2);
            int quantity= Integer.parseInt(qty2)+qty;
            ContentValues values = new ContentValues();
            values.put(COLUMN_QTY, quantity);

            db.update(TABLE_ADDTOCART, values, COLUMN_PRODUCT_VARIENT_ID + "= '" + id+ "'", null);
        }
        db.close();
    }





    public boolean deleteRow(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ADDTOCART, COLUMN_PRODUCT_VARIENT_ID + "='" + name +"' ;", null) > 0;
    }

    public boolean checkUser(String id) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_PRODUCT_VARIENT_ID + " = ?";

        // selection argument
        String[] selectionArgs = {id};

        Cursor cursor = db.query(TABLE_ADDTOCART, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;

    }
}
