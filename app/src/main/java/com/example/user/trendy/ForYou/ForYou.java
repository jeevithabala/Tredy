package com.example.user.trendy.ForYou;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.Account.MyAccount;
import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.ForYou.AllCollection.AllCollectionAdapter;
import com.example.user.trendy.ForYou.AllCollection.AllCollectionModel;
import com.example.user.trendy.ForYou.GroceryHome.GroceryHomeModel;
import com.example.user.trendy.ForYou.NewArrival.NewArrivalModel;
import com.example.user.trendy.ForYou.TopCollection.TopCollectionAdapter;
import com.example.user.trendy.ForYou.TopCollection.TopCollectionModel;
import com.example.user.trendy.ForYou.TopSelling.TopSellingAdapter;
import com.example.user.trendy.ForYou.TopSelling.TopSellingModel;
import com.example.user.trendy.ForYou.ViewModel.ForYouViewModel;
import com.example.user.trendy.ForYou.ViewModel.ForyouInterface;
import com.example.user.trendy.Groceries.Groceries;
import com.example.user.trendy.Navigation;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.Constants;
import com.example.user.trendy.Whislist.Whislist;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ForYou extends Fragment implements ResultCallBackInterface, ForyouInterface {


    private ArrayList<Object> objects = new ArrayList<>();
    ResultCallBackInterface resultCallBackInterface;


    RecyclerView topselling_recyclerview, topcollection_recyclerview, new_arrivals_recyclerview, allcollection;
    GraphClient graphClient;
    static ArrayList<TopSellingModel> topSellingModelArrayList = new ArrayList<>();
    static ArrayList<TopCollectionModel> topCollectionModelArrayList = new ArrayList<>();
    static ArrayList<NewArrivalModel> newArrivalModelArrayList = new ArrayList<>();
    static ArrayList<GroceryHomeModel> GroceryHomeModels = new ArrayList<>();
    private RequestQueue mRequestQueue;
    MainAdapter adapter;
    AllCollectionAdapter allCollectionAdapter;
    ArrayList<AllCollectionModel> allCollectionModelArrayList = new ArrayList<>();
    ArrayList<String> bannerlist = new ArrayList<>();
    ProgressDialog progressDialog;
    private ArrayList<Object> getObjects1 = new ArrayList<>();
    private ArrayList<TopCollectionModel> topcollectionlist = new ArrayList<>();
    View view;
    String id, title, price, image;
    private String collectionname;
    private JsonArrayRequest request;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    SlidingImage_Adapter slidingImage_adapter;
    private String collectionid;
    CardView myaccount, whislist;
    TextView grcery;
    ArrayList<TopSellingModel> topSellingModelArray = new ArrayList<>();
    ArrayList<TopCollectionModel> topCollectionModelArray = new ArrayList<>();
    ArrayList<NewArrivalModel> newArrivalModelArray = new ArrayList<>();
    private ArrayList<GroceryHomeModel> GroceryHomeModelArrayList = new ArrayList<>();
    Toolbar toolbar;
    private String converted = "";

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

        return view;
    }


    private ArrayList<Object> getObject() {

        return objects;
    }

    public static ArrayList<TopSellingModel> getTopSellingCollection() {
        return topSellingModelArrayList;
    }

    public static ArrayList<TopCollectionModel> getBestCollection() {
        return topCollectionModelArrayList;
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

    }

    @Override
    public void bestCollection(ArrayList<TopCollectionModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < arrayList.size(); i++) {
                    TopCollectionModel topCollectionModel = new TopCollectionModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());

                    topCollectionModel.setCollectionid(arrayList.get(i).getCollectionid());
                    topCollectionModelArrayList.add(topCollectionModel);
                }

                getObject().add(topCollectionModelArrayList.get(0));
                adapter.notifyDataSetChanged();


            }
        });
    }

    @Override
    public void topSelling(ArrayList<TopSellingModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                topSellingModelArrayList.clear();
                topCollectionModelArrayList.clear();
                newArrivalModelArrayList.clear();

                Log.e("array1", String.valueOf(arrayList.size()));
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

    @Override
    public void newArrivals(ArrayList<NewArrivalModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {


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
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void init() {

//Log.e("bannerlist", ""+String.valueOf(bannerlist.size()));
        ImagesArray.clear();
        for (int i = 0; i < bannerlist.size(); i++)
            ImagesArray.add(bannerlist.get(i));


        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), ImagesArray);
        mPager.setAdapter(slidingImage_adapter);


        CirclePageIndicator indicator = (CirclePageIndicator) view.
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(3 * density);


        NUM_PAGES = ImagesArray.size();


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
        }, 7000, 7000);

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
    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<TopCollectionModel> topCollectionModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList) {
        resultCallBackInterface.topSelling(topSellingModelArrayList);
        resultCallBackInterface.bestCollection(topCollectionModelArrayList);
        resultCallBackInterface.newArrivals(newArrivalModelArrayList);
    }

    @Override
    public void bannerlist(ArrayList<String> bannerlist1) {
        bannerlist.clear();

        for (int i = 0; i < bannerlist1.size(); i++) {
            bannerlist.add(bannerlist1.get(i));
        }
        init();
    }

    @Override
    public void grocerylist(ArrayList<GroceryHomeModel> arrayList) {
        Log.e("arrr", String.valueOf(arrayList.size()));
        resultCallBackInterface.grocery(arrayList);
    }
}
