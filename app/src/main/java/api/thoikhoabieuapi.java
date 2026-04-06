package api;

import java.util.List;
import model.KhoaBieuModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface thoikhoabieuapi {

    // Cập nhật: Thêm tham số semesterCode để khớp với lệnh gọi trong Activity và API Server
    @GET("api/ThoiKB/index")
    Call<KhoaBieuModel.IndexResponse> getIndex(
            @Query("userName") String userName,
            @Query("semesterCode") String semesterCode
    );

    @GET("api/ThoiKB/schedule-by-week")
    Call<List<KhoaBieuModel>> getThoiKB(
            @Query("weekNumber") int weekNumber,
            @Query("semesterId") int semesterId,
            @Query("userName") String userName
    );
}