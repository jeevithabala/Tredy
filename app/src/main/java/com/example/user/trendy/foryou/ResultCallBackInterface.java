package com.example.user.trendy.foryou;

import com.example.user.trendy.foryou.groceryhome.GroceryHomeModel;
import com.example.user.trendy.foryou.newarrival.NewArrivalModel;
import com.example.user.trendy.foryou.topselling.TopSellingModel;

import java.util.ArrayList;

public interface ResultCallBackInterface {
//    public void bestCollection(String collectionid,String id, String title, String price, String image, String collectionname);
//    public void topSelling(String collectionid,String id, String title, String price, String image, String collectionname);
//    public void newArrivals(String collectionid,String id, String title, String price, String image, String collectionname);

//    public void bestCollection(ArrayList<TopCollectionModel> arrayList);
    public void topSelling(ArrayList<TopSellingModel> arrayList);
    public void newArrivals(ArrayList<NewArrivalModel> arrayList);
    public void grocery(ArrayList<GroceryHomeModel> arrayList);
}
