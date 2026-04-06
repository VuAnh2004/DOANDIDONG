package api;

import java.util.List;
import model.VietDonModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface vietdonapi {

    @GET("api/vietdonhocsinhcontorller")
    Call<List<VietDonModel>> getDanhSachDon(@Query("studentId") String studentId);

    @Multipart
    @POST("api/vietdonhocsinhcontorller/create")
    Call<ResponseBody> taoDon(
            @Part("StudentID") RequestBody studentId,
            @Part("StartDate") RequestBody startDate,
            @Part("EndDate") RequestBody endDate,
            @Part("Reason") RequestBody reason,
            @Part MultipartBody.Part file
    );

    @GET("api/vietdonhocsinhcontorller/details/{id}")
    Call<VietDonModel> getDetails(@Path("id") int id);
}