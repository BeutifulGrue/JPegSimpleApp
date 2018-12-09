package com.garthskidstuff.jpegsimpleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import static android.graphics.Bitmap.CompressFormat.JPEG;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";

    private static final int PICK_IMAGE_A = 1;

    private static final int PICK_IMAGE_B = 2;

    private ImageView aView;

    private ImageView bView;

    private ImageView abView;

    private File aFile;

    private File bFile;

    private File imageDir;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout parent = findViewById(R.id.parent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageDir = new File(getFilesDir(), "images/");
        //noinspection ResultOfMethodCallIgnored
        imageDir.mkdir();

        aView = findViewById(R.id.aImage);
        aFile = new File(imageDir, "A.jpg");
        bView = findViewById(R.id.bImage);
        bFile = new File(imageDir, "B.jpg");
        abView = findViewById(R.id.abImage);

        FloatingActionButton fabA = findViewById(R.id.fabA);
        fabA.setOnClickListener(new View.OnClickListener()
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
        fabB.setOnClickListener(new View.OnClickListener()
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
        fabAB.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view)
            {
                File abFile = new File(imageDir, "AB.jpg");
                abFile.delete(); // So we know if we made
                try (OutputStream abStream = new FileOutputStream(abFile.getPath());
                     InputStream aStream = getContentResolver().openInputStream(Uri.fromFile(aFile));
                     InputStream bStream = getContentResolver().openInputStream(Uri.fromFile(bFile)))
                {
                    Bitmap a = BitmapFactory.decodeStream(aStream);
                    Bitmap b = BitmapFactory.decodeStream(bStream);
                    ImageInfo aInfo = new ImageInfo(aStream);
                    ImageInfo bInfo = new ImageInfo(bStream);

                    int width = a.getWidth() + b.getWidth();
                    int height = Integer.max(a.getHeight(), b.getHeight());
                    Bitmap ab = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(ab);
                    canvas.drawBitmap(a, null, new Rect(0, 0, a.getWidth(), a.getHeight()), null);
                    canvas.drawBitmap(b, null, new Rect(a.getWidth(), 0, width, b.getHeight()),
                            null);
                    Log.w(TAG, "onClick: newFile.exists():" + abFile.exists());
                    ab.compress(JPEG, 100, abStream);
                    abStream.close();
                    ImageInfo.MergeAndWrite(aInfo, bInfo, abFile);

                    Uri abUri = Uri.fromFile(abFile);
                    Log.d(TAG, "onClick: " + abUri.toString());
                    abView.setImageURI(null);
                    abView.setImageURI(abUri);
                    share(abFile);

                }
                catch (FileNotFoundException e)
                {
                    Log.e(TAG, "onClick: FileNotFound", e);
                }
                catch (IOException e)
                {
                    Log.e(TAG, "onClick: ", e);
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

    private void share(final File abFile)
    {
        Uri aUri = FileProvider.getUriForFile(
                MainActivity.this,
                        getString(R.string.file_provider_authority),
                        aFile);
        Uri bUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        getString(R.string.file_provider_authority),
                        bFile);
        Uri abUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        getString(R.string.file_provider_authority),
                        abFile);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.share));
        StringBuilder sb = new StringBuilder();
        sb.append("Send left right and merged?");
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
                                                            "Share files to"));
                                        }
                                    });
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Upload Error:" + e);
                }
            }
        });
        builder.show();
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
                    if (null == uri)
                    {
                        Log.e(TAG, "onActivityResult: ", new NullPointerException("uri"));
                    }
                    else
                    {
                        try (InputStream stream1 = getContentResolver().openInputStream(uri);
                             InputStream stream2 = getContentResolver().openInputStream(uri))
                        {
                            Boolean isA = PICK_IMAGE_A == requestCode;
                            ImageInfo info = new ImageInfo(stream1);
                            Bitmap bitmap = BitmapFactory.decodeStream(stream2);
                            Log.d(TAG, "onActivityResult: " + info.orientation);
                            Bitmap normalized = info.normalizeOrientation(bitmap);
                            try (OutputStream out = new FileOutputStream(isA ? aFile : bFile))
                            {
                                normalized.compress(JPEG, 100, out);
                            }
                            ImageView view = isA ? aView : bView;
                            view.setImageBitmap(normalized);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                throw new InvalidParameterException();
        }
    }
}
