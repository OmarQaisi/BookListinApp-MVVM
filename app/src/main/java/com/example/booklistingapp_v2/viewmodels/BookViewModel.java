package com.example.booklistingapp_v2.viewmodels;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booklistingapp_v2.models.Book;
import com.example.booklistingapp_v2.repositories.BookClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookViewModel extends ViewModel {

    private static final String TAG = "BookViewModel";
    private MutableLiveData<List<Book>> books = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEmpty = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();

    public void getBooks(String bookName, int maxResult){
        mIsUpdating.setValue(true);
        mIsEmpty.setValue(false);

        BookClient.getInstance().getBooks(bookName,maxResult).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                List<Book> dataSet = new ArrayList<>();

                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    mIsUpdating.setValue(false);
                    return;
                }

                if(response.body().get("totalItems").getAsInt() == 0){
                    mIsUpdating.setValue(false);
                    mIsEmpty.setValue(true);
                    return;
                }

                try {
                    JsonObject root = response.body();
                    JsonArray booksArray = root.getAsJsonArray("items");
                    for (int i=0; i<booksArray.size(); i++){
                        JsonObject currentObject = booksArray.get(i).getAsJsonObject();
                        JsonObject info = currentObject.getAsJsonObject("volumeInfo");
                        JsonElement bookTitle = info.get("title");
                        if(info.has("averageRating") && info.has("description")){
                            JsonElement bookDescription= info.get("description");
                            JsonElement bookRating = info.get("averageRating");
                            JsonElement bookURL = info.get("infoLink");
                            JsonElement bookRatingReviews = info.get("ratingsCount");
                            JsonElement bookPageCount = info.get("pageCount");
                            String bookCategories= "";
                            if(info.has("categories")){
                                JsonArray categories = info.getAsJsonArray("categories");
                                JsonElement bookCategoriesElement = categories.get(0);
                                bookCategories = bookCategoriesElement.getAsString();
                            }
                            JsonObject imageLinks = info.getAsJsonObject("imageLinks");
                            JsonElement bookThumbnail = imageLinks.get("thumbnail");
                            JsonElement bookPublishedDate = info.get("publishedDate");
                            JsonElement bookAuthor;
                            if(info.has("authors")){
                                JsonArray authors = info.getAsJsonArray("authors");
                                bookAuthor = authors.get(0);
                            }else
                                bookAuthor = info.get("publisher");

                            dataSet.add(new Book(bookTitle.getAsString(),
                                    bookAuthor.getAsString(),
                                    bookDescription.getAsString(),
                                    bookThumbnail.getAsString(),
                                    bookURL.getAsString(),
                                    bookCategories,
                                    bookPublishedDate.getAsString(),
                                    bookRating.getAsDouble(),
                                    bookRatingReviews.getAsInt(),
                                    bookPageCount.getAsInt()));
                        }
                    }
                }catch (JsonParseException e){
                    Log.e(TAG, "Problem parsing the book JSON results", e);
                }

                if(dataSet.size() == 0){
                    mIsEmpty.setValue(true);
                } else{
                    mIsEmpty.setValue(false);
                    books.setValue(dataSet);
                }
                mIsUpdating.setValue(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }//getBooks

    public LiveData<List<Book>> getBooksLiveData(){
        return books;
    }

    public LiveData<Boolean> getIsUpdating(){
        return mIsUpdating;
    }

    public LiveData<Boolean> getIsEmpty(){
        return mIsEmpty;
    }

    public LiveData<Boolean> getIsConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mIsConnected.setValue(activeNetwork != null && activeNetwork.isConnectedOrConnecting());

        return mIsConnected;
    }

}//BookViewModel
