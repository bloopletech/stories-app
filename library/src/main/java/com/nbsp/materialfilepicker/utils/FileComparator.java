package com.nbsp.materialfilepicker.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileComparator implements Comparator<File> {
    public static final int SORT_ALPHABETIC = 1;
    public static final int SORT_SIZE = 2;
    public static final int SORT_AGE = 3;

    private int mSortMethod = SORT_ALPHABETIC;
    private boolean mSortDirectionAsc = true;

    public FileComparator(int sortMethod, boolean sortDirectionAsc) {
        mSortMethod = sortMethod;
        mSortDirectionAsc = sortDirectionAsc;
    }

    @Override
    public int compare(File f1, File f2) {
        if(f1 == f2) {
            return 0;
        }

        File sf1 = f1;
        File sf2 = f2;
        if(!mSortDirectionAsc) {
            sf1 = f2;
            sf2 = f1;
        }

        if(sf1.isDirectory() && sf2.isFile()) {
            // Show directories above files
            return -1;
        }
        if(sf1.isFile() && sf2.isDirectory()) {
            // Show files below directories
            return 1;
        }

        switch(mSortMethod) {
            case SORT_ALPHABETIC:
                return sf1.getName().compareToIgnoreCase(sf2.getName());
            case SORT_SIZE:
                if(sf1.length() > sf2.length()) return 1;
                if(sf1.length() < sf2.length()) return -1;
                return 0;
            case SORT_AGE:
                if(sf1.lastModified() > sf2.lastModified()) return 1;
                if(sf1.lastModified() < sf2.lastModified()) return -1;
                return 0;
            default:
                return 0;
        }
    }
}
