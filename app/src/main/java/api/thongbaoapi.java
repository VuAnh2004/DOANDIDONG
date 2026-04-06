package api;

import java.util.List;
import model.ThongBaoModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface thongbaoapi {

    @GET("api/thongbao/get-by-user")
    Call<ThongBaoResponse> getThongBao(@Query("userName") String userName);

    class ThongBaoResponse {
        public boolean success;
        public List<ThongBaoModel> data;
    }
}