package com.apk.editor.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apk.editor.R;
import com.apk.editor.adapters.TextViewAdapter;
import com.apk.editor.utils.APKExplorer;
import com.apk.editor.utils.AppData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by APK Explorer & Editor <apkeditor@protonmail.com> on March 04, 2021
 */
public class TextViewActivity extends AppCompatActivity {

    private ContentLoadingProgressBar mProgressLayout;
    private RecyclerView mRecyclerView;
    public static final String PATH_INTENT = "path";
    private String mPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialButton mSearch = findViewById(R.id.search);
        mProgressLayout = findViewById(R.id.progress);
        MaterialAutoCompleteTextView mSearchWord = findViewById(R.id.search_word);
        MaterialTextView mTitle = findViewById(R.id.title);
        mRecyclerView = findViewById(R.id.recycler_view);

        mPath = getIntent().getStringExtra(PATH_INTENT);

        if (mPath != null && sFileUtils.exist(new File(mPath))) {
            mTitle.setText(new File(mPath).getName());
        }

        mSearch.setOnClickListener(v -> {
            if (mSearchWord.getVisibility() == View.VISIBLE) {
                mSearchWord.setVisibility(View.GONE);
                AppData.toggleKeyboard(0, mSearchWord, this);
            } else {
                mSearchWord.setVisibility(View.VISIBLE);
                AppData.toggleKeyboard(1, mSearchWord, this);
            }
        });

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                reload(s.toString()).execute();

            }
        });

        reload(null).execute();

        mBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchWord.getVisibility() == View.VISIBLE) {
                    mSearchWord.setVisibility(View.GONE);
                    mSearchWord.setText(null);
                    return;
                }
                finish();
            }
        });
    }

    private sExecutor reload(String searchWord) {
        return new sExecutor() {
            private TextViewAdapter mTextViewAdapter;
            @Override
            public void onPreExecute() {
                mProgressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mTextViewAdapter = new TextViewAdapter(APKExplorer.getTextViewData(mPath, searchWord, false, TextViewActivity.this), searchWord);
            }

            @Override
            public void onPostExecute() {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(TextViewActivity.this));
                mRecyclerView.setAdapter(mTextViewAdapter);
                mProgressLayout.setVisibility(View.GONE);
            }
        };
    }

}