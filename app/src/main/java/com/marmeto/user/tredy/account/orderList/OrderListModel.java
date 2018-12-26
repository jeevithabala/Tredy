package com.marmeto.user.tredy.account.orderList;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.marmeto.user.tredy.R;
import com.squareup.picasso.Picasso;

public class OrderListModel {

    String image, title, totalcost, shippingtax, subtotal ,id ,quantity, productcost;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductcost() {
        return productcost;
    }

    public void setProductcost(String productcost) {
        this.productcost = productcost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(String totalcost) {
        this.totalcost = totalcost;
    }

    public String getShippingtax() {
        return shippingtax;
    }

    public void setShippingtax(String shippingtax) {
        this.shippingtax = shippingtax;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }


    @BindingAdapter("orderimage")
    public static void loadImage(ImageView view, String image) {
//        String imageUrl = orderd.getLineItems().getEdges().get(0).getNode().getVariant().getImage().getSrc();

        if (image != null) {
            Picasso.with(view.getContext())
                    .load(image)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .resize(200, 200)
                    .into(view);
        } else {
            Picasso.with(view.getContext())
                    .load(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(view);
        }
    }
}
