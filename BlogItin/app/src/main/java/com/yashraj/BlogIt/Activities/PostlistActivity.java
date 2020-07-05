package com.yashraj.BlogIt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yashraj.BlogIt.Data.BlogRecyclerAdapter;
import com.yashraj.BlogIt.Model.Blog;
import com.yashraj.BlogIt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostlistActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter adapter;
    private List<Blog> blogList;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        
        final String userid=mUser.getUid();

        databaseReference=FirebaseDatabase.getInstance().getReference().child("mUsers").child("Users_post").child(userid).child(userid);
        databaseReference.keepSynced(true);
        view =findViewById(android.R.id.content);
        Snackbar.make(view,"Swipe Post To Delete",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).setActionTextColor(Color.parseColor("#FFFFFF")).show();

        blogList=new ArrayList<>();
        recyclerView=findViewById(R.id.myrecyclerViewID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);



        Log.d("User ID................",userid);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deletepost(userid);
            }
        }).attachToRecyclerView(recyclerView);

    }

    private void deletepost(String userid){

        final DatabaseReference duserid=FirebaseDatabase.getInstance().getReference().child("mUsers").child("Users_post").child(userid).child(userid);
        duserid.removeValue();

        Snackbar.make(view,"Post Deleted",Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Blog blog=dataSnapshot.getValue(Blog.class);
                Collections.reverse(blogList);
                blogList.add(blog);
                adapter=new BlogRecyclerAdapter(PostlistActivity.this,blogList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_post:
                if(mUser!=null && mAuth!=null){
                    startActivity(new Intent(PostlistActivity.this,AddPostActivity.class));

                }
                break;
            case R.id.signout:
                if(mUser!=null && mAuth!=null){
                    mAuth.signOut();
                    startActivity(new Intent(PostlistActivity.this,MainActivity.class));
                    finish();
                }
                break;
            case R.id.webview:
                startActivity(new Intent(PostlistActivity.this,webview.class));
                break;
            case R.id.shareApp:
                shareapp();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void shareapp(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String app_url="https://drive.google.com/file/d/10xaMq1qwudqOs8FCK0BVqXh2n6rP9Wjy/view?usp=sharing";
        intent.putExtra(Intent.EXTRA_TEXT,app_url);
        startActivity(Intent.createChooser(intent,"Share Via"));

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postlist,menu);
        return super.onCreateOptionsMenu(menu);

    }
}