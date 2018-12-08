package com.garthskidstuff.jpegsimpleapp;

import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.media.ExifInterface.TAG_IMAGE_DESCRIPTION;

public class ImageInfo
{

    private static String spikePath = "ToDo/Info/"; // TODO: 12/8/18
    // member variables
    private String imageDiscription;
    // TODO: 12/8/18

    private ImageInfo (File jpg) throws IOException
    {
        ExifInterface exif = new ExifInterface(jpg.getAbsolutePath());
        readExif(exif);
        readSpikeFile(jpg);
    }

    public ImageInfo(ImageInfo aInfo, ImageInfo bInfo)
    {
    }

    private void readSpikeFile(File jpg) throws IOException
    {
        String[] parts = jpg.getName().split("/.", 2);
        String path = spikePath + parts[0] + ".spike";
        try (InputStream stream = new FileInputStream(path))
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 1000);
            // TODO: 12/8/18 Read Spike File Format (XML?)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readExif(ExifInterface exif)
    {
        imageDiscription = exif.getAttribute(TAG_IMAGE_DESCRIPTION);
        // TODO: 12/8/18 Other attributes?
    }

    private void WriteInfo(File jpg) throws IOException
    {
        ExifInterface abExif = new ExifInterface(jpg.getAbsolutePath());
        abExif.setAttribute(TAG_IMAGE_DESCRIPTION, imageDiscription);
        // TODO: 12/8/18 write info into exif

        abExif.saveAttributes();
    }

    static public void MergeAndWrite(File aFile, File bFile, File cFile) throws IOException
    {
        ImageInfo aInfo = new ImageInfo(aFile);
        ImageInfo bInfo = new ImageInfo(bFile);

        ImageInfo abInfo = new ImageInfo (aInfo, bInfo);
        abInfo.WriteInfo(cFile);
    }
}
