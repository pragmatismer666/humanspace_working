package com.dappcloud.humanspace.Common.PhoneDirectory;

import android.os.Environment;

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getName();

    public String PICTURES = ROOT_DIR + "/pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
}
