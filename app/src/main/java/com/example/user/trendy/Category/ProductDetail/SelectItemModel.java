package com.example.user.trendy.Category.ProductDetail;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.example.user.trendy.Category.ProductModel;
import com.example.user.trendy.R;
import com.shopify.buy3.Storefront;

import java.math.BigDecimal;

public class SelectItemModel extends BaseObservable {

    private ProductModel selectItem;
    private boolean addedTocart = false;
    private Storefront.Product product;
    int price;
    String productid;

    private String count = "1";
    private int cost;
    private String ship;

    @Bindable
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;

        setTotal(Integer.parseInt(count) * getProduct().getVariants().getEdges().get(price).getNode().getPrice().intValue());

        notifyPropertyChanged(BR.price);
        notifyPropertyChanged(BR.count);
        notifyPropertyChanged(BR.cost);
    }
@Bindable
    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
        notifyPropertyChanged(BR.productid);
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public SelectItemModel() {
    }

    public Storefront.Product getProduct() {
        return product;
    }

    public void setProduct(Storefront.Product product) {
        this.product = product;
    }

    public boolean isAddedTocart() {
        return addedTocart;
    }

    public void setAddedTocart(boolean addedTocart) {
        this.addedTocart = addedTocart;
    }

    public SelectItemModel(ProductModel selectItem) {
        this.selectItem = selectItem;

    }

    public ProductModel getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(ProductModel selectItem) {
        this.selectItem = selectItem;
    }


    public void increment() {
        setCount(String.valueOf(Integer.parseInt(getCount()) + 1));
//        setTotal(getCount()* selectItem.getProduct().getVariants().getEdges().get(0).getNode().getPrice().intValue());

    }

    public void decrement() {
        if (Integer.parseInt(getCount()) != 0 && Integer.parseInt(getCount()) > 1) {
            setCount(String.valueOf(Integer.parseInt(getCount()) - 1));
//            setTotal(getCount()* selectItem.getProduct().getVariants().getEdges().get(0).getNode().getPrice().intValue());
        }

    }

    @Bindable
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;

        if (count.equalsIgnoreCase("")) {

        } else if (count.equalsIgnoreCase("0")) {
            setCount("1");
            notifyPropertyChanged(BR.count);
        } else {
            Log.e("price", String.valueOf(price));
            setTotal(Integer.parseInt(count) * getProduct().getVariants().getEdges().get(price).getNode().getPrice().intValue());
            notifyPropertyChanged(BR.count);
        }


    }

    public void setTotal(int cost) {
        this.cost = cost;
//        if (cost != 0) {
        notifyPropertyChanged(BR.cost);
//        }

    }

    @Bindable
    public int getCost() {
        return cost;
    }

    @BindingAdapter({"productPrice","position"})
    public static void setProductprice(TextView textView, Storefront.Product product, int i)
    {
        if(product!=null) {
            if (product.getVariants() != null)
                textView.setText(product.getVariants().getEdges().get(i).getNode().getPrice().toString());
        }
    }


    @BindingAdapter("desc")
    public static void desc(TextView text, SelectItemModel desc) {
        String mHtmlString = desc.selectItem.getProduct().getDescriptionHtml();
        Log.e("desxc", mHtmlString);
        text.setText(Html.fromHtml(Html.fromHtml(mHtmlString).toString()));

    }


}