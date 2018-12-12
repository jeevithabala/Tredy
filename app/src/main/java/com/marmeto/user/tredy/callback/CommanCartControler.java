package com.marmeto.user.tredy.callback;

public interface CommanCartControler {

    public void AddToCart(String id);

    public void AddQuantity(String id);

    public void RemoveQuantity(String id);
    public int getTotalPrice();
    public int getItemCount();
    public void  UpdateShipping(String id, String value);

    public void AddToWhislist(String id);

    void AddToCartGrocery(String trim, int selectedweight, int qty);
}