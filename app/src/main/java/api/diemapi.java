package api;

import java.util.List;
import model.DiemModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface diemapi {
    @GET("api/Diem")
    Call<List<DiemModel>> getDiem(
            @Query("studentId") String studentId,
            @Query("year") String year,
            @Query("semester") String semester,
            @Query("subjectId") Integer subjectId
    );

    @GET("api/Diem/metadata")
    Call<MetadataResponse> getMetadata();

    class MetadataResponse {
        public List<String> years;
        public List<String> semesters;
        public List<DiemModel.SubjectOption> subjects;
    }
}