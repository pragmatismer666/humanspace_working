package com.dappcloud.humanspace.Common.PhoneDirectory;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isFile()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
