package com.tredy.user.tredy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class Tawk extends Fragment {
    WebView webView;
    String download_link = "https://tawk.to/chat/5979ec025dfc8255d623f3ee/default";
    private ProgressDialog progressDoalog;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tawk_fragment, container, false);

        webView = view. findViewById(R.id.webView);

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMessage("loading....");
        progressDoalog.setTitle("Processing");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();

        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        webView.loadUrl(download_link);
        progressDoalog.dismiss();


    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Chat");

    }
}
