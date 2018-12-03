package com.garthskidstuff.jpegsimpleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

                    if (ab.exists())
                    {
                        

                    }
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
}
