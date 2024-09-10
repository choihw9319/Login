package kr.kwj.loogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/account/create/")
    Call<PostData> createPost(@Body PostData postData);

    @GET("/account/find/") // GET 요청 예제
    Call<GET> getUser(@Path("id") String userId);
}

