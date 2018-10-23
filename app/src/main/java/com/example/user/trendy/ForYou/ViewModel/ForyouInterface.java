package com.example.user.trendy.ForYou.ViewModel;

import com.example.user.trendy.ForYou.AllCollection.AllCollectionModel;
import com.example.user.trendy.ForYou.GroceryHome.GroceryHomeModel;
import com.example.user.trendy.ForYou.NewArrival.NewArrivalModel;
import com.example.user.trendy.ForYou.TopCollection.TopCollectionModel;
import com.example.user.trendy.ForYou.TopSelling.TopSellingModel;

import java.util.ArrayList;

public interface ForyouInterface {
    public void allcollection( ArrayList<AllCollectionModel> allCollectionModelArrayList);

    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<TopCollectionModel> topCollectionModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList);

    public void bannerlist(ArrayList<String> bannerlist);

    public void grocerylist(ArrayList<GroceryHomeModel> arrayList);
}
