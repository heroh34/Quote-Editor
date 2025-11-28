package com.editor.app.api;

import com.editor.app.api.models.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WallpaperService {
    @GET("napi/search/{type}")
    Call<SearchResponse> getSearchMedia(
            @Path("type") String type,  // Dynamic topic name
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("orientation") String orientation,
            @Query("query") String query
    );
}
