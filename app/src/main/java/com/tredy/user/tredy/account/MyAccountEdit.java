package com.tredy.user.tredy.account;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.SharedPreference;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created By Jeevitha 
 */

public class MyAccountEdit extends Fragment {
    EditText firstname, lastname, email, mobilenumber;
    String emailtext, mobiletext, firstnametext, lastnametext;
    private String accessToken;
    private GraphClient graphClient;
    TextView save;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.myaccountedit, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(" Edit Account");

        firstname = view.findViewById(R.id.first_name);
        lastname = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        mobilenumber = view.findViewById(R.id.mobile_number);
//        password=view.findViewById(R.id.password);
        save=view.findViewById(R.id.save);


        assert getArguments() != null;
        emailtext = getArguments().getString("email");
        mobiletext = getArguments().getString("mobile");
        firstnametext = getArguments().getString("firstname");
        lastnametext = getArguments().getString("lastname");

        firstname.setText(firstnametext);
        lastname.setText(lastnametext);
        email.setText(emailtext);
        if(mobiletext!=null) {
            if (mobiletext.length() > 0) {
                mobiletext = mobiletext.substring(3, 13);
            }
        }
        mobilenumber.setText(mobiletext);
        accessToken = SharedPreference.getData("accesstoken", getActivity());
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

        save.setOnClickListener(view -> {
//                Boolean valid=true;
            if (accessToken != null) {
                emailtext=email.getText().toString().trim();
                mobiletext=mobilenumber.getText().toString().trim();
                firstnametext=firstname.getText().toString().trim();
                lastnametext=lastname.getText().toString().trim();
                if(mobiletext.length()>0)
                {
                    if(mobiletext.length()<10) {
//                            Toast.makeText(getActivity(), "Please Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                        Config.Dialog("Please Enter 10 Digit Mobile Number", getActivity());

//                            valid = false;
                    }
                    else if(mobiletext.length()==10)
                    {
//                            valid=true;
                        mobiletext=("+91"+mobiletext).trim();
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("loading, please wait...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                            update(accessToken);
                        } else {
                            Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else
                {
                    Config.Dialog("Please Enter Mobile Number", getActivity());

//                        valid=false;
//                        Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();

                }
            }
            });
    }

    public void update(String accessToken) {
        Storefront.CustomerUpdateInput input = new Storefront.CustomerUpdateInput()
                .setFirstName(firstnametext)
                .setLastName(lastnametext)
                .setEmail(emailtext)
//                .setPassword(passwordtext)
                .setPhone(mobiletext);

        Storefront.MutationQuery mutationQuery1 = Storefront.mutation(mutation -> mutation


                .customerUpdate(accessToken.trim(), input, arg -> arg
                        .customer(cus -> cus
                                .phone()
                                .email()
                                .firstName()
                                .lastName()
                                .id()
                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );
        graphClient.mutateGraph(mutationQuery1).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.data() != null) {
                    if(response.data().getCustomerUpdate()!=null && response.data().getCustomerUpdate().getCustomer()!=null) {
                        String phone = response.data().getCustomerUpdate().getCustomer().getPhone();
                        String firstName = response.data().getCustomerUpdate().getCustomer().getFirstName();
                        String lastName = response.data().getCustomerUpdate().getCustomer().getLastName();
                        String email = response.data().getCustomerUpdate().getCustomer().getEmail();
                        String id=response.data().getCustomerUpdate().getCustomer().getId().toString();
                        Log.e("phone", ""+phone+firstName+lastName+email+id);

//                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                        transaction.replace(R.id.home_container, new MyAccount(), "account");
//                        transaction.addToBackStack("account");
//                        transaction.commit();
                       if(getActivity()!=null) {
                           getActivity().runOnUiThread(() -> {
                               progressDialog.dismiss();
                               Config.Dialog("Profile Changes updated.. It takes few minutes to update in your account", getActivity());

//                                   Toast.makeText(getActivity(), "Changes updated.. It takes few minutes to update in your account", Toast.LENGTH_SHORT).show();
                               if (getFragmentManager() != null) {
                                   getFragmentManager().popBackStack();
                               }

                           });
                       }



                    }else{

                        for (int i=0;i<response.data().getCustomerUpdate().getUserErrors().size();i++){
                            String phonecheck=response.data().getCustomerUpdate().getUserErrors().get(i).getMessage();
                            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Config.Dialog(phonecheck, getActivity());

//                                    Toast.makeText(getActivity(),phonecheck,Toast.LENGTH_SHORT).show();

                            });
                        }



                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                progressDialog.dismiss();

            }
        });
    }

}