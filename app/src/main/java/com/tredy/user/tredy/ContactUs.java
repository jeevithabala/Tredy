package com.tredy.user.tredy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.Objects;

public class ContactUs extends Fragment {
    ImageView facebook;
    Intent intent;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.contactus, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Contact Us");
        facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(view1 -> {
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1923175201243047/"));
                startActivity(intent);
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Tredy-Foods-1923175201243047/"));
                startActivity(intent);
            }
        });




        return view;
    }
}

