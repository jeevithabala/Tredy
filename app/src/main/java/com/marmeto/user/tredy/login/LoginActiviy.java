package com.marmeto.user.tredy.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.util.Config;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.FilterSharedPreference;
import com.marmeto.user.tredy.util.SharedPreference;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.marmeto.user.tredy.R;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class LoginActiviy extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 007;
    CallbackManager callbackManager;
    LoginButton login_button;
    String firstname = "", lastname = "", email = "", password;
    public String customerid = "";
    private GraphClient graphClient;
    Button facebook, google, btnSignIn;
    TextView signin, signup, forgot_password;
    EditText name_text, email_text;
    ProgressBar progressBar;
    private ProgressDialog progressDoalog;
    TextInputEditText etPassword;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog mProgressDialog;
    //    SignInButton btnSignIn;
    Button btnSignOut, btnRevokeAccess;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        Log.d("TAG", "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        try {
            // simulate a slow startup
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        SharedPreference.saveData("login", "true", getApplicationContext());

        graphClient = GraphClient.builder(LoginActiviy.this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


//        String login = SharedPreference.getData("login", getApplicationContext());

//            if (login.equals("true")) {
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(i);
//        }


        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        etPassword = findViewById(R.id.etPassword);

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
//        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
//        btnSignIn.setScopes(gso.getScopeArray());

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable() == true) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_button = findViewById(R.id.login_button);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        facebook = findViewById(R.id.facebookView);
        //  name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        forgot_password = findViewById(R.id.forgot_password);
        login_button.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        login_button.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()

                {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.e("facebook_accessToken", "" + accessToken);

                        //  SharedPreference.saveData("data", "success", MainActivity.this);


                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.e("LoginActivity", response.toString());
                                        try {

                                            // Bundle bFacebookData = getFacebookData(object);
                                            // email = response.getJSONObject().getString("email");
                                            firstname = object.getString("first_name");
                                            lastname = object.getString("last_name");
                                            email = object.getString("email");
                                            Log.e("name", "" + firstname + email);
//                                            gender = object.getString("gender");
//                                            birthday = object.getString("birthday");

//                                            Intent i = new Intent(getApplicationContext(), ProfiiView.class);
//                                            i.putExtra("name", name);
//                                            i.putExtra("email", email);
//                                            startActivity(i);
                                            SharedPreference.saveData("email", email.trim(), getApplicationContext());
                                            SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
                                            SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());
                                            String password1 = email;

                                            String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
                                            Log.e("coverted", password.trim());

                                            checkCustomer(email, password.trim());


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "first_name,last_name,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Log.e("name", "" + firstname + email);


                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable() == true) {
                    Intent i = new Intent(LoginActiviy.this, SignupActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable() == true) {
                    Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }


            }
        });


        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() == true) {
                    if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                        //Logged in so show the login button

                        LoginManager.getInstance().logOut();

                        login_button.performClick();

                    } else {
                        login_button.performClick();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable() == true) {
                    email = email_text.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    if (email.trim().length() != 0) {
                        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                            dialog("Please Enter Valid email");
//                            Toast.makeText(LoginActiviy.this, "Please Enter Valid email", Toast.LENGTH_SHORT).show();
                        } else {
                            if (password.trim().length() != 0) {
                                checkCustomer(email.trim(), password.trim());
                            } else {
                                dialog("Please enter password");
//                                Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        dialog("Please enter email");
//                        Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference


        String login = SharedPreference.getData("login", getApplicationContext());
//
        if (login.equals("true")) {
            Intent i = new Intent(getApplicationContext(), Navigation.class);
            startActivity(i);

            finish();
        }
    }

    //            Toast.makeText(getActivity(),"Press again to exit",Toast.LENGTH_SHORT);
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
            email = acct.getEmail();
            String password1 = email;

            String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
            Log.e("coverted", password.trim());
            checkCustomer(email.trim(), password.trim());
        } else {
            Log.e("erroer", result.toString());
        }
    }

    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    public void checkCustomer(String email, String password) {

        progressDoalog = new ProgressDialog(LoginActiviy.this);
        progressDoalog.setMessage("loading....");
        progressDoalog.setTitle("Processing");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
//
        Storefront.CustomerAccessTokenCreateInput input1 = new Storefront.CustomerAccessTokenCreateInput(email.trim(), password.trim());
        Storefront.MutationQuery mutationQuery1 = Storefront.mutation(mutation -> mutation
                .customerAccessTokenCreate(input1, query -> query
                        .customerAccessToken(customerAccessToken -> customerAccessToken
                                .accessToken()
                                .expiresAt()

                        )

                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )

                )
        );

        graphClient.mutateGraph(mutationQuery1).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.Mutation> response) {
//                Log.e("response", response.toString());

                if (response.data() != null) {


                    if (response.data().getCustomerAccessTokenCreate().getCustomerAccessToken() != null) {

                        String token = "" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken().toString();
                        String expire = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getExpiresAt().toString();
                        SharedPreference.saveData("accesstoken", token.trim(), getApplicationContext());
                        getId(token);

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDoalog.dismiss();
                                dialog("The email or password you entered is incorrect.");
                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                }
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDoalog.dismiss();
                            Config.Dialog("Please try again later", LoginActiviy.this);
                        }
                    });

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDoalog.dismiss();
                    }
                });
                Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

            }


        });

    }

    @Override
    public void onClick(View view) {

    }


    public void dialog(String poptext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActiviy.this);
//            builder.setTitle("Success");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
//            alert.getWindow().setBackgroundDrawableResource(android.R.color.white)
    }

    public void saveToken() {
        String token = FilterSharedPreference.getData("firebasetoken", getApplicationContext());
        Log.e("tokennn", " " + token);


        byte[] data = Base64.decode(customerid, Base64.DEFAULT);
        try {
            customerid = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] separated = customerid.split("/");
        customerid = separated[4]; // this will contain "Customer id"
        Log.e("customer_id", " " + customerid);
        SharedPreference.saveData("customerid", customerid, getApplicationContext());


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("customer_id", customerid.trim());
            jsonBody.put("registration_token", token);


            Log.d("check JSON", jsonBody.toString());


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.savetoken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("tokenresponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        Intent i = new Intent(getApplicationContext(), Navigation.class);
                        SharedPreference.saveData("login", "true", getApplicationContext());
                        startActivity(i);
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", " " + error.toString());

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                        return requestBody == null;
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    //TODO if you want to use the status code for any other purpose like to handle 401, 403, 404
                    String statusCode = String.valueOf(response.statusCode);
                    //Handling logic
                    return super.parseNetworkResponse(response);
                }
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getId(String token) {

        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(token, customer -> customer
                        .id()

                )
        );
        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.QueryRoot> response) {
                Log.e("data", "user..." + response.data().getCustomer().getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDoalog.dismiss();
                    }
                });
                if (response.data() != null && response.data().getCustomer() != null) {
                    customerid = response.data().getCustomer().getId().toString();
                    saveToken();
                } else {
                    if (response.data().getCustomer() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Config.Dialog("Please try again later", LoginActiviy.this);
                            }
                        });
                    }
                }


            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDoalog.dismiss();
                    }
                });

                Log.e("TAG", "Failed to execute query", error);
            }
        });


    }


}