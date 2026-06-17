package api;

import model.ProfileResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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

    @POST("api/Profile/update-security")
    Call<ResponseBody> updateSecurity(
            @Query("studentId") String studentId,
            @Query("oldPassword") String oldPassword,
            @Query("newPassword") String newPassword,
            @Query("email") String email
    );

    @Multipart
    @POST("api/Profile/update-hosohoa")
    Call<ResponseBody> updateHosohoa(
            @Part("studentId") RequestBody studentId,
            @Part("docType") RequestBody docType,
            @Part("notes") RequestBody notes,
            @Part MultipartBody.Part file
    );
}