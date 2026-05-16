package api;

import model.ForgotPasswordRequest;
import model.LoginRequest;
import model.LoginResponse;
import model.ResetPasswordRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface loginapi {
    // Đã sửa: Đường dẫn khớp với Backend C# loginmobileController
    @POST("api/loginmobile/mobile")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/loginmobile/forgot-password")
    Call<ResponseBody> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/loginmobile/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);
}
