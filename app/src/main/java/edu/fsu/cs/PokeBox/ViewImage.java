package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImage extends CardView{
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        System.out.println("this is the url: " + url);
        new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(url);


    }



}
