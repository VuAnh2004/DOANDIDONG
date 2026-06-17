package api;

import java.util.List;
import model.KhoaBieuModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import okhttp3.ResponseBody;
public interface thoikhoabieuapi {
    // Dấu / ở đầu giúp bỏ qua BaseURL và gọi thẳng tới /api/thoikhoabieu/index
    @GET("/api/thoikhoabieu/index")
    Call<KhoaBieuModel.IndexResponse> getIndex(
            @Query("studentId") String studentId,
            @Query("semester") String semester
    );

    @GET("/api/thoikhoabieu/calendar")
    Call<KhoaBieuModel.CalendarResponse> getCalendar();

    @GET("/api/thoikhoabieu/week")
    Call<List<KhoaBieuModel>> getThoiKB(
            @Query("weekNumber") int weekNumber,
            @Query("semesterId") int semesterId
    );
    @GET("/api/thoikhoabieu/export")
    Call<ResponseBody> exportExcel(
            @Query("studentId") String studentId,
            @Query("semester") String semester
    );
}