package com.marmeto.user.tredy.foryou.viewmodel;

import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;

import java.util.ArrayList;

public interface ForyouInterface {

     void allcollection( ArrayList<AllCollectionModel> allCollectionModelArrayList);
     void collectionlist( ArrayList<NewArrivalModel> newArrivalModelArrayList);
     void bannerlist(ArrayList<String> bannerlist);
     void grocerylist(ArrayList<GroceryHomeModel> arrayList);
     void topselling1(ArrayList<TopSellingModel> topSellingModelArrayList);
     void getcount(int count);

}
