package io.github.httpmattpvaughn.hnapp.data;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public interface HackerNewsService {

    public final static String BASE_URL = "https://hacker-news.firebaseio.com/v0/";

    @GET("item/{id}.json")
    Call<Story> item(
            @Path("id") int id
    );

    @GET("topstories.json")
    Call<Integer[]> topStories();


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
