package com.tredy.user.tredy.category;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.tredy.user.tredy.callback.ProductClickInterface;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.category.model.ProductModel;
import com.tredy.user.tredy.databinding.ProductAdapterBinding;
import com.tredy.user.tredy.databinding.ProductBinding;

import java.util.ArrayList;

import static com.tredy.user.tredy.category.CategoryProduct.isViewWithCatalog;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<ProductModel> itemsList;
    private FragmentManager fragmentManager;
    private ProductClickInterface productClickInterface;


     ProductAdapter(Context mContext, ArrayList<ProductModel> itemsList, FragmentManager fragmentManager, ProductClickInterface productClickInterface) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
        this.productClickInterface = productClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder;


        if (isViewWithCatalog) {
            ProductAdapterBinding productAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.product_adapter, parent, false);

//            view = productAdapterBinding;
            viewHolder = new ViewHolder<>(productAdapterBinding);
        } else {
          ProductBinding productBinding = DataBindingUtil.inflate(layoutInflater, R.layout.product, parent, false);

//          view = productBinding;

            viewHolder = new ViewHolder<>(productBinding);

        }

        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ( holder).getBinding().setVariable(BR.product, itemsList.get(position));
        (holder).getBinding().setVariable(BR.itemclick,productClickInterface);
        ( holder).getBinding().executePendingBindings();
//        viewHolder.getBinding().setVariable(BR.onitemclickplus,plus;
        //        viewHolder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                int position=viewHolder.getAdapterPosition();
//                productClickInterface.clickProduct(itemsList.get(position).getProduct_ID());
//                Log.e("position",itemsList.get(position).getProduct_ID());
////                productClickInterface.clickProduct(itemsList.get(position).getProduct_ID());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }



    class ViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder  {
        private final T binding;

        public ViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public T getBinding() {
            return binding;
        }


    }
//    class ViewHolder extends RecyclerView.ViewHolder {
//
//        private final ProductAdapterBinding binding;
//
//
//        public ViewHolder(final ProductAdapterBinding itembinding) {
//            super(itembinding.getRoot());
//            this.binding = itembinding;
//
//
//            binding.setItemclick(new FragmentRecyclerViewClick() {
//                @Override
//                public void onClickPostion() {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
//
////                    onItemClick.onClick(itemsList.get(getAdapterPosition()).getProduct().getId().toString());
////                    Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
////                            .setLineItemsInput(Input.value(Arrays.asList(
////                                    new Storefront.CheckoutLineItemInput(5, new ID(itemsList.get(getAdapterPosition()).getProduct_ID()))
////                            )));
////
//                    Fragment fragment = new ProductView();
//
//                    fragment.setArguments(bundle);
//                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
//                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                    // ft.addToBackStack("fragment");
//                    ft.commit();
//
//////
//                }
//            });
//        }


//    }

    public interface OnItemClick {
        void onClick(String value);
    }
}
