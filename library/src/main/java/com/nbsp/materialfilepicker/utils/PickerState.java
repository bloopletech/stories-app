package com.nbsp.materialfilepicker.utils;

import java.io.Serializable;

/**
 * Created by i on 20/06/2016.
 */
public class PickerState implements Serializable {
    private String mPath;
    private int mSortMethod = FileComparator.SORT_ALPHABETIC;
    private boolean mSortDirectionAsc = true;

    public PickerState() {}

    public PickerState(String path, int sortMethod, boolean sortDirectionAsc) {
        mPath = path;
        mSortMethod = sortMethod;
        mSortDirectionAsc = sortDirectionAsc;
    }

    public PickerState(PickerState other) {
        mPath = other.getPath();
        mSortMethod = other.getSortMethod();
        mSortDirectionAsc = other.isSortDirectionAsc();
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getSortMethod() {
        return mSortMethod;
    }

    public void setSortMethod(int sortMethod) {
        mSortMethod = sortMethod;
    }

    public boolean isSortDirectionAsc() {
        return mSortDirectionAsc;
    }

    public void setSortDirectionAsc(boolean sortDirectionAsc) {
        mSortDirectionAsc = sortDirectionAsc;
    }
}