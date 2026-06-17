package api;

import model.ForgotPasswordRequest;
import model.LoginRequest;
import model.LoginResponse;
import model.ResetPasswordRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface loginapi {

    // ================================================================
    // LOGIN BẰNG MẬT KHẨU
    // ================================================================
    @POST("api/loginmobile/mobile")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // ================================================================
    // LOGIN BẰNG PASSKEY (VÂN TAY / FACE ID) - QUAN TRỌNG
    // ================================================================
    @FormUrlEncoded
    @POST("api/loginmobile/login-passkey")
    Call<LoginResponse> loginWithPasskey(
            @Field("username") String username,
            @Field("deviceId") String deviceId
    );

    // ================================================================
    // QUÊN MẬT KHẨU
    // ================================================================
    @POST("api/loginmobile/forgot-password")
    Call<ResponseBody> forgotPassword(@Body ForgotPasswordRequest request);

    // ================================================================
    // ĐẶT LẠI MẬT KHẨU
    // ================================================================
    @POST("api/loginmobile/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);

    // ================================================================
    // ĐĂNG XUẤT
    // ================================================================
    @FormUrlEncoded
    @POST("api/loginmobile/logout")
    Call<ResponseBody> logout(@Field("username") String username);

    // ================================================================
    // KIỂM TRA TOKEN CÒN HIỆU LỰC
    // ================================================================
    @FormUrlEncoded
    @POST("api/loginmobile/verify-token")
    Call<ResponseBody> verifyToken(@Field("token") String token);
}