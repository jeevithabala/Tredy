package com.marmeto.user.tredy.callback;

import java.util.ArrayList;

public interface OnFilterDataCallBack {

    public void onFilterValueSelectCallBack(String minprice, String maxprice, String sortby, String collectionid, ArrayList<String> selectedFilterLists , String CollectionName);
}
