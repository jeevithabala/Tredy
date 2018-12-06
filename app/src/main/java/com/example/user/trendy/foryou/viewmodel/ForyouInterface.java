package com.example.user.trendy.foryou.viewmodel;

import com.example.user.trendy.foryou.allcollection.AllCollectionModel;
import com.example.user.trendy.foryou.groceryhome.GroceryHomeModel;
import com.example.user.trendy.foryou.newarrival.NewArrivalModel;
import com.example.user.trendy.foryou.topselling.TopSellingModel;

import java.util.ArrayList;

public interface ForyouInterface {
    public void allcollection( ArrayList<AllCollectionModel> allCollectionModelArrayList);

    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList);

    public void bannerlist(ArrayList<String> bannerlist);

    public void grocerylist(ArrayList<GroceryHomeModel> arrayList);
}
