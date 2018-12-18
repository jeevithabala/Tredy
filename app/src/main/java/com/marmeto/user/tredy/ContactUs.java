package com.marmeto.user.tredy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.ImageView;

import com.marmeto.user.tredy.util.SharedPreference;

public class ContactUs extends Fragment {
    ImageView facebook;
    Intent intent;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.contactus, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Contact Us");
        facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1923175201243047/"));
                    startActivity(intent);
                } catch (Exception e) {
                    intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Tredy-Foods-1923175201243047/"));
                    startActivity(intent);
                }
            }
        });




        return view;
    }
}

