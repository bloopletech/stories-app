package com.nbsp.materialfilepicker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nbsp.materialfilepicker.R;
import com.nbsp.materialfilepicker.filter.CompositeFilter;
import com.nbsp.materialfilepicker.utils.FileComparator;
import com.nbsp.materialfilepicker.utils.FileUtils;
import com.nbsp.materialfilepicker.utils.PickerState;
import com.nbsp.materialfilepicker.widget.EmptyRecyclerView;

import java.io.File;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class DirectoryFragment extends Fragment {
    interface FileClickListener {
        void onFileClicked(File clickedFile);
    }

    interface PickerStateListener {
        void onPickerStateChanged(PickerState pickerState);
    }

    private static final String ARG_FILTER = "arg_filter";
    private static final String ARG_PICKER_STATE = "arg_picker_state";

    private View mEmptyView;
    private CompositeFilter mFilter;
    private PickerState mPickerState;

    private EmptyRecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private FileClickListener mFileClickListener;
    private PickerStateListener mPickerStateListener;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFileClickListener = (FileClickListener) activity;
        mPickerStateListener = (PickerStateListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFileClickListener = null;
        mPickerStateListener = null;
    }

    public static DirectoryFragment getInstance(CompositeFilter filter, PickerState pickerState) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle args = new Bundle();

        args.putSerializable(ARG_FILTER, filter);
        args.putSerializable(ARG_PICKER_STATE, pickerState);
        instance.setArguments(args);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.sort_toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.sort_alphabetic) {
                            mPickerState.setSortMethod(FileComparator.SORT_ALPHABETIC);
                        } else if (menuItem.getItemId() == R.id.sort_size) {
                            mPickerState.setSortMethod(FileComparator.SORT_SIZE);
                        } else if (menuItem.getItemId() == R.id.sort_age) {
                            mPickerState.setSortMethod(FileComparator.SORT_AGE);
                        } else if (menuItem.getItemId() == R.id.sort_direction) {
                            mPickerState.setSortDirectionAsc(!mPickerState.isSortDirectionAsc());
                        }

                        if (mPickerStateListener != null) {
                            mPickerStateListener.onPickerStateChanged(mPickerState);
                        }
                        initFilesList();

                        return true;
                    }
                });

        mDirectoryRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.directory_recycler_view);
        mEmptyView = view.findViewById(R.id.directory_empty_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initFilesList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle args = getArguments();
        args.putSerializable(ARG_FILTER, mFilter);
        args.putSerializable(ARG_PICKER_STATE, mPickerState);
    }

    private void initFilesList() {
        mDirectoryAdapter = new DirectoryAdapter(getActivity(),
                FileUtils.getFileListByDirPath(mFilter, mPickerState));

        mDirectoryAdapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileClickListener != null) {
                    mFileClickListener.onFileClicked(mDirectoryAdapter.getModel(position));
                }
            }
        });

        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setEmptyView(mEmptyView);
    }

    @SuppressWarnings("unchecked")
    private void initArgs() {
        mFilter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
        mPickerState = (PickerState)getArguments().getSerializable(ARG_PICKER_STATE);
    }
}
