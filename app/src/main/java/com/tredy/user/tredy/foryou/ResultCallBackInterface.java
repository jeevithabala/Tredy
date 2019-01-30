package com.tredy.user.tredy.foryou;

import com.tredy.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalModel;
import com.tredy.user.tredy.foryou.topselling.TopSellingModel;

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
