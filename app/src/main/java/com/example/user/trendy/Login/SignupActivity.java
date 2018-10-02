package com.example.user.trendy.Login;

import android.app.Activity;
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

import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.MainActivity;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.SharedPreference;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SignupActivity extends Activity implements TextWatcher {

    EditText first_name, last_name, mobile, email_id;
    TextInputEditText create_password;
    TextInputLayout firstNameInputLayout, lastNameInputLayout, emailInputLayout, mobileInputLayout, passwordInputLayout;
    Button submit_btn;
    String firstname, lastname, mobilenumber, email, password;
    private GraphClient graphClient;

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

                firstname = first_name.getText().toString();
                lastname = last_name.getText().toString();
                mobilenumber = mobile.getText().toString();
                email = email_id.getText().toString();
                password = create_password.getText().toString();


                if (firstname.isEmpty() && lastname.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    firstNameInputLayout.setError("First name is empty");
                    lastNameInputLayout.setError("Last name is empty");
//                    mobileInputLayout.setError("Enter mobile no");
                    emailInputLayout.setError("Enter email id");
                    passwordInputLayout.setError("password is empty");
                }
                if (!firstname.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !mobilenumber.isEmpty()
                        && !password.isEmpty() && Validationemail.isEmailAddress(email_id, true)
                        && Validationmobile.isPhoneNumber(mobile, true)) {


                    signingUpUser();

                }

                if (firstname.isEmpty()) {
                    firstNameInputLayout.setError("First name is empty");
                } else {
                    firstNameInputLayout.setError(null);
                }

                if (lastname.isEmpty()) {
                    lastNameInputLayout.setError("Last name is empty");
                } else {
                    lastNameInputLayout.setError(null);
                }

                if (!mobilenumber.isEmpty()) {
                    if (!Validationmobile.isPhoneNumber(mobile, true)) {
                        mobileInputLayout.setError("Invalid mobile");
//                } else {
//                    mobileInputLayout.setError(null);
                    }

                    if (email.isEmpty()) {
                        emailInputLayout.setError("Enter email id");
                    } else if (!Validationemail.isEmailAddress(email_id, true)) {
                        emailInputLayout.setError("Invalid email");
                    } else {
                        emailInputLayout.setError(null);
                    }

                    if (password.isEmpty()) {
                        passwordInputLayout.setError("password is empty");
//                    } else if (password.length() < 6) {
//                        passwordInputLayout.setError("Enter valid password");
//                    } else if (password.length() >= 6) {
//                        if (!isValidPassword(password)) {
//                            passwordInputLayout.setError("Enter valid password");
//                        } else {
//                            passwordInputLayout.setError(null);
//                        }
//                    } else {
//                        passwordInputLayout.setError(null);
                    }
                }
//                if(first_name.getText().toString().isEmpty())
//                {
//                    firstNameInputLayout.setError("Please Enter First Name");
//                }
//
//                if(last_name.getText().toString().isEmpty())
//                {
//                    lastNameInputLayout.setError("Please Enter Last Name");
//                }
//
//                if(email_id.getText().toString().isEmpty())
//                {
//                    emailInputLayout.setError("Please Enter Email");
//                }
//
//                if(create_password.getText().toString().isEmpty())
//                {
//                    passwordInputLayout.setError("Please Enter Password");
//                }
//
//                if(mobile.getText().toString().length()!=0) {
//                    if (isValidMobile(mobile.getText().toString())) {
//                        //GOT THE CORRECT EMAIL;
//                        Toast.makeText(SignupActivity.this, "GOT Phone Number", Toast.LENGTH_SHORT).show();
//                    } else {
//                        mobileInputLayout.setError("Please Enter mobile no ");
//                    }
//                }
//
//                if(isValidMail(email_id.getText().toString()))
//                {
//                    //GOT THE CORRECT EMAIL;
//                    Toast.makeText(SignupActivity.this,"GOT ",Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    emailInputLayout.setError("Please Enter Email");
//                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (charSequence.hashCode() == first_name.getText().hashCode()) {
            Log.e("hashcode", String.valueOf(first_name.getText().hashCode()));
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
            } else {
                mobileInputLayout.setError(null);
            }
        } else {
            if (create_password.getText().toString().isEmpty()) {
                passwordInputLayout.setError("password is empty");
            } else {
                passwordInputLayout.setError(null);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private void signingUpUser() {
        checkCustomer(email.trim(), password.trim());

    }


    public void checkCustomer(String email, String password) {
//        if (progressDoalog != null) {
//            progressDoalog = new ProgressDialog(LoginActiviy.this);
//            progressDoalog.setMessage("loading....");
//            progressDoalog.setTitle("Processing");
//            progressDoalog.setCancelable(true);
//            progressDoalog.show();
//        }


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
                        //                        if (progressDoalog != null) {
                        //                            progressDoalog.dismiss();
                        //                        }
                        String token = "" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken().toString();
                        String expire = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getExpiresAt().toString();
                        SharedPreference.saveData("accesstoken", token.trim(), getApplicationContext());

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        SharedPreference.saveData("login", "true", getApplicationContext());
                        startActivity(i);


                        Log.e("token", "" + token);
                        Log.e("expire", "" + expire);
                        //                    String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                        //                    String email = response.data().getCustomerCreate().getCustomer().getEmail();
                        //  Log.d("em", "Create Customer Info:" + email + ":" + id);
                    } else {
                        Log.e("token", "" + "empty");
                        create();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
//                if (progressDoalog != null) {
//                    progressDoalog.dismiss();
//                }
                Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

            }


        });
    }

    public void create() {
//
//        StringTokenizer st = new StringTokenizer(name, " "); //pass comma as delimeter
//        String firstname = st.nextToken();
//        String lastname = st.nextToken();
//        Log.e("firstname", firstname);
//        Log.e("lastname", lastname);

//            String password1 = email.trim();
//
//            String password = Base64.encodeToString(password1.getBytes(), Base64.DEFAULT).trim();
//            Log.e("coverted1", password.trim());
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
//                Log.e("response", response.toString());

                    if (response.data().getCustomerCreate() != null) {

                        String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                        String email = response.data().getCustomerCreate().getCustomer().getEmail();
                        Log.d("em", "Create Customer Info:" + email + ":" + id);

                        if (id != null) {
//                        if (progressDoalog != null) {
//                            progressDoalog.dismiss();
//                        }
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            SharedPreference.saveData("login", "true", getApplicationContext());
                            startActivity(i);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull GraphError error) {
//                if (progressDoalog != null) {
//                    progressDoalog.dismiss();
//                }
                    Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

                }


            });


        } else {
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

                        String id = response.data().getCustomerCreate().getCustomer().getId().toString();
                        String email = response.data().getCustomerCreate().getCustomer().getEmail();
                        Log.d("em", "Create Customer Info:" + email + ":" + id);

                        if (id != null) {
//                        if (progressDoalog != null) {
//                            progressDoalog.dismiss();
//                        }
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            SharedPreference.saveData("login", "true", getApplicationContext());
                            startActivity(i);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull GraphError error) {
//                if (progressDoalog != null) {
//                    progressDoalog.dismiss();
//                }
                    Log.d("fa", "Create customer Account API FAIL:" + error.getMessage());

                }


            });
        }
    }

}
