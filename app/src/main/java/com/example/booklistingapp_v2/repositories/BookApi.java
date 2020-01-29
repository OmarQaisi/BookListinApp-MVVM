package com.example.booklistingapp_v2.repositories;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookApi {

    @GET("books/v1/volumes")
    Call<JsonObject> getBooks(@Query("q") String name, @Query("maxResult") int max);
}
