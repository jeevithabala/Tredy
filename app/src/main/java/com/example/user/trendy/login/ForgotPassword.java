package com.example.user.trendy.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.R;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class ForgotPassword extends AppCompatActivity {

    private EditText email_text;
    private String email;
    Button submit;
    private GraphClient graphClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        graphClient = GraphClient.builder(this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        email_text = findViewById(R.id.email_text);
        submit = findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = email_text.getText().toString().trim();
                if (email.trim().length() != 0) {
                    if (Validationemail.isEmailAddress(email_text, true)) {
                        forgotpassword();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void forgotpassword() {
        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerRecover(email.trim(), query -> query
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );

        graphClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.Mutation> response) {
                if (response.data() != null) {
                    if (response.data().getCustomerRecover() != null) {
                        if (response.data().getCustomerRecover().getUserErrors().size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog(response.data().getCustomerRecover().getUserErrors().get(0).getMessage());

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog("Password reset link is sent to your registered email ID");
                                }
                            });

                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog("Resetting password limit exceeded. Please try again later.");

                            }
                        });
                    }

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());
                progressDialog.dismiss();
            }


        });

    }

    public void dialog(String poptext) {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
//            builder.setTitle("Success");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
//            alert.getWindow().setBackgroundDrawableResource(android.R.color.white)
    }

}
