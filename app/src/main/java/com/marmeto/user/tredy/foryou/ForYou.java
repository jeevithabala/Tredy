package com.marmeto.user.tredy.foryou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.foryou.allcollection.AllCollectionAdapter;
import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;
import com.marmeto.user.tredy.foryou.viewmodel.ForYouViewModel;
import com.marmeto.user.tredy.foryou.viewmodel.ForyouInterface;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.login.LoginActiviy;
import com.marmeto.user.tredy.util.Config;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ForYou extends Fragment implements ResultCallBackInterface, ForyouInterface {


    private ArrayList<Object> objects = new ArrayList<>();
    ResultCallBackInterface resultCallBackInterface;


    RecyclerView topselling_recyclerview, allcollection;
    GraphClient graphClient;
    static ArrayList<TopSellingModel> topSellingModelArrayList = new ArrayList<>();
    static ArrayList<NewArrivalModel> newArrivalModelArrayList = new ArrayList<>();
    static ArrayList<GroceryHomeModel> GroceryHomeModels = new ArrayList<>();
    MainAdapter adapter;
    AllCollectionAdapter allCollectionAdapter;
    ArrayList<AllCollectionModel> allCollectionModelArrayList = new ArrayList<>();
    ArrayList<String> bannerlist = new ArrayList<>();
    View view;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    SlidingImage_Adapter slidingImage_adapter;
    private ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.foryou, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Home");

        ForYouViewModel forYouViewModel = new ForYouViewModel(getActivity(), this);

        topselling_recyclerview = view.findViewById(R.id.main_recyclerview);
        allcollection = view.findViewById(R.id.allcollection);
        resultCallBackInterface = (ResultCallBackInterface) this;


        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        allCollectionAdapter = new AllCollectionAdapter(getActivity(), allCollectionModelArrayList, getFragmentManager());
        allcollection.setAdapter(allCollectionAdapter);
        allcollection.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        topselling_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        getObject().clear();

        if (getTopSellingCollection() != null || getNewArrival() != null || getGroceryHomeModels() != null) {
            adapter = new MainAdapter(getActivity(), getObject(), getFragmentManager());
            topselling_recyclerview.setAdapter(adapter);
        }
    }

    private ArrayList<Object> getObject() {

        return objects;
    }

    public static ArrayList<TopSellingModel> getTopSellingCollection() {
        return topSellingModelArrayList;
    }

    public static ArrayList<NewArrivalModel> getNewArrival() {
        return newArrivalModelArrayList;
    }

    public static ArrayList<GroceryHomeModel> getGroceryHomeModels() {
        return GroceryHomeModels;
    }

    @Override
    public void onResume() {
        super.onResume();

//        Config.hideKeyboard(Objects.requireNonNull(getActivity()));
    }


    @Override
    public void topSelling(ArrayList<TopSellingModel> arrayList) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    topSellingModelArrayList.clear();
                    for (int i = 0; i < arrayList.size(); i++) {
                        TopSellingModel topSellingModel = new TopSellingModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());
                        topSellingModel.setCollectionid(arrayList.get(i).getCollectionid());
                        topSellingModelArrayList.add(topSellingModel);
                    }


                    getObject().add(topSellingModelArrayList.get(0));
                    adapter.notifyDataSetChanged();


                }
            });
        }

    }

    @Override
    public void newArrivals(ArrayList<NewArrivalModel> arrayList) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    newArrivalModelArrayList.clear();


                    for (int i = 0; i < arrayList.size(); i++) {
                        NewArrivalModel newArrivalModel = new NewArrivalModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());
                        newArrivalModel.setCollectionid(arrayList.get(i).getCollectionid());
                        newArrivalModelArrayList.add(newArrivalModel);
                    }
                    getObject().add(newArrivalModelArrayList.get(0));
                    adapter.notifyDataSetChanged();


                }
            });
        }
    }

    @Override
    public void grocery(ArrayList<GroceryHomeModel> arrayList) {
        GroceryHomeModels.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            GroceryHomeModel GroceryHomeModel = new GroceryHomeModel();
            GroceryHomeModel.setProduct(arrayList.get(i).getProduct());
            GroceryHomeModel.setQty("1");
            GroceryHomeModel.setTitle(arrayList.get(i).getTitle());
            GroceryHomeModels.add(GroceryHomeModel);
        }
        getObject().add(GroceryHomeModels.get(0));

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void init() {

        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), bannerlist);
        mPager.setAdapter(slidingImage_adapter);


        CirclePageIndicator indicator = (CirclePageIndicator) view.
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(3 * density);


        NUM_PAGES = bannerlist.size();


        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 8000, 8000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    public void allcollection(ArrayList<AllCollectionModel> allCollectionModelArrayList1) {
        allCollectionModelArrayList.clear();
        for (int i = 0; i < allCollectionModelArrayList1.size(); i++) {
            AllCollectionModel allCollectionModel = new AllCollectionModel(allCollectionModelArrayList1.get(i).getId(), allCollectionModelArrayList1.get(i).getImage(), allCollectionModelArrayList1.get(i).getTitle());
            allCollectionModelArrayList.add(allCollectionModel);
        }
        allCollectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList) {

        resultCallBackInterface.topSelling(topSellingModelArrayList);
        resultCallBackInterface.newArrivals(newArrivalModelArrayList);
    }

    @Override
    public void bannerlist(ArrayList<String> bannerlist1) {
        bannerlist.clear();

        bannerlist.addAll(bannerlist1);
        init();
    }

    @Override
    public void grocerylist(ArrayList<GroceryHomeModel> arrayList) {
        resultCallBackInterface.grocery(arrayList);
    }

    @Override
    public void getcount(int count) {
        Navigation.noti_counnt = count;
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

//        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }




}