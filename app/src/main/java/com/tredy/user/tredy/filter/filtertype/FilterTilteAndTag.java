package com.tredy.user.tredy.filter.filtertype;

import java.util.ArrayList;

public class FilterTilteAndTag {

    private String Title;
    private ArrayList<FilterModel> filterModels;

    public FilterTilteAndTag() {
    }

    public FilterTilteAndTag(String title, ArrayList<FilterModel> filterModels) {
        Title = title;
        this.filterModels = filterModels;
    }

    public String getTitle() {
        return Title;
    }

    public ArrayList<FilterModel> getFilterModels() {
        return filterModels;
    }

}
