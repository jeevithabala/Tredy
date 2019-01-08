package com.marmeto.user.tredy.foryou.allcollection;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.marmeto.user.tredy.R;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class AllCollectionModel implements Serializable {
    private String id, image, title;

    private Storefront.Collection collection;

    public Storefront.Collection getCollection() {
        return collection;
    }

    public void setCollection(Storefront.Collection collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public AllCollectionModel(String id, String image, String title) {
        this.id = id;
        this.image = image;
        this.title = title;
    }

    @BindingAdapter("imageUr")
    public static void loadImage(ImageView view, String image) {
        if(image!=null) {
            if(image.trim().length()>0) {
                Picasso.with(view.getContext())
                        .load(image)
                        .into(view);
            }else {
                Picasso.with(view.getContext())
                        .load(R.drawable.ic_placeholder)
                        .placeholder(R.drawable.ic_placeholder)
                        //.transform(new CircleTransform())
                        .into(view);
            }
        } else {
            Picasso.with(view.getContext())
                    .load(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    //.transform(new CircleTransform())
                    .into(view);
        }
    }

}
