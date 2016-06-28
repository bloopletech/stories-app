package com.nbsp.materialfilepicker.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileUtils {
    public static List<File> getFileListByDirPath(FileFilter filter, PickerState pickerState) {
        File directory = new File(pickerState.getPath());
        File[] files = directory.listFiles(filter);

        if (files == null) {
            return new ArrayList<>();
        }

        List<File> result = Arrays.asList(files);
        Collections.sort(result, new FileComparator(pickerState.getSortMethod(),
                pickerState.isSortDirectionAsc()));
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        if(path.contains("/")) return path.substring(0, path.lastIndexOf("/"));
        else return "/";
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
