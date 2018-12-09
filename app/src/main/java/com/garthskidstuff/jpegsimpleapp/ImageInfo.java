package com.garthskidstuff.jpegsimpleapp;

import android.graphics.Bitmap;
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
import static android.media.ExifInterface.TAG_ORIENTATION;

public class ImageInfo
{

    private static String spikePath = "ToDo/Info/"; // TODO: 12/8/18
    // member variables
    private String imageDescription;
    ImproperOrientation orientation;
    // TODO: 12/8/18

    public ImageInfo (InputStream jpg) throws IOException
    {
        ExifInterface exif = new ExifInterface(jpg);
        readExif(exif);
//        readSpikeFile(jpg);
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
        imageDescription = exif.getAttribute(TAG_IMAGE_DESCRIPTION);
        orientation = ImproperOrientation.fromCode(exif.getAttributeInt(TAG_ORIENTATION, 1));

        // TODO: 12/8/18 Other attributes?
    }

    private void WriteInfo(File jpg) throws IOException
    {
        ExifInterface abExif = new ExifInterface(jpg.getAbsolutePath());
        abExif.setAttribute(TAG_IMAGE_DESCRIPTION, imageDescription);
        abExif.setAttribute(TAG_ORIENTATION, String.valueOf(orientation));
        // TODO: 12/8/18 write info into exif

        abExif.saveAttributes();
    }

    static public void MergeAndWrite(ImageInfo aInfo, ImageInfo bInfo, File abFile) throws IOException
    {
        ImageInfo abInfo = new ImageInfo (aInfo, bInfo);
        abInfo.WriteInfo(abFile);
    }

    Bitmap normalizeOrientation(Bitmap bitmap) throws IllegalArgumentException
    {
            Bitmap ret = orientation.normalizeOrientation(bitmap);
            orientation = ImproperOrientation.ORIENTATION_NORMAL;
            return ret;
    }
}
