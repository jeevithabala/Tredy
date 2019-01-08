package com.marmeto.user.tredy.category.model;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.marmeto.user.tredy.R;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryModel extends BaseObservable implements Serializable {
    private  String imageUrl, id, collectiontitle;
    private ArrayList<SubCategoryModel> subCategoryModelArrayList;


    private Storefront.Collection collection;

    public Storefront.Collection getCollection() {
        return collection;
    }

    public void setCollection(Storefront.Collection collection) {
        this.collection = collection;
    }

    public String getImageurl() {
        return imageUrl;
    }

    public void setImageurl(String imageurl) {
        this.imageUrl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectiontitle() {
        return collectiontitle;
    }

    public void setCollectiontitle(String collectiontitle) {
        this.collectiontitle = collectiontitle;
    }

    public ArrayList<SubCategoryModel> getSubCategoryModelArrayList() {
        return subCategoryModelArrayList;
    }

    public void setSubCategoryModelArrayList(ArrayList<SubCategoryModel> subCategoryModelArrayList) {
        this.subCategoryModelArrayList = subCategoryModelArrayList;
    }

//    @BindingAdapter("categoryImageUrl")
//    public static void loadImage(ImageView view, Storefront.Collection collection) {
//        if (collection.getImage() != null) {
//            String image = "" + collection.getImage().getSrc();
//            Log.d("categoryimage", "" + image);
//                Picasso.with(view.getContext())
//                        .load(collection.getImage().getSrc())
//                        //.transform(new CircleTransform())
//                        .into(view);
//        } else {
//            Picasso.with(view.getContext())
//                    .load(R.drawable.ic_dashboard_black_24dp)
//                    //.transform(new CircleTransform())
//                    .into(view);
//        }
//    }

    @BindingAdapter("imageu")
    public static void loadImage1(ImageView view, String imageUrl) {

        if(imageUrl!=null){
//        Log.d("categoryimage", "" + imageUrl);
        if (imageUrl.trim().length()>0) {
            Picasso.with(view.getContext())

                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(view);
        } else {
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
