package api;

import java.util.List;
import model.AddressModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AddressApi {
    @GET("api/v2/")
    Call<List<AddressModel>> getProvinces();

    @GET("api/v2/p/{code}")
    Call<AddressModel> getWards(@Path("code") int provinceCode, @Query("depth") int depth);
}