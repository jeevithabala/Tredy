package com.marmeto.user.tredy.bag.cartdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.callback.AddRemoveCartItem;
import com.shopify.buy3.Storefront;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "shopifyCheckOut";
    private static final String TABLE_ADDTOCART = "addtocart";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_VARIENT_ID = "product_varient_id";
    private static final String COLUMN_PRODUCT_VARIENT_TITLE = "product_varient_title";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_QTY = "qty";
    private static final String COLUMN_TAG = "tag";
    private static final String COLUMN_SHIPPING = "shipping";
    private static final String COLUMN_PRODUCT_ID = "product_id";

    private Context mContext;

    private String CREATE_ADD_TO_CARD = "CREATE TABLE " + TABLE_ADDTOCART + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCT_NAME + " TEXT,"
            + COLUMN_PRODUCT_VARIENT_ID + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_PRODUCT_VARIENT_TITLE + " TEXT,"
            + COLUMN_QTY + " INTEGER,"
            + COLUMN_TAG + " TEXT,"
            + COLUMN_SHIPPING + " TEXT,"
            + COLUMN_PRODUCT_ID + " TEXT,"
            + COLUMN_IMAGE_URL + " TEXT" + ")";


    private String DROP_ADD_TO_CART = "DROP TABLE IF EXISTS " + TABLE_ADDTOCART;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_ADD_TO_CARD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_ADD_TO_CART);

        // Create tables again
        onCreate(db);
    }


    public void insertToDo(String productid, Storefront.ProductVariant listItem, int qty, String Product_name, String tag, String shipping) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, Product_name);
        values.put(COLUMN_PRODUCT_VARIENT_ID, String.valueOf(listItem.getId()));
        values.put(COLUMN_PRICE, Double.parseDouble(String.valueOf(listItem.getPrice())));
        values.put(COLUMN_PRODUCT_VARIENT_TITLE, listItem.getTitle()+ " "+String.valueOf(listItem.getSelectedOptions().get(0).getName()));
        values.put(COLUMN_QTY, qty);
        values.put(COLUMN_TAG, tag);
        values.put(COLUMN_SHIPPING, shipping);
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

        ((AddRemoveCartItem) mContext).AddCartItem();
    }

    public List<AddToCart_Model> getCartList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<AddToCart_Model> userList = new ArrayList<AddToCart_Model>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADDTOCART, null);

        if (cursor.moveToFirst()) {
            do {
                AddToCart_Model user = new AddToCart_Model();
                user.setCol_id(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                user.setProduct_name(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
                user.setProduct_varient_id(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_VARIENT_ID)));
                user.setProduct_price(Double.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_PRICE))));
                user.setProduct_varient_title(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_VARIENT_TITLE)));
                user.setQty(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_QTY))));
                user.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                user.setTag(cursor.getString(cursor.getColumnIndex(COLUMN_TAG)));
                user.setProduct_id(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID)));
                user.setShip(cursor.getString(cursor.getColumnIndex(COLUMN_SHIPPING)));

                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public void update(String id, int qty) {
        String qty2 = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String qty1 = "select qty from "
                + TABLE_ADDTOCART + " where "
                + COLUMN_PRODUCT_VARIENT_ID + " = " + "'" + id + "'";
        Cursor cursor = db.rawQuery(qty1, null);
        if (cursor.moveToFirst()) {
            qty2 = cursor.getString(cursor.getColumnIndex("qty"));
            Log.e("qty2", "" + qty2);
            int quantity = Integer.parseInt(qty2) + qty;
            ContentValues values = new ContentValues();
            values.put(COLUMN_QTY, quantity);

            db.update(TABLE_ADDTOCART, values, COLUMN_PRODUCT_VARIENT_ID + "= '" + id + "'", null);
        }
        db.close();
        ((AddRemoveCartItem) mContext).AddCartItem();
    }


    public void deletDuplicates(){
        getWritableDatabase().execSQL("delete from addtocart where id not in (SELECT MIN(id ) FROM addtocart GROUP BY product_varient_id)");
    }

    public void updateshipping(String id, String ship) {
        String qty2 = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String qty1 = "select shipping from "
                + TABLE_ADDTOCART + " where "
                + COLUMN_PRODUCT_VARIENT_ID + " = " + "'" + id + "'";
        Cursor cursor = db.rawQuery(qty1, null);
        if (cursor.moveToFirst()) {
            qty2 = cursor.getString(cursor.getColumnIndex("shipping"));
//            if (qty2.equals("true")){
//                qty2="false";
//            }
            Log.e("value", "" + qty2);

            ContentValues values = new ContentValues();
            values.put(COLUMN_SHIPPING, ship);

            db.update(TABLE_ADDTOCART, values, COLUMN_PRODUCT_VARIENT_ID + "= '" + id + "'", null);
        }
        db.close();
    }

    public void decreaseqty(String id) {
        String qty2 = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String qty1 = "select qty from "
                + TABLE_ADDTOCART + " where "
                + COLUMN_PRODUCT_VARIENT_ID + " = " + "'" + id + "'";
        Cursor cursor = db.rawQuery(qty1, null);
        if (cursor.moveToFirst()) {
            qty2 = cursor.getString(cursor.getColumnIndex("qty"));
            Log.e("qty2", "" + qty2);
            if (Integer.parseInt(qty2) > 1) {
                int quantity = Integer.parseInt(qty2) - 1;
                ContentValues values = new ContentValues();
                values.put(COLUMN_QTY, quantity);

                db.update(TABLE_ADDTOCART, values, COLUMN_PRODUCT_VARIENT_ID + "= '" + id + "'", null);
            }

        }
        db.close();
        ((AddRemoveCartItem) mContext).AddCartItem();
    }


    public String getQuantity(String id) {
        String qty2 = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String qty1 = "select qty from "
                + TABLE_ADDTOCART + " where "
                + COLUMN_PRODUCT_VARIENT_ID + " = " + "'" + id + "'";
        Cursor cursor = db.rawQuery(qty1, null);
        if (cursor.moveToFirst()) {
            qty2 = cursor.getString(cursor.getColumnIndex("qty"));
            Log.e("qty2", "" + qty2);

        }
        db.close();
        return qty2;
    }

//    public void deleteItemFromCart(String ID) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        db.delete(TABLE_ADDTOCART, COLUMN_PRODUCT_VARIENT_ID + "=" + ID, null);
//
//    }

    public boolean deleteRow(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_ADDTOCART, COLUMN_PRODUCT_VARIENT_ID + "='" + name + "' ;", null) > 0;


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

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
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

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public boolean checkProduct(String id) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_PRODUCT_ID + " = ?";

        // selection argument
        String[] selectionArgs = {id};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
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

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public  void deleteCart(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            db.execSQL("DELETE FROM " + DBHelper.TABLE_ADDTOCART);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }
}
