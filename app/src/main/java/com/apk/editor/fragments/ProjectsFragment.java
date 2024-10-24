package com.apk.editor.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apk.editor.R;
import com.apk.editor.adapters.ProjectsAdapter;
import com.apk.editor.utils.AppData;
import com.apk.editor.utils.Common;
import com.apk.editor.utils.CommonViews;
import com.apk.editor.utils.Projects;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by APK Explorer & Editor <apkeditor@protonmail.com> on March 06, 2021
 */
public class ProjectsFragment extends Fragment {

    private AppCompatAutoCompleteTextView mSearchWord;
    private LinearLayoutCompat mProgress;
    private MaterialTextView mTitle;
    private RecyclerView mRecyclerView;
    private ProjectsAdapter mRecycleViewAdapter;
    private String mSearchText = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mTitle = mRootView.findViewById(R.id.app_title);
        mSearchWord = mRootView.findViewById(R.id.search_word);
        mProgress = mRootView.findViewById(R.id.progress_layout);
        AppCompatImageButton mSearchButton = mRootView.findViewById(R.id.search_button);
        AppCompatImageButton mSortButton = mRootView.findViewById(R.id.sort_button);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mTitle.setText(getString(R.string.projects));

        mSearchButton.setOnClickListener(v -> {
            if (mSearchWord.getVisibility() == View.VISIBLE) {
                mSearchWord.setVisibility(View.GONE);
                mTitle.setVisibility(View.VISIBLE);
                if (mSearchWord != null) {
                    mSearchWord.setText(null);
                }
                AppData.toggleKeyboard(0, mSearchWord, requireActivity());
            } else {
                mSearchWord.setVisibility(View.VISIBLE);
                mSearchWord.requestFocus();
                mTitle.setVisibility(View.GONE);
                AppData.toggleKeyboard(1, mSearchWord, requireActivity());
            }
        });

        mSortButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mSortButton);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.sort_order)).setCheckable(true)
                    .setChecked(sCommonUtils.getBoolean("az_order", true, requireActivity()));
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) {
                    sCommonUtils.saveBoolean("az_order", !sCommonUtils.getBoolean("az_order", true, requireActivity()), requireActivity());
                    loadProjects(mSearchText, requireActivity());
                }
                return false;
            });
            popupMenu.show();
        });

        loadProjects(mSearchText, requireActivity());

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = s.toString().toLowerCase();
                loadProjects(mSearchText, requireActivity());
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Common.isBusy()) return;
                if (mSearchText != null) {
                    if (mSearchWord != null && mSearchWord.getVisibility() == View.VISIBLE) {
                        mSearchWord.setVisibility(View.GONE);
                        mTitle.setVisibility(View.VISIBLE);
                        mSearchWord.setText(null);
                    }
                    mSearchText = null;
                    return;
                }
                CommonViews.navigateToFragment(requireActivity(), R.id.nav_apps);
            }
        });

        return mRootView;
    }

    private void loadProjects(String searchWord, Activity activity) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                mRecyclerView.setVisibility(View.GONE);
                Common.setProgress(true, mProgress);
                mRecyclerView.removeAllViews();
            }

            @Override
            public void doInBackground() {
                mRecycleViewAdapter = new ProjectsAdapter(Projects.getData(searchWord, activity), searchWord);
            }

            @Override
            public void onPostExecute() {
                mRecyclerView.setAdapter(mRecycleViewAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                Common.setProgress(false, mProgress);
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Common.isReloading()) {
            Common.isReloading(false);
            loadProjects(mSearchText, requireActivity());
        }
    }
    
}