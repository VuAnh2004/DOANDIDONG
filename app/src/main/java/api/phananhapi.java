package api;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface phananhapi {

    @Multipart
    @POST("api/PhanAnh/Taodon")
    Call<ResponseBody> guiPhanAnh(
            @Part("StudentID") RequestBody studentId,
            @Part("Content") RequestBody content,
            @Part List<MultipartBody.Part> files
    );
}