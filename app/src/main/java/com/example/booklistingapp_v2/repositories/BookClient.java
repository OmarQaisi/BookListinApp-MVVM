package com.example.booklistingapp_v2.repositories;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookClient {

    private static final String BASE_URL = "https://www.googleapis.com/";
    private BookApi mBookApi;
    private static BookClient instance;

    public BookClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mBookApi = retrofit.create(BookApi.class);
    }

    public static BookClient getInstance() {
        if(instance == null){
            instance = new BookClient();
        }
        return instance;
    }

    public Call<JsonObject> getBooks(String bookName, int maxResult){
        return mBookApi.getBooks(bookName, maxResult);
    }

}
