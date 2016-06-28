package com.nbsp.materialfilepicker;

import android.app.Activity;
import android.content.Intent;

import com.nbsp.materialfilepicker.filter.CompositeFilter;
import com.nbsp.materialfilepicker.filter.HiddenFilter;
import com.nbsp.materialfilepicker.filter.PatternFilter;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nbsp.materialfilepicker.utils.PickerState;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Dimorinny on 25.02.16.
 */
public class MaterialFilePicker {
    private Activity mActivity;
    private Pattern mFileFilter;
    private Boolean mDirectoriesFilter = false;
    private String mPath;
    private String mRootPath;
    private Boolean mShowHidden = false;
    private Class mClass;

    public MaterialFilePicker() {}

    public MaterialFilePicker withActivity(Activity activity) {
        mActivity = activity;
        return this;
    }

    public MaterialFilePicker withClass(Class cls) {
        mClass = cls;
        return this;
    }

    public MaterialFilePicker withFilter(Pattern pattern) {
        mFileFilter = pattern;
        return this;
    }

    public MaterialFilePicker withFilterDirectories(boolean directoriesFilter) {
        mDirectoriesFilter = directoriesFilter;
        return this;
    }

    public MaterialFilePicker withRootPath(String rootPath) {
        mRootPath = rootPath;
        return this;
    }

    public MaterialFilePicker withPath(String path) {
        mPath = path;
        return this;
    }

    public MaterialFilePicker withHiddenFiles(boolean show) {
        mShowHidden = show;
        return this;
    }


    private CompositeFilter getFilter() {
        ArrayList<FileFilter> filters = new ArrayList<>();

        if (!mShowHidden) {
            filters.add(new HiddenFilter());
        }

        if (mFileFilter != null) {
            filters.add(new PatternFilter(mFileFilter, mDirectoriesFilter));
        }

        return new CompositeFilter(filters);
    }

    public void start() {
        if (mActivity == null) {
            throw new RuntimeException("You must pass activity by calling withActivity method");
        }

        if (mClass == null) {
            throw new RuntimeException("You must pass intent class by calling withClass method");
        }

        CompositeFilter filter = getFilter();

        Intent intent = new Intent(mActivity, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.ARG_CLASS, mClass);
        intent.putExtra(FilePickerActivity.ARG_FILTER, filter);

        if (mPath != null) {
            intent.putExtra(FilePickerActivity.ARG_PATH, mPath);
        }

        if (mRootPath != null) {
            intent.putExtra(FilePickerActivity.ARG_ROOT_PATH, mRootPath);
        }

        mActivity.startActivity(intent);
    }
}
