package com.tredy.user.tredy.foryou.newarrival;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.tredy.user.tredy.R;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Date;

public class NewArrivalModel implements Serializable, Comparable<NewArrivalModel>  {

    private Storefront.Product product;
    private Storefront.Collection collection;
    private String collectionTitle;

    public NewArrivalModel() {
    }
    private String price,Product_ID,Product_title,imageUrl;
    private String  collectionid;



    public NewArrivalModel(String product_ID, String product_title, String Price,String imageUrl ,String CollectionTitle ) {
        Product_ID = product_ID;
        Product_title = product_title;
        price = Price;
        this.imageUrl = imageUrl;
        collectionTitle=CollectionTitle;
    }

    public String getPrice() {
        return price;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public String getProduct_title() {
        return Product_title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCollectionid() {
        return collectionid;
    }

    public void setCollectionid(String collectionid) {
        this.collectionid = collectionid;
    }


    public NewArrivalModel(Storefront.Product product, String collectionTitle) {
        this.product = product;
        this.collectionTitle = collectionTitle;
    }

    public Storefront.Collection getCollection() {
        return collection;
    }

    public void setCollection(Storefront.Collection collection) {
        this.collection = collection;
    }

    public Storefront.Product getProduct() {
        return product;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("price")
    public static void price(TextView textView, String price)
    {
        textView.setText("₹ "+price);
    }
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if(imageUrl.trim().length()>0) {
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(view);
        }else {
            Picasso.with(view.getContext())
                    .load(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(view);
        }
    }

    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }



    @Override
    public int compareTo(@NonNull NewArrivalModel newArrivalModel) {
        return getDateTime().compareTo(newArrivalModel.getDateTime());

    }
}
