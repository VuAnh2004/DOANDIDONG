package api;

import java.util.List;
import model.HocPhiModel;
import model.NghiaVuModel;
import model.PaymentInformationModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface hocphiapi {

    @GET("api/HocPhi/dashboard")
    Call<HocPhiModel> getDashboard(@Query("studentId") String studentId);

    @GET("api/HocPhi/nghia-vu")
    Call<NghiaVuModel> getNghiaVu(@Query("studentId") String studentId);

    @GET("api/HocPhi/lich-su-nop")
    Call<List<LichSuThanhToan>> getLichSuNop(@Query("studentId") String studentId);

    @POST("api/HocPhi/create-payment")
    Call<PaymentUrlResponse> createPayment(@Body PaymentInformationModel model);

    class LichSuThanhToan {
        public String OrderDescription;
        public double Amount;
        public String Date;
        public String Method;
    }

    class PaymentUrlResponse {
        public String paymentUrl; // Khớp với { paymentUrl = url } bên ASP.NET
    }
}