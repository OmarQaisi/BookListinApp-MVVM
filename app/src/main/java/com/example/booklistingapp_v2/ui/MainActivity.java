package com.example.booklistingapp_v2.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.booklistingapp_v2.R;
import com.example.booklistingapp_v2.adapters.BookAdapter;
import com.example.booklistingapp_v2.models.Book;
import com.example.booklistingapp_v2.viewmodels.BookViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mBookListView;
    private BookAdapter mAdapter;
    private Dialog myDialog;
    private EditText mEditText;
    private ImageButton mImageButton;
    private TextView mEmptyStateTextView, mStartingTextView;
    private ProgressBar mProgressBar;
    private BookViewModel mBookViewModel;
    private final int maxResult = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        mBookViewModel.getIsConnected(this).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean){
                    hideStartTextView();
                    showEmptyStateTextView();
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }else{
                    hideEmptyStateTextView();
                }
            }
        });

        mBookViewModel.getBooksLiveData().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                mAdapter.clear();
                mAdapter.addAll(books);
            }
        });

        mBookViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                }else{
                    hideProgressBar();
                }
            }
        });

        mBookViewModel.getIsEmpty().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showEmptyStateTextView();
                    mEmptyStateTextView.setText(R.string.no_books);
                }else{
                    hideEmptyStateTextView();
                }
            }
        });

    }//onCreate


    private void initView(){
        mEditText = findViewById(R.id.et_search);
        mImageButton = findViewById(R.id.img_search);

        mStartingTextView = findViewById(R.id.emoji_view);
        mStartingTextView.setText(getString(R.string.emoji));

        mProgressBar = findViewById(R.id.loading_indicator);
        mEmptyStateTextView  = findViewById(R.id.empty_view);

        mBookListView = findViewById(R.id.list);
        mBookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        mBookListView.setAdapter(mAdapter);

        myDialog = new Dialog(this);
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mAdapter.getItem(position);
                showPopup(view, currentBook);
            }
        });

        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setFocusableInTouchMode(true);
                return false;
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mImageButton.callOnClick();
                    return true;
                }
                return false;
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setFocusable(false);
                String search = mEditText.getText().toString();
                search = search.replace(' ','+');
                if(mStartingTextView.getVisibility() == View.VISIBLE){
                    hideStartTextView();
                }
                mBookViewModel.getBooks(search, maxResult);
            }
        });
    }//initView


    private void showPopup(View v, final Book currentBook){
        myDialog.setContentView(R.layout.layout_dialog);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = myDialog.findViewById(R.id.dialog_title);
        title.setText(currentBook.getTitle());

        ImageView img = myDialog.findViewById(R.id.dialog_img);
        Picasso.get().load(currentBook.getImageUrl()).fit().into(img);

        TextView author = myDialog.findViewById(R.id.dialog_author);
        author.setText("by "+currentBook.getAuthor());

        RatingBar rating = myDialog.findViewById(R.id.dialog_rating);
        rating.setRating((float)currentBook.getRating());

        TextView ratingReviews = myDialog.findViewById(R.id.dialog_rating_reviews);
        ratingReviews.setText("("+currentBook.getRatingCount()+")");

        TextView category = myDialog.findViewById(R.id.dialog_catogry);
        category.setText(currentBook.getCategories());

        TextView pageCount = myDialog.findViewById(R.id.dialog_pageCount);
        pageCount.setText(currentBook.getPageCount()+" pages");

        String[] arr = currentBook.getPublishedDate().split("-");
        TextView date = myDialog.findViewById(R.id.dialog_publishDate);
        date.setText("Published Date: "+arr[0]);

        TextView discreption = myDialog.findViewById(R.id.dialog_disc);
        discreption.setText(currentBook.getDescription());
        discreption.setMovementMethod(new ScrollingMovementMethod());

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(30);
        shape.setColor(getResources().getColor(R.color.gold));
        Button buy = myDialog.findViewById(R.id.btn_buy);
        buy.setBackground(shape);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri BookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, BookUri);
                startActivity(websiteIntent);
            }
        });

        GradientDrawable shape2 = new GradientDrawable();
        shape2.setCornerRadius(30);
        shape2.setColor(Color.BLACK);
        Button cancel = myDialog.findViewById(R.id.btn_cancle);
        cancel.setBackground(shape2);

        myDialog.show();
    }//showPopup


    protected void cancelPopup(View view) {
        myDialog.dismiss();
    }


    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }


    private void showEmptyStateTextView(){
        mAdapter.clear();
        mEmptyStateTextView.setVisibility(View.VISIBLE);
    }


    private void hideEmptyStateTextView(){
        mEmptyStateTextView.setVisibility(View.GONE);
    }


    private void hideStartTextView(){
        mStartingTextView.setVisibility(View.GONE);
    }


}//MainActivity
