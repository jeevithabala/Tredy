package com.tredy.user.tredy.login;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.FilterSharedPreference;
import com.tredy.user.tredy.util.SharedPreference;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;

import com.tredy.user.tredy.R;
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
import java.util.Objects;
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
    Button facebook, btnSignIn;
    TextView signin, signup, forgot_password;
    EditText email_text;
    //    ProgressBar progressBar;
    private ProgressDialog progressDoalog;
    TextInputEditText etPassword;
    GoogleApiClient mGoogleApiClient;
    //    ProgressDialog mProgressDialog;
    //    SignInButton btnSignIn;
    Button btnSignOut, btnRevokeAccess;
    Boolean sociallogin = false;
//    private String personName = "";

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


        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnRevokeAccess = findViewById(R.id.btn_revoke_access);
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

        btnSignIn.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            } else {
                Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
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
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.e("facebook_accessToken", "" + accessToken);

                        //  SharedPreference.saveData("data", "success", MainActivity.this);


                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                (object, response) -> {
                                    try {
                                        sociallogin = true;

                                        // Bundle bFacebookData = getFacebookData(object);
                                        // email = response.getJSONObject().getString("email");
                                        firstname = object.getString("first_name");
                                        lastname = object.getString("last_name");
                                        email = object.getString("email");
                                        String id = object.getString("id");
                                        Log.e("LoginActivity", id);

//                                            gender = object.getString("gender");
//                                            birthday = object.getString("birthday");

//                                            Intent i = new Intent(getApplicationContext(), ProfiiView.class);
//                                            i.putExtra("name", name);
//                                            i.putExtra("email", email);
//                                            startActivity(i);
                                        SharedPreference.saveData("facebookid", id.trim(), getApplicationContext());
                                        SharedPreference.saveData("email", email.trim(), getApplicationContext());
                                        SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
                                        SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());
                                        String password1 = email;

                                        String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
                                        Log.e("coverted", password.trim());

                                        checkCustomer(email, password.trim());


                                    } catch (JSONException e) {
                                        Toast.makeText(LoginActiviy.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
//                        LoginManager.getInstance().logOut();
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActiviy.this, " " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        signup.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                Intent i = new Intent(LoginActiviy.this, SignupActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
            }

        });

        forgot_password.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
            }


        });


        facebook.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
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
        });


        signin.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                email = email_text.getText().toString().trim();
                password = Objects.requireNonNull(etPassword.getText()).toString().trim();
                if (email.trim().length() != 0) {
                    if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                        dialog("Please Enter Valid email");
//                            Toast.makeText(LoginActiviy.this, "Please Enter Valid email", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.trim().length() != 0) {
                            sociallogin = false;
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
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference


        String login = SharedPreference.getData("login", getApplicationContext());
//
        if (login.equals("true")) {
            SharedPreference.saveData("update", "false", getApplicationContext());
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

            if (acct != null) {
                firstname = acct.getGivenName();
                lastname = acct.getFamilyName();
                email = acct.getEmail();
                String password1 = email;

                String password = null;
                if (password1 != null) {
                    password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
                }
//                Log.e("firstname", " "+firstname);
//                Log.e("lastname", " "+lastname);
                if (email != null) {
                    checkCustomer(email.trim(), password.trim());
                }
            }

//            String personPhotoUrl = acct.getPhotoUrl().toString();

        } else {
            Log.e("erroer", " " + result.toString());
        }
    }

    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RC_SIGN_IN) {
            sociallogin = true;
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, data);

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

                    if (response.data().getCustomerAccessTokenCreate().getUserErrors().size() > 0) {
                        String message = response.data().getCustomerAccessTokenCreate().getUserErrors().get(0).getMessage();
                        if (message.trim().equalsIgnoreCase("unidentified customer")) {
                            if (sociallogin) {
                                usercreate(email, password);
                            } else {
                                runOnUiThread(() -> {
                                    progressDoalog.dismiss();
                                    Config.Dialog("The email or password you entered is incorrect.", LoginActiviy.this);

                                    if (mGoogleApiClient.isConnected()) {
                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                    }
                                });

                            }
                        } else {
                            runOnUiThread(() -> {
                                progressDoalog.dismiss();
                                Config.Dialog("The email or password you entered is incorrect.", LoginActiviy.this);

                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                }
                            });


                        }

                    } else {

                        if (response.data().getCustomerAccessTokenCreate().getCustomerAccessToken() != null) {

                            String token = "" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken();
//                            String expire = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getExpiresAt().toString();
                            SharedPreference.saveData("accesstoken", token.trim(), getApplicationContext());
                            getId(token.trim());

                        } else {
                            runOnUiThread(() -> {
                                progressDoalog.dismiss();
                                Config.Dialog("The email or password you entered is incorrect.", LoginActiviy.this);

                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                }
                            });
                        }
                    }
                } else {
                    runOnUiThread(() -> {
                        progressDoalog.dismiss();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        }
                        Config.Dialog("Please try again later", LoginActiviy.this);
                    });

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(() -> {
                    progressDoalog.dismiss();
                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    }
                    Config.Dialog("Please try again later", LoginActiviy.this);

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
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.white);
//            alert.getWindow().setBackgroundDrawableResource(android.R.color.white)
    }

    public void saveToken() {
        String token = FilterSharedPreference.getData("firebasetoken", getApplicationContext());
        if (token.trim().length() > 0) {


            byte[] data = Base64.decode(customerid, Base64.DEFAULT);
            try {
                customerid = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] separated = customerid.split("/");
            customerid = separated[4]; // this will contain "Customer id"
            SharedPreference.saveData("customerid", customerid, getApplicationContext());


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("customer_id", customerid.trim());
                jsonBody.put("registration_token", token);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.savetoken, response -> {
                    Log.e("tokenresponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        SharedPreference.saveData("update", "false", getApplicationContext());
                        Intent i = new Intent(getApplicationContext(), Navigation.class);
                        SharedPreference.saveData("login", "true", getApplicationContext());
                        startActivity(i);
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("VOLLEY", " " + error.toString())) {
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
//                    String statusCode = String.valueOf(response.statusCode);
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
    }

    private void getId(String token) {

        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(token, Storefront.CustomerQuery::id

                )
        );
        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.QueryRoot> response) {
//                Log.e("data", "user..." + response.data().getCustomer().getId());
                runOnUiThread(() -> progressDoalog.dismiss());
                if (response.data() != null && response.data().getCustomer() != null) {
                    customerid = response.data().getCustomer().getId().toString();
//                    saveToken();
                    byte[] data = Base64.decode(customerid, Base64.DEFAULT);
                    try {
                        customerid = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String[] separated = customerid.split("/");
                    customerid = separated[4]; // this will contain "Customer id"

                    SharedPreference.saveData("customerid", customerid, getApplicationContext());
                    SharedPreference.saveData("update", "false", getApplicationContext());
                    Intent i = new Intent(getApplicationContext(), Navigation.class);
                    SharedPreference.saveData("login", "true", getApplicationContext());
                    startActivity(i);
                    finish();

                } else {
                    if (response.data() != null && response.data().getCustomer() == null) {
                        runOnUiThread(() -> Config.Dialog("Please try again later", LoginActiviy.this));
                    }
                }


            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(() -> progressDoalog.dismiss());

            }
        });


    }


    public void usercreate(String email, String password) {

        Storefront.CustomerCreateInput input = new Storefront.CustomerCreateInput(email.trim(), password.trim())
                .setFirstName(firstname)
                .setLastName(lastname)
                .setEmail(email.trim())
                .setAcceptsMarketing(true);
        //  .setPhone(Input.value("1-123-456-7890"));

        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerCreate(input, query -> query
                        .customer(customer -> customer
                                .id()
                                .email()
                                .firstName()

                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );


        graphClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {


            @Override
            public void onResponse(@NonNull com.shopify.buy3.GraphResponse<Storefront.Mutation> response) {

                if (response.data() != null) {
                    if (response.data().getCustomerCreate() != null) {
                        runOnUiThread(() -> progressDoalog.dismiss());
                        if (response.data().getCustomerCreate().getUserErrors() != null && response.data().getCustomerCreate().getUserErrors().size() != 0) {
                            String error = response.data().getCustomerCreate().getUserErrors().get(0).getMessage();
                            runOnUiThread(() -> {
                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                }
                                Config.Dialog(error, LoginActiviy.this);
                            });

                        } else {

                            String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                            String email = response.data().getCustomerCreate().getCustomer().getEmail();

                            if (id != null) {
                                runOnUiThread(() -> checkCustomer(email.trim(), password.trim()));
                            }
                        }
                    } else {
                        runOnUiThread(() -> {
                            if (mGoogleApiClient.isConnected()) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            }
                            Config.Dialog("Try Again Later", LoginActiviy.this);
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
//
                runOnUiThread(() -> {
                    progressDoalog.dismiss();
                    Config.Dialog("Try Again Later", LoginActiviy.this);
                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    }
                });
            }


        });


    }


}