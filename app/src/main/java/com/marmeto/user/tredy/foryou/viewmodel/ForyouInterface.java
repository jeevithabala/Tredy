package com.marmeto.user.tredy.foryou.viewmodel;

import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;

import java.util.ArrayList;

public interface ForyouInterface {
    public void allcollection( ArrayList<AllCollectionModel> allCollectionModelArrayList);

    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList);

    public void bannerlist(ArrayList<String> bannerlist);

    public void grocerylist(ArrayList<GroceryHomeModel> arrayList);

    public void getcount(int count);
}
