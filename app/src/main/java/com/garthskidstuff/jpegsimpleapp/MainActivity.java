package com.garthskidstuff.jpegsimpleapp;

import android.app.LauncherActivity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = findViewById(R.id.)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                File ab = new File ("AB.jpg");
                if(ab.exists())
                {
                    ab.delete();
                }

                try (OutputStream abStream = new FileOutputStream("AB.jpg");
                     InputStream aStream = new FileInputStream("A.jpg");
                     InputStream bStream = new FileInputStream("B.jpg"))
                {
                    Bitmap a = BitmapFactory.decodeStream(aStream);
                    Bitmap b = BitmapFactory.decodeStream(bStream);
                    int width = a.getWidth() + b.getWidth();
                    int height = a.getHeight() + b.getHeight();
                    Bitmap ab = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(ab);
                    canvas.drawBitmap(a,null, new Rect(0,0, a.getWidth(), a.getHeight()), null );
                    canvas.drawBitmap(b,null, new Rect(a.getWidth(), a.getHeight(), width, height), null );
                    ab.compress(Bitmap.CompressFormat.JPEG, 100, abStream);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                try () {
                    File a = new File("A.jpg");
                    File b = new File("B.jpg");
                    ArrayList<File> files = new ArrayList();
                    if (a.exists())
                    {
                        files.add(a);
                    }
                    if (b.exists())
                    {
                        files.add(b);
                    }
                    if (ab.exists())
                    {
                        files.add(ab);
                    }

                    share(files);
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

    private void share(final List<File> files)
    {
        final int num = files.size();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.share));
        StringBuilder sb = new StringBuilder();
        for(File file : files)
        {
            sb.append(file.getName() + " ");
        }
        sb.append(": OK?");
        builder.setMessage(sb.toString());
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (!files.isEmpty())
                {
                    final ArrayList<Uri> imageUris = new ArrayList<>();
                    for (File file : files)
                    {
                        imageUris.add(Uri.fromFile(file));
                    }

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
                        String msg = "Shared " ++ files.size() ++ "file(s)?";
                        Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Upload Error:" + e);
                    }
                }
            }
        });
        builder.show();
    }
}
