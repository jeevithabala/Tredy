package com.tredy.user.tredy.foryou.newarrivalrecycler;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.tredy.user.tredy.category.CategoryProduct;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalAdapter;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalModel;
import com.tredy.user.tredy.R;

import java.util.ArrayList;

import static com.tredy.user.tredy.foryou.ForYou.getNewArrival;


public class NewMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
//    private ArrayList<Object> items;
//    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private final int HORIZONTAL1 = 0;
    private ArrayList<NewArrivalModel> newArrival;
    private FragmentManager fragmentManager;


    public NewMainAdapter(Context context, ArrayList<NewArrivalModel> newArrival, FragmentManager fragmentManager) {
        this.context = context;
        this.newArrival = newArrival;
        this.fragmentManager=fragmentManager;
    }

//    public NewMainAdapter(ArrayList<NewArrivalModel> newArrival) {
//        this.newArrival=newArrival;
//    }


    //this method returns the number according to the Vertical/Horizontal object


    //this method returns the holder that we've inflated according to the viewtype.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
//            case HORIZONTAL:
//                view = inflater.inflate(R.layout.blank, parent, false);
//                holder = new HorizontalViewHolder(view);
//                break;

            case HORIZONTAL1:
                view = inflater.inflate(R.layout.newarrival1, parent, false);
                holder = new NewArrivalViewHolder(view);

                break;

            case VERTICAL:
                view = inflater.inflate(R.layout.blank1, parent, false);
                holder = new VerticalViewHolder(view);
                break;



            default:
                view = inflater.inflate(R.layout.blank1, parent, false);
                holder = new VerticalViewHolder(view);

                break;
        }


        return holder;
    }

    //here we bind view with data according to the position that we have defined.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


//        if (holder.getItemViewType() == HORIZONTAL)
//            topSelling((HorizontalViewHolder) holder);

         if (holder.getItemViewType() == VERTICAL)
            bestCollection((VerticalViewHolder) holder);

        else if (holder.getItemViewType() == HORIZONTAL1)
            newArrival((NewArrivalViewHolder) holder);




    }

    private void bestCollection(VerticalViewHolder holder) {


       holder.see_all.setOnClickListener(view -> {
           Fragment fragment = new CategoryProduct();
           Bundle bundle = new Bundle();
           bundle.putString("collection", "newarrival");
           bundle.putSerializable("category_id", getNewArrival().get(0));
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

    //
//    private void topSelling(HorizontalViewHolder holder) {
//        Log.d("Adapter1", "come");
//        TopSellingAdapter adapter = new TopSellingAdapter(getTopSellingCollection());
//        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        holder.recyclerView.setAdapter(adapter);
      //  holder.topselling.setText("io");
//        Log.d("collectiontitle", "" + getTopSellingCollection().get(0).getCollectionTitle());
//    }

    private void newArrival(NewArrivalViewHolder holder) {

        NewArrivalAdapter adapter = new NewArrivalAdapter(context,newArrival, fragmentManager);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.recyclerView.setAdapter(adapter);
//        holder.newarrival.setText(String.valueOf(newArrival.get(0).getCollectionTitle()));
//       Log.d("collectiontitley", "" +newArrival.get(0).getCollectionTitle());
    }

    @Override
    public int getItemCount() {
        Log.e("item Sizelist",""+ String.valueOf(newArrival.size()));
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HORIZONTAL1;
        else
            return VERTICAL;

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


//    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
//
//        RecyclerView recyclerView;
//        TextView topselling;
//
//        HorizontalViewHolder(View itemView) {
//            super(itemView);
//         //   topselling = itemView.findViewById(R.id.category_title);
////            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
//        }
//    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
//        RecyclerView recyclerView;
      LinearLayout see_all;

        VerticalViewHolder(View itemView) {
            super(itemView);
            see_all = itemView.findViewById(R.id.see_all);
           // recyclerView = itemView.findViewById(R.id.bestselling_recyclerView);
        }
    }

    public class NewArrivalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
//        TextView newarrival;

        NewArrivalViewHolder(View itemView) {
            super(itemView);
         //   newarrival = itemView.findViewById(R.id.new_arrival_text);
            recyclerView = itemView.findViewById(R.id.newarrival_recycler);
        }
    }


}

