package com.marmeto.user.tredy.foryou;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marmeto.user.tredy.category.CategoryProduct;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeAdapter;
import com.marmeto.user.tredy.foryou.newarrivalrecycler.NewMainAdapter;
import com.marmeto.user.tredy.foryou.topselling.TopSellingAdapter;
import com.marmeto.user.tredy.groceries.Groceries;
import com.marmeto.user.tredy.R;

import java.util.ArrayList;
import static com.marmeto.user.tredy.foryou.ForYou.getGroceryHomeModels;
import static com.marmeto.user.tredy.foryou.ForYou.getNewArrival;
import static com.marmeto.user.tredy.foryou.ForYou.getTopSellingCollection;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;
    private final int HORIZONTAL = 0;
    private final int HORIZONTAL1 = 1;
    private final int Grocery = 2;
    private FragmentManager fragmentManager;


     MainAdapter(Context context, ArrayList<Object> items, FragmentManager fragmentManager) {
        this.context = context;
        this.items = items;
        this.fragmentManager = fragmentManager;
    }

    //this method returns the number according to the Vertical/Horizontal object


    //this method returns the holder that we've inflated according to the viewtype.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case HORIZONTAL:
                view = inflater.inflate(R.layout.topselling_recycler, parent, false);
                holder = new HorizontalViewHolder(view);
                break;

//            case VERTICAL:
//                view = inflater.inflate(R.layout.bestcollection_recycler, parent, false);
//                holder = new VerticalViewHolder(view);
//                break;

            case HORIZONTAL1:
                view = inflater.inflate(R.layout.newarrival_recycler, parent, false);
                holder = new NewArrivalViewHolder(view);

                break;
            case Grocery:
                view = inflater.inflate(R.layout.groceryhome, parent, false);
                holder = new GroceryHolder(view);

                break;

            default:
//                view = inflater.inflate(R.layout.bestcollection_recycler, parent, false);
//                holder = new VerticalViewHolder(view);

                view = inflater.inflate(R.layout.topselling_recycler, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
        }


        return holder;
    }

    //here we bind view with data according to the position that we have defined.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder.getItemViewType() == HORIZONTAL)
            topSelling((HorizontalViewHolder) holder);

//        else if (holder.getItemViewType() == VERTICAL)
//            bestCollection((VerticalViewHolder) holder);

        else if (holder.getItemViewType() == HORIZONTAL1)
            newArrival((NewArrivalViewHolder) holder);

        else if (holder.getItemViewType() == Grocery)
            grocery((GroceryHolder) holder);


    }

//    private void bestCollection(VerticalViewHolder holder) {
//
//
//        Log.d("Adapter", "come");
//
//        TopCollectionAdapter adapter1 = new TopCollectionAdapter(getBestCollection(), fragmentManager);
//        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        holder.recyclerView.setAdapter(adapter1);
//
//        if (getBestCollection().size() != 0) {
//            holder.bestselling.setText(getBestCollection().get(0).getCollectionTitle());
////            Log.d("collectiontitle", "" + getBestCollection().get(0).getCollectionTitle());
//        }
//        holder.seall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment fragment = new CategoryProduct();
//                Bundle bundle = new Bundle();
//                bundle.putString("collection", "bestcollection");
//                bundle.putSerializable("category_id", getBestCollection().get(0));
////                Log.e("iddddddd", getBestCollection().get(0).getCollectionTitle());
//                fragment.setArguments(bundle);
//                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
//                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                if(fragmentManager.findFragmentByTag("categoryproduct")==null)
//                {
//                    ft.addToBackStack("categoryproduct");
//                    ft.commit();
//                }
//                else
//                {
//                    ft.commit();
//                }
//
//            }
//        });
//
//    }

    //
    private void topSelling(HorizontalViewHolder holder) {
        TopSellingAdapter adapter = new TopSellingAdapter(context, getTopSellingCollection(), fragmentManager);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
        if (getTopSellingCollection().size() >0) {
            holder.topselling.setText(String.valueOf(getTopSellingCollection().get(0).getCollectionTitle()));
        }
        holder.seall.setOnClickListener(view -> {
            if (getTopSellingCollection().size() >0) {
                Fragment fragment = new CategoryProduct();
                Bundle bundle = new Bundle();
                bundle.putString("collection", "topselling");
                bundle.putSerializable("category_id", getTopSellingCollection().get(0));
                fragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                if(fragmentManager.findFragmentByTag("categoryproduct")==null)
                {
                    ft.addToBackStack("categoryproduct");
                    ft.commit();
                }
                else
                {
                    ft.commit();
                }

            }


        });
    }


    private void newArrival(NewArrivalViewHolder holder) {
        NewMainAdapter adapter = new NewMainAdapter(context, getNewArrival(), fragmentManager);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
        if (getNewArrival().size()>0) {
            holder.newarrival.setText(String.valueOf(getNewArrival().get(0).getCollectionTitle()));

        }

        holder.seeall1.setOnClickListener(view -> {
            Fragment fragment = new CategoryProduct();
            Bundle bundle = new Bundle();
            bundle.putString("collection", "newarrival");
            bundle.putSerializable("category_id", getNewArrival().get(0));
//                Log.e("iddddddd", getNewArrival().get(0).getCollectionTitle());
            fragment.setArguments(bundle);
            FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if(fragmentManager.findFragmentByTag("categoryproduct")==null)
            {
                ft.addToBackStack("categoryproduct");
                ft.commit();
            }
            else
            {
                ft.commit();
            }

        });

    }

    private void grocery(GroceryHolder holder) {
        GroceryHomeAdapter groceryAdapter = new GroceryHomeAdapter(context, getGroceryHomeModels(), fragmentManager);
        holder.grocery_recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.grocery_recyclerview.setAdapter(groceryAdapter);
        if (getGroceryHomeModels().size() != 0) {
            holder.grocery_title.setText(getGroceryHomeModels().get(0).getTitle());
        }
        holder.seall.setOnClickListener(view -> {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.add(R.id.home_container, new Groceries(), "grocery");
            if(fragmentManager.findFragmentByTag("grocery")==null)
            {
                transaction.addToBackStack("grocery");
                transaction.commit();
            }
            else
            {
                transaction.commit();
            }

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HORIZONTAL;
//        else if (position == 1)
//            return VERTICAL;
        else if (position == 2)
            return Grocery;
        else
            return HORIZONTAL1;
    }

//    public class AllView extends RecyclerView.ViewHolder{
//        public AllView( View itemView) {
//            super(itemView);
//            if (getItemViewType() == HORIZONTAL) {
//                HorizontalViewHolder(itemView)
//            }
//
//        }
//
//
//    }


    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView topselling;
        LinearLayout seall;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            topselling = itemView.findViewById(R.id.category_title);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
            seall = itemView.findViewById(R.id.see_all);
        }
    }

//    public class VerticalViewHolder extends RecyclerView.ViewHolder {
//        RecyclerView recyclerView;
//        TextView bestselling;
//        LinearLayout seall;
//
//        VerticalViewHolder(View itemView) {
//            super(itemView);
//            bestselling = itemView.findViewById(R.id.category_title);
//            recyclerView = itemView.findViewById(R.id.bestselling_recyclerView);
//            seall = itemView.findViewById(R.id.see_all);
//        }
//    }

    public class NewArrivalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView newarrival;
        LinearLayout seeall1;

        NewArrivalViewHolder(View itemView) {
            super(itemView);
            newarrival = itemView.findViewById(R.id.new_arrival_text);
            recyclerView = itemView.findViewById(R.id.newarrival_recyclerView);
            seeall1 = itemView.findViewById(R.id.see_all);
        }
    }

    private class GroceryHolder extends RecyclerView.ViewHolder {
        RecyclerView grocery_recyclerview;
        TextView grocery_title;
        LinearLayout seall;


        GroceryHolder(View view) {
            super(view);
            grocery_recyclerview = view.findViewById(R.id.grocery_recycler);
            grocery_title = view.findViewById(R.id.category_title);
            seall = itemView.findViewById(R.id.see_all);
        }
    }
}
