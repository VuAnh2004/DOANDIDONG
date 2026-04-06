package api;

import model.CauHinhResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface cauhinhapi {

    @GET("api/CauHinh/GetInitialData")
    Call<CauHinhResponse> getInitialData(@Query("username") String username);

    @POST("api/CauHinh/SaveConfig")
    Call<ResponseBody> saveConfig(
            @Query("username") String username,
            @Query("semester_code") String semesterCode,
            @Query("semester_name") String semesterName
    );
}