package com.tredy.user.tredy.bag.cartdatabase;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class AddToCart_Model extends BaseObservable implements Serializable {

    private String product_name;
    private String product_varient_id;
    private Double product_price;
    private String product_varient_title;
    private int qty;
    private String imageUrl;
    private String col_id;
    private int total;
    private  String tag;
    private ArrayList<String> productlist=new ArrayList<>();
    private String ship, product_id;



    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    String getShip() {
        return ship;
    }

    void setShip(String ship) {
        this.ship = ship;
    }

    public ArrayList<String> getProductlist() {
        return productlist;
    }

    public void setProductlist(ArrayList<String> productlist) {
        this.productlist = productlist;
    }

    public String getCol_id() {
        return col_id;
    }

    void setCol_id(String col_id) {
        this.col_id = col_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_varient_id() {
        return product_varient_id;
    }

    void setProduct_varient_id(String product_varient_id) {
        this.product_varient_id = product_varient_id;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Double product_price) {
        this.product_price = product_price;
    }

    public String getProduct_varient_title() {
        return product_varient_title;
    }

    void setProduct_varient_title(String product_varient_title) {
        this.product_varient_title = product_varient_title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Bindable
    public int getQty() {
        setTotal(qty);
        return qty;
    }
    public void setTotal(int qty) {
        this.qty = qty;
        notifyPropertyChanged(BR.qty);
    }



    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Bindable
    public int getTotal() {
        setTotal1(total);
        return total;
    }
     private void setTotal1(int total) {
        this.total = total;
        notifyPropertyChanged(BR.total);
    }
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl.equals("")){}else {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(view);}
    }

}
