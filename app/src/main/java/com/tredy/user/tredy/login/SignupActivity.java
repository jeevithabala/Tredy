package com.tredy.user.tredy.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shopify.buy3.QueryGraphCall;
import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.FilterSharedPreference;
import com.tredy.user.tredy.util.SharedPreference;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SignupActivity extends Activity implements TextWatcher {

    EditText first_name, last_name, mobile, email_id;
    TextInputEditText create_password;
    TextInputLayout firstNameInputLayout, lastNameInputLayout, emailInputLayout, mobileInputLayout, passwordInputLayout;
    Button submit_btn;
    String firstname, lastname, mobilenumber, email, password;
    private GraphClient graphClient;
    private ProgressDialog progressDialog;
    private String customerid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        graphClient = GraphClient.builder(this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        final String PHONE_REGEX = "^[0-9][0-9]{9}$";

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile = findViewById(R.id.number);
        email_id = findViewById(R.id.email_id);
        create_password = findViewById(R.id.create_password);

        firstNameInputLayout = findViewById(R.id.first_name_input_layout);
        lastNameInputLayout = findViewById(R.id.last_name_input_layout);
        ;
        mobileInputLayout = findViewById(R.id.mobile_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);

        submit_btn = (Button) findViewById(R.id.signup);

        first_name.addTextChangedListener(this);
        last_name.addTextChangedListener(this);
        email_id.addTextChangedListener(this);
        mobile.addTextChangedListener(this);
        create_password.addTextChangedListener(this);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid() == true) {
//                    Toast.makeText(SignupActivity.this,"GOT",Toast.LENGTH_SHORT).show();
                    firstname = first_name.getText().toString().trim();
                    lastname = last_name.getText().toString().trim();
                    mobilenumber = mobile.getText().toString().trim();
                    email = email_id.getText().toString().trim();
                    password = create_password.getText().toString().trim();

                    signingUpUser();
                } else {
//                    Toast.makeText(SignupActivity.this,"Not GOT",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (charSequence.hashCode() == first_name.getText().hashCode()) {
            if (first_name.getText().toString().isEmpty()) {
                firstNameInputLayout.setError("First name is empty");
            } else {
                firstNameInputLayout.setError(null);
            }
        } else if (charSequence.hashCode() == last_name.getText().hashCode()) {
            if (last_name.getText().toString().isEmpty()) {
                lastNameInputLayout.setError("Last name is empty");
            } else {
                lastNameInputLayout.setError(null);
            }

        } else if (charSequence.hashCode() == email_id.getText().hashCode()) {
            if (email_id.getText().toString().isEmpty()) {
                emailInputLayout.setError("Enter email id");
            } else {
                emailInputLayout.setError(null);
            }
        } else if (charSequence.hashCode() == mobile.getText().hashCode()) {
            if (mobile.getText().toString().isEmpty()) {
                mobileInputLayout.setError("Enter mobile no");
                if (mobile.getText().toString().length() == 0) {
                    mobileInputLayout.setError(null);
                }
            } else {
                mobileInputLayout.setError(null);
            }
        } else {
            if (create_password.getText().toString().isEmpty()) {
                passwordInputLayout.setError("Minimum 5 character is required");
            } else {
                passwordInputLayout.setError(null);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private boolean isValid() {
        boolean check = true;

        if (first_name.getText().toString().isEmpty()) {
            firstNameInputLayout.setError("Please Enter First Name");
            return false;
        }

        if (last_name.getText().toString().isEmpty()) {
            lastNameInputLayout.setError("Please Enter Last Name");
            return false;
        }

        if (email_id.getText().toString().isEmpty()) {
            emailInputLayout.setError("Please Enter Email");
            return false;
        }

        if (create_password.getText().toString().isEmpty()) {
            passwordInputLayout.setError("Minimum 5 character is required");
            return false;
        } else {
            if (create_password.getText().toString().length() >= 5) {
                check = isValidPassword(create_password.getText().toString());
            } else {
                passwordInputLayout.setError("Minimum 5 character is required");
                return false;
            }


        }

        if (mobile.getText().toString().length() != 0) {
            if (mobile.getText().toString().length() == 10) {
                check = isValidMobile(mobile.getText().toString());
            } else {
                mobileInputLayout.setError("Please Enter 10 Digit Mobile Number");
                return false;
            }
        } else if (mobile.getText().toString().length() == 0) {
            check = true;
        }


        if (isValidMail(email_id.getText().toString())) {
            check = true;
        } else {
            emailInputLayout.setError("Please Enter Email");
            return false;
        }

        return check;
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 1) {
                // if(phone.length() != 10) {
                check = false;
                mobileInputLayout.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private boolean isValidPassword(String pass) {
        boolean check = false;
        if (pass.length() >= 5) {
            check = true;
        } else {
            check = false;
        }
        return check;
    }

    private void signingUpUser() {
        if (Config.isNetworkAvailable(Objects.requireNonNull(getApplicationContext()))) {
            checkCustomer(email.trim(), password.trim());
        } else {
            Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }

    }


    public void checkCustomer(String email, String password) {
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

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

                if (response.data() != null) {


                    if (response.data().getCustomerAccessTokenCreate() != null && response.data().getCustomerAccessTokenCreate().getCustomerAccessToken() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(progressDialog!=null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        });

                        String token = "" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken().toString();
                        String expire = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getExpiresAt().toString();
                        SharedPreference.saveData("accesstoken", token.trim(), getApplicationContext());

                        SharedPreference.saveData("email", email.trim(), getApplicationContext());
                        SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
                        SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());

//                        Intent i = new Intent(getApplicationContext(), Navigation.class);
//                        SharedPreference.saveData("login", "true", getApplicationContext());
//                        startActivity(i);
                        if (token.trim().length() > 0) {
                            getId(token.trim());
                        }
                    } else {

                        create();
                    }

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }


        });
    }

    public void create() {

        SharedPreference.saveData("email", email.trim(), getApplicationContext());
        SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
        SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());

        if (mobilenumber.trim().length() == 0) {


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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            if (response.data().getCustomerCreate().getUserErrors() != null && response.data().getCustomerCreate().getUserErrors().size() != 0) {
                                String error = response.data().getCustomerCreate().getUserErrors().get(0).getMessage();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Config.Dialog(error, SignupActivity.this);
                                    }
                                });

                            } else {

                                customerid = response.data().getCustomerCreate().getCustomer().getId().toString();
                                String email = response.data().getCustomerCreate().getCustomer().getEmail();

                                if (customerid != null) {
                                    saveToken();
                                    //                                Intent i = new Intent(getApplicationContext(), Navigation.class);
                                    //                                SharedPreference.saveData("login", "true", getApplicationContext());
                                    //                                startActivity(i);
                                } else {
                                }
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Config.Dialog("Try Again Later", SignupActivity.this);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull GraphError error) {
//
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Config.Dialog("Try Again Later", SignupActivity.this);
                        }
                    });
                }


            });


        } else {

            SharedPreference.saveData("email", email.trim(), getApplicationContext());
            SharedPreference.saveData("firstname", firstname.trim(), getApplicationContext());
            SharedPreference.saveData("lastname", lastname.trim(), getApplicationContext());
            SharedPreference.saveData("mobile", mobilenumber.trim(), getApplicationContext());

            if (mobilenumber.trim().length() == 10) {
                mobilenumber = "+91" + mobilenumber;


            }

            Storefront.CustomerCreateInput input = new Storefront.CustomerCreateInput(email.trim(), password.trim())
                    .setFirstName(firstname)
                    .setLastName(lastname)
                    .setEmail(email.trim())
                    .setPhone(mobilenumber.trim())
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
//                Log.e("response", response.toString());

                    if (response.data().getCustomerCreate() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                        if (response.data().getCustomerCreate().getCustomer() == null || response.data().getCheckoutCreate() == null) {
                            if (response.data().getCustomerCreate().getUserErrors().size() != 0) {
                                String message = response.data().getCustomerCreate().getUserErrors().get(0).getMessage();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Config.Dialog(message, SignupActivity.this);
                                    }
                                });
                            }
                        } else {

                            customerid = response.data().getCustomerCreate().getCustomer().getId().toString();
                            String email = response.data().getCustomerCreate().getCustomer().getEmail();

                            if (customerid != null && customerid.trim().length() > 0) {
                                saveToken();
                            }
                        }
                    } else {
                        if (response.data() == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Config.Dialog("Please try again later.", SignupActivity.this);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Config.Dialog("Please try again later.", SignupActivity.this);
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
                            progressDialog.dismiss();
                            Config.Dialog("Try Again Later", SignupActivity.this);
                        }
                    });

                }


            });
        }
    }

    public void saveToken() {
        String token = FilterSharedPreference.getData("firebasetoken", getApplicationContext());
//        Log.e("tokennn", " " + token);
//        Log.e("customer_id", " " + customerid);

        if (token.trim().length() > 0) {
            byte[] data = Base64.decode(customerid, Base64.DEFAULT);
            try {
                customerid = new String(data, "UTF-8");
                String[] separated = customerid.split("/");
                customerid = separated[4]; // this will contain "Customer id"
                Log.e("customer_id", " " + customerid);
                SharedPreference.saveData("customerid", customerid, getApplicationContext());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("customer_id", customerid.trim());
//            jsonBody.put("registration_token", token);


//            Log.d("check JSON", jsonBody.toString());


                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.savetoken, response -> {
//                Log.e("tokenresponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        SharedPreference.saveData("update", "false", getApplicationContext());
//                    Intent i = new Intent(getApplicationContext(), Navigation.class);
//                    SharedPreference.saveData("login", "true", getApplicationContext());
//                    startActivity(i);
//                    finish();
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
                if (response.data() != null && response.data().getCustomer() != null) {
                    customerid = response.data().getCustomer().getId().toString();
                    saveToken();
                } else {
                    if (response.data() != null && response.data().getCustomer() == null) {
                        runOnUiThread(() -> Config.Dialog("Please try again later", SignupActivity.this));
                    }
                }


            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });


    }


}
