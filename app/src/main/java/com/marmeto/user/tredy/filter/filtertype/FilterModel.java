package com.marmeto.user.tredy.filter.filtertype;

public class FilterModel {
    String title;
    boolean checked=false;



    public FilterModel() {
    }

    public FilterModel(String title,boolean checked) {
        this.title = title;
        this.checked=checked;
    }

    public String getTitle() {
        return title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


}
