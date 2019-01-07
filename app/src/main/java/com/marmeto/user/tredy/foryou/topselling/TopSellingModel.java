package com.marmeto.user.tredy.foryou.topselling;

import android.annotation.SuppressLint;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marmeto.user.tredy.R;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class TopSellingModel extends BaseObservable implements Serializable {

        private String Product_ID;
    private String Product_title;
    private String imageUrl;

    private String price;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(String product_ID) {
        Product_ID = product_ID;
    }

    public String getProduct_title() {
        return Product_title;
    }

    public void setProduct_title(String product_title) {
        Product_title = product_title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getCollectionid() {
        return collectionid;
    }

    public void setCollectionid(String collectionid) {
        this.collectionid = collectionid;
    }

   private Storefront.Collection collection;
    private Storefront.Product product;
    private String collectionTitle,id, collectionid;

    public TopSellingModel(String product_ID, String product_title, String Price,String imageUrl ,String CollectionTitle ) {
        Product_ID = product_ID;
        Product_title = product_title;
        price = Price;
        this.imageUrl = imageUrl;
        collectionTitle=CollectionTitle;
    }

    public TopSellingModel() {
    }

    public TopSellingModel(Storefront.Product product, String collectionTitle, String id) {
        this.product = product;
        this.collectionTitle = collectionTitle;
        this.id=id;
    }

    public Storefront.Product getProduct() {
        return product;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public String getId() {
        return id;
    }

    public Storefront.Collection getCollection() {
        return collection;
    }

    public void setCollection(Storefront.Collection collection) {
        this.collection = collection;
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("price1")
    public static void price(TextView textView, String price) {
        textView.setText("₹ " + price);
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if(imageUrl.trim().length()>0) {
                Picasso.with(view.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(view);
        }else{
            Picasso.with(view.getContext())
                    .load(R.drawable.ic_placeholder)
                    .into(view);
        }
    }


}