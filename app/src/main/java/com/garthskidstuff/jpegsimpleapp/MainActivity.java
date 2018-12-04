package com.garthskidstuff.jpegsimpleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_A = 1;
    private static final int PICK_IMAGE_B = 2;

    private View mainView;
    private Uri aUri;
    private Uri bUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainView = toolbar;
        setSupportActionBar(toolbar);

        FloatingActionButton fabA = findViewById(R.id.fabA);
        fabA.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                                               PICK_IMAGE_A);
                    }
                });

        FloatingActionButton fabB = findViewById(R.id.fabB);
        fabB.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                                               PICK_IMAGE_B);
                    }
                });

        FloatingActionButton fabAB = findViewById(R.id.fabAB);
        fabAB.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        try (FileOutputStream abStream  = openFileOutput("AB.jpg", 0);
                             InputStream aStream    = getContentResolver().openInputStream(aUri);
                             InputStream bStream    = getContentResolver().openInputStream(bUri))
                        {
                            Bitmap a = BitmapFactory.decodeStream(aStream);
                            Bitmap b = BitmapFactory.decodeStream(bStream);
                            int width = a.getWidth() + b.getWidth();
                            int height = a.getHeight() + b.getHeight();
                            Bitmap ab = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(ab);
                            canvas.drawBitmap(a, null,
                                              new Rect(0, 0, a.getWidth(), a.getHeight()),
                                              null);
                            canvas.drawBitmap(b, null,
                                              new Rect(a.getWidth(), a.getHeight(), width, height),
                                              null);
                            ab.compress(Bitmap.CompressFormat.JPEG, 100, abStream);
                            Uri abUri = Uri.fromFile(getFileStreamPath("AB.jpg"));
                            share(abUri);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share(Uri abUri)
    {
        if (null != abUri && null != aUri && null != bUri)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.share));
            StringBuilder sb = new StringBuilder();
            sb.append(aUri + ", " + bUri + ", " + abUri + ": OK?");
            final ArrayList<Uri> imageUris = new ArrayList<>();
            imageUris.add(aUri);
            imageUris.add(bUri);
            imageUris.add(abUri);
            builder.setMessage(sb.toString());
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                        try
                        {
                            MediaScannerConnection
                                    .scanFile(MainActivity.this, new String[]{imageUris.toString()},
                                              null, new MediaScannerConnection.OnScanCompletedListener()
                                            {
                                                public void onScanCompleted(String path, Uri uri)
                                                {
                                                    Intent shareIntent = new Intent();
                                                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                                    shareIntent.putParcelableArrayListExtra(
                                                            Intent.EXTRA_STREAM, imageUris);
                                                    shareIntent.setType("*/*");
                                                    MainActivity.this.startActivity(
                                                            Intent.createChooser(shareIntent,
                                                                                 "share files to"));
                                                }
                                            });
                            Snackbar.make(mainView, "Shared a, b, and ab files?", Snackbar.LENGTH_LONG);
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Upload Error:" + e);
                        }
                    }
                });
        builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case PICK_IMAGE_A:
                //fallthrough
            case PICK_IMAGE_B:
                if (resultCode == RESULT_OK && data != null && data.getData() != null)
                {
                    Uri uri = data.getData();
                    if(null == uri)
                    {
                        Log.e(TAG, "onActivityResult: ", new NullPointerException("uri"));
                    }

                    if (PICK_IMAGE_A == requestCode)
                    {
                        aUri = uri;
                    }
                    else
                    {
                        bUri = uri;
                    }
                }
                break;
            default:
                throw new InvalidParameterException();
        }
    }
}
