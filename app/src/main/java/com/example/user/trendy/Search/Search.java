package com.example.user.trendy.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.trendy.R;

import java.util.ArrayList;

public class Search extends Fragment implements SearchView.OnQueryTextListener  {
    RecyclerView search_recycler;
    private SearchAdapter adapter;
    private ArrayList<SearchModel> searchlist=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search, container, false);

        search_recycler=view.findViewById(R.id.search_recycler);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        search_recycler.setLayoutManager(layoutManager1);
        search_recycler.setItemAnimator(new DefaultItemAnimator());


        adapter = new SearchAdapter( getActivity(),searchlist);
        search_recycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return view;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getActivity(), "Query Inserted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        adapter.filter(newText);
        return true;
    }

    }
