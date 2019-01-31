package com.tredy.user.tredy.filter.sortby;

public class SortByModel {

    String title;
    private boolean checked=false;

    public SortByModel(String title,boolean checked) {
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
