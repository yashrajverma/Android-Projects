package com.yashraj.BlogIt.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yashraj.BlogIt.Activities.PostViewActivity;
import com.yashraj.BlogIt.Model.Blog;
import com.yashraj.BlogIt.R;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.Long.parseLong;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcard,parent,false);

        return new ViewHolder(view,context );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog=blogList.get(position);
        String imageurl;
        holder.posttitle.setText(blog.getTitle());
        holder.postdescription.setText(blog.getDescription());

        DateFormat dateFormat=DateFormat.getDateInstance();
        String formattedate=dateFormat.format(new Date(parseLong(blog.getDateadded())).getTime());
        holder.dateadded.setText(formattedate);

        imageurl=blog.getImage();

        //TODO load picasso lib to add image
        Picasso.get().load(imageurl).into(holder.postimage);

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView postimage;
        public TextView posttitle,postdescription,dateadded;
        String userid;



        public ViewHolder(@NonNull final View itemView, Context ctx) {
            super(itemView);


            context=ctx;
            postimage=itemView.findViewById(R.id.PostImageList);
            posttitle=itemView.findViewById(R.id.PostTitleListText);
            postdescription=itemView.findViewById(R.id.PostSubtitleListText);
            dateadded=itemView.findViewById(R.id.PostDateListText);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getAdapterPosition();
                    //TODO Can go t the next activuty..
                    Blog blog=blogList.get(position);
                    DateFormat dateFormat=DateFormat.getDateInstance();
                    String formattedate=dateFormat.format(new Date(parseLong(blog.getDateadded())).getTime());

                    Intent intent=new Intent(context, PostViewActivity.class);
                    intent.putExtra("title",blog.getTitle());
                    intent.putExtra("desc",blog.getDescription());
                    intent.putExtra("date",formattedate);
                    intent.putExtra("image",blog.getImage());
                    context.startActivity(intent);

                }
            });

        }
    }
}
