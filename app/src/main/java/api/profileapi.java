package api;

import model.ProfileResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface profileapi {

    @GET("api/Profile/detail")
    Call<ProfileResponse> getProfile(@Query("studentId") String studentId);

    @Multipart
    @POST("api/Profile/update")
    Call<ResponseBody> updateProfile(
            @Part("StudentID") RequestBody studentId,
            @Part("FullName") RequestBody fullName,
            @Part("Birth") RequestBody birth,
            @Part("Gender") RequestBody gender,
            @Part("Address") RequestBody address,
            @Part("NumberPhone") RequestBody phone,
            @Part("Province") RequestBody province,
            @Part("Commune") RequestBody commune,
            @Part("Hamlet") RequestBody hamlet,
            @Part MultipartBody.Part imageFile
    );

    @FormUrlEncoded
    @POST("api/Profile/update-security")
    Call<ResponseBody> updateSecurity(
            @Field("studentId") String studentId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword,
            @Field("email") String email
    );

    @Multipart
    @POST("api/Profile/update-hosohoa")
    Call<ResponseBody> updateHosohoa(
            @Part("studentId") RequestBody studentId,
            @Part("docType") RequestBody docType,
            @Part("notes") RequestBody notes,
            @Part MultipartBody.Part file
    );

    // ====== PASSKEY API ======

    @FormUrlEncoded
    @POST("api/Profile/remove-passkey")
    Call<ResponseBody> removePasskey(
            @Field("studentId") String studentId
    );

    @GET("api/Profile/passkey-status")
    Call<ResponseBody> getPasskeyStatus(@Query("studentId") String studentId);

    // ⭐ THÊM API ĐĂNG KÝ PASSKEY
    @FormUrlEncoded
    @POST("api/Profile/register-passkey")
    Call<ResponseBody> registerPasskey(
            @Field("studentId") String studentId,
            @Field("credentialId") String credentialId,
            @Field("publicKey") String publicKey
    );
}