package com.tredy.user.tredy.search;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tredy.user.tredy.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class SearchModel implements Serializable {

    private String Product_ID;
    private String price;
    private String Product_title;
    private String imageUrl;

     SearchModel(String product_ID, String price, String product_title, String imageUrl) {
        Product_ID = product_ID;
        this.price = price;
        Product_title = product_title;
        this.imageUrl = imageUrl;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public String getPrice() {
        return price;
    }

    public String getProduct_title() {
        return Product_title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("price1")
    public static void price(TextView textView, String price) {
        textView.setText("₹ " + price);
    }

    @BindingAdapter("imageUrl1")
    public static void loadImage(ImageView view, String imageUrl) {

        if(imageUrl!=null) {
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.trendybanner)
                    .error(R.drawable.trendybanner)
                    .resize(200,200)
                    .into(view);
        } else {
            Picasso.with(view.getContext())
                    .load(R.drawable.trendybanner)
                    .into(view);
        }
    }

}
