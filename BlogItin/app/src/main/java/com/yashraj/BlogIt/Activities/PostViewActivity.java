package com.yashraj.BlogIt.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashraj.BlogIt.Model.Blog;
import com.yashraj.BlogIt.R;

import java.util.List;

public class PostViewActivity extends AppCompatActivity {

    private ImageView postimageview;
    private TextView posttitle,postdesc,postdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        postdate=findViewById(R.id.PostViewDateListText);
        postdesc=findViewById(R.id.PostViewSubtitleListText);
        postimageview=findViewById(R.id.PostViewImageList);
        posttitle=findViewById(R.id.PostViewTitleListText);

        Bundle bundle=getIntent().getExtras();


        if(bundle!=null){
            posttitle.setText(bundle.getString("title"));
            postdesc.setText(bundle.getString("desc"));
            postdate.setText(bundle.getString("date"));
            postimageview.setBackground(Drawable.createFromPath(bundle.getString("image")));

        }


    }
}