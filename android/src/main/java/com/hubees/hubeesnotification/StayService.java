import retrofit2.Call;
import retrofit2.http.GET;

public interface StayService {
    @GET("stays/single/active")
    Call<StayResponse> getActiveStay();
}
