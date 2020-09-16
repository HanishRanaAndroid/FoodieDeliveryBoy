package com.valle.deliveryboyfoodieapp.network;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.REGISTER)
    @FormUrlEncoded
    Observable<String> register(@Field("Full_Name") String Full_Name, @Field("Mobile") String Mobile, @Field("Email") String Email,
                                @Field("Password") String Password, @Field("Device_Token") String Device_Token, @Field("Role") String Role);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.LOGIN)
    @FormUrlEncoded
    Observable<String> login(@Field("FieldType") String FieldType, @Field("Mobile") String Mobile, @Field("Password") String Password,
                             @Field("Device_Token") String Device_Token, @Field("Role_Type") String Role_Type);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.SEND_OTP)
    @FormUrlEncoded
    Observable<String> sendOTPtoUser(@Field("Mobile") String Mobile);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_USER_BY_FILTER)
    @FormUrlEncoded
    Observable<String> getPresenceStatus(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.VERIFY_ACCOUNT)
    @FormUrlEncoded
    Observable<String> verifyAccount(@Field("Mobile") String Mobile, @Field("Code") String Code,@Field("Status") String Status);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.CHNAGE_PASSWORD)
    @FormUrlEncoded
    Observable<String> changePassword(@Field("User_Id") String User_Id, @Field("oldPassword") String oldPassword, @Field("newPassword")
            String newPassword);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.FORGET_PASSWORD)
    @FormUrlEncoded
    Observable<String> forgetPassword(@Field("Mobile") String Mobile, @Field("Password") String Password);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @Multipart
    @POST(Apis.EDIT_PROFILE)
    Call<String> updateProfile(@Part MultipartBody.Part image, @Part("User_Id") RequestBody User_Id,
                               @Part("Full_Name") RequestBody Full_Name, @Part("Email") RequestBody Email,
                               @Part("Mobile") RequestBody Mobile);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.USER_PRESENCE)
    @FormUrlEncoded
    Observable<String> updateUserPresence(@Field("Where") String Where, @Field("Data") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.USER_PRESENCE)
    @FormUrlEncoded
    Observable<String> updateToken(@Field("Where") String Where, @Field("Data") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_ORDER_HISTORY)
    @FormUrlEncoded
    Observable<String> getOrderHistory(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_ONLY_NEW)
    @FormUrlEncoded
    Observable<String> getAssignedOrder(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_ORDER)
    @FormUrlEncoded
    Observable<String> getAcceptedOrders(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.ORDER_UPDATE)
    @FormUrlEncoded
    Observable<String> updateOrderStatus(@Field("Where") String Where, @Field("data") String data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_ORDER)
    @FormUrlEncoded
    Observable<String> getOrderDetails(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.CHAT_API)
    @FormUrlEncoded
    Observable<String> chatApi(@Field("Where") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.SUBMIT_CHAT)
    @FormUrlEncoded
    Observable<String> submitChatApi(@Field("Data") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.DELETE_CHAT)
    @FormUrlEncoded
    Observable<String> deleteChat(@Field("Where") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.ORDER_HISTORY)
    Observable<String> getOrdersHistory(@Field("Where") String Where, @Field("Last_Days") String Last_Days);
}
