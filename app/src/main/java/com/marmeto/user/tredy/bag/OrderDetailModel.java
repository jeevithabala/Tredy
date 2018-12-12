package com.marmeto.user.tredy.bag;

import java.io.Serializable;

public class OrderDetailModel  implements Serializable{

    String emailstring, totalamount, firstname = "", lastname = "", bfirstname = "", blastname = "", address1 = "", city = "", state = "", country = "", zip = "", phone = "", b_address1 = "", b_city = "", b_state = "", b_country = "", b_zip = "", varientid,qty, discounted_price, discount_coupon;
String s_mobile,b_email,b_mobile;
    public OrderDetailModel() {
    }

    public OrderDetailModel(String emailstring, String totalamount, String firstname, String lastname, String bfirstname, String blastname, String address1, String city, String state, String country, String zip, String phone, String b_address1, String b_city, String b_state, String b_country, String b_zip, String varientid, String qty,String discounted_price,String discount_coupon,String s_mobile,String b_mobile,String b_email) {
        this.emailstring = emailstring;
        this.totalamount = totalamount;
        this.firstname = firstname;
        this.lastname = lastname;
        this.bfirstname = bfirstname;
        this.blastname = blastname;
        this.address1 = address1;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.phone = phone;
        this.b_address1 = b_address1;
        this.b_city = b_city;
        this.b_state = b_state;
        this.b_country = b_country;
        this.b_zip = b_zip;
        this.varientid = varientid;
        this.qty = qty;
        this.discounted_price=discounted_price;
        this.discount_coupon=discount_coupon;
        this.s_mobile=s_mobile;
        this.b_mobile=b_mobile;
        this.b_email=b_email;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public String getDiscount_coupon() {
        return discount_coupon;
    }

    public String getEmailstring() {
        return emailstring;
    }

    public String getTotalamount() {
        return totalamount;
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getBfirstname() {
        return bfirstname;
    }

    public String getBlastname() {
        return blastname;
    }

    public String getAddress1() {
        return address1;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getB_address1() {
        return b_address1;
    }

    public String getB_city() {
        return b_city;
    }

    public String getB_state() {
        return b_state;
    }

    public String getB_country() {
        return b_country;
    }

    public String getB_zip() {
        return b_zip;
    }

    public String getVarientid() {
        return varientid;
    }

    public String getQty() {
        return qty;
    }

    public String getS_mobile() {
        return s_mobile;
    }

    public String getB_email() {
        return b_email;
    }

    public String getB_mobile() {
        return b_mobile;
    }
}
