package com.example.user.trendy.foryou.groceryhome;

import android.databinding.BindingAdapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.user.trendy.R;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class GroceryHomeModel implements Serializable {

    public Storefront.Collection collection;
    public Storefront.Product product;
    public String qty;
    public String title;


    public GroceryHomeModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Storefront.Product getProduct() {
        return product;
    }

    public void setProduct(Storefront.Product product) {
        this.product = product;
    }

    public Storefront.Collection getCollection() {
        return collection;
    }

    public void setCollection(Storefront.Collection collection) {
        this.collection = collection;
    }

    @BindingAdapter("productname1")
    public static void productname(TextView textView, Storefront.Product product) {
        if (product != null) {
            String name = product.getTitle();
            textView.setText(name);
        }
    }

    @BindingAdapter("cost1")
    public static void productcost(TextView textView, Storefront.Product product) {
        if (product != null) {
            String cost = String.valueOf("₹ " + product.getVariants().getEdges().get(0).getNode().getPrice());
            textView.setText(cost);
        }
    }

    @BindingAdapter("weight1")
    public static void weight(TextView textView, Storefront.Product product) {
        if (product != null) {
            String c = "";
            if (product.getOptions() != null) {
                if (product.getOptions().size() == 1) {
                    c = String.valueOf(product.getOptions().get(0).getName());
                } else {
                    for (int i = 0; i < product.getOptions().size(); i++) {
                        c = c + String.valueOf(product.getOptions().get(i).getName()) + " / ";
                    }
                }
                String cost = String.valueOf(c);
                textView.setText(cost);
            }
        }
    }

    @BindingAdapter("imageg1")
    public static void loadImage(ImageView view, Storefront.Product product) {
        if (product != null) {
            if (product.getVariants().getEdges().get(0).getNode().getImage() != null) {
                String imageUrl = product.getVariants().getEdges().get(0).getNode().getImage().getSrc();

                if (imageUrl != null) {
                    Picasso.with(view.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.trendybanner)
                            .error(R.drawable.trendybanner)
                            .resize(200, 200)
                            .into(view);
                } else {
                    Picasso.with(view.getContext())
                            .load(R.drawable.trendybanner)
                            .into(view);
                }
            } else {
                Picasso.with(view.getContext())
                        .load(R.drawable.trendybanner)
                        .into(view);
            }
        }
    }

    @BindingAdapter("spinner1")
    public static void spinner(Spinner spinner, Storefront.Product product) {
        ArrayList<String> spinner_title = new ArrayList<>();

        for (int i = 0; i < product.getVariants().getEdges().size(); i++) {
            spinner_title.add(product.getVariants().getEdges().get(i).getNode().getTitle());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
                android.R.layout.simple_spinner_item, spinner_title);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }
}



