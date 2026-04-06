package api;

import java.util.List;
import model.BookingModel;
import model.BookingRequest;
import model.RoomModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface muonphongapi {

    @GET("api/Dangkymuonphong/rooms")
    Call<List<RoomModel>> getRooms();

    @GET("api/Dangkymuonphong/bookings")
    Call<List<BookingModel>> getBookings(@Query("studentId") String studentId);

    @POST("api/Dangkymuonphong/book")
    Call<ResponseBody> bookRoom(@Body BookingRequest request);

    @POST("api/Dangkymuonphong/return")
    Call<ResponseBody> returnRoom(@Query("requestId") int requestId, @Query("userName") String userName);
}