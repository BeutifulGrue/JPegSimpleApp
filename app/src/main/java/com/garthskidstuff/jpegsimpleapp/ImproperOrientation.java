package com.garthskidstuff.jpegsimpleapp;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

enum ImproperOrientation
{
    ORIENTATION_UNKNOWN(0),
    ORIENTATION_NORMAL(1), ORIENTATION_FLIPPED_HORIZONTAL(2), ORIENTATION_180(3), ORIENTATION_FLIPPED_VERTICALLY(4),
    ORIENTATION_TRANSPOSE(5), ORIENTATION_90(6), ORIENTATION_TRANSVERSE(7), ORIENTATION_270(8);
    public final int code;
    private static final String TAG = "ImproperOrientation";

    ImproperOrientation(int i)
    {
        code = i;
    }

    static ImproperOrientation fromCode(int i)
    {
        for (ImproperOrientation orientation: values())
        {
            if (orientation.code == i)
            {
                return orientation;
            }
        }
        return ORIENTATION_UNKNOWN;
    }

    static int getCode(ImproperOrientation orientation)
    {
        return orientation.code;
    }

    public Bitmap normalizeOrientation(Bitmap bitmap) throws IllegalArgumentException
    {
        Log.d(TAG, "normalizeOrientation: " + this + bitmap);
        Matrix matrix = new Matrix();
        Bitmap newBitmap = null;
        int xCenter = bitmap.getWidth() / 2;
        int yCenter = bitmap.getHeight() / 2;
        switch(this)
        {
            default:
            case ORIENTATION_UNKNOWN:
                throw new IllegalArgumentException();
            case ORIENTATION_NORMAL:
                newBitmap = bitmap; //the bitmap is already "normalized".
            case ORIENTATION_FLIPPED_HORIZONTAL:
                matrix.postScale(-1, 1, xCenter, yCenter);
                break;
            case ORIENTATION_180:
                matrix.postRotate(180, xCenter, yCenter);
                break;
            case ORIENTATION_FLIPPED_VERTICALLY:
                matrix.postScale(1, -1, xCenter, yCenter);
                break;
            case ORIENTATION_TRANSPOSE:
                matrix.postScale(1, -1, xCenter, yCenter);
                matrix.postRotate(90, xCenter, yCenter);
                break;
            case ORIENTATION_90:
                matrix.postRotate(90, xCenter, yCenter);
                break;
            case ORIENTATION_TRANSVERSE:
                matrix.postScale(-1, 1, xCenter, yCenter);
                matrix.postRotate(90, xCenter, yCenter);
                break;
            case ORIENTATION_270:
                matrix.postRotate(270, xCenter, yCenter);
                break;
        }

        // If the orientation is normal, don't create a new bitmap
        if (null == newBitmap)
        {
            newBitmap = Bitmap
                    .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
        }

        return newBitmap;
    }
}
