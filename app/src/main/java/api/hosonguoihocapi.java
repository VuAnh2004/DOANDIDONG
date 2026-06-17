package api;

import model.hosonguoihocResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface hosonguoihocapi {

    // Cập nhật để nhận Object bao gồm success và student
    @GET("api/HoSo/detail")
    Call<hosonguoihocResponse> getStudentProfile(@Query("studentId") String studentId);

}