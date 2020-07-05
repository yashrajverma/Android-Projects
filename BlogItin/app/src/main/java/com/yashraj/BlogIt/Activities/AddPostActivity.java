package com.yashraj.BlogIt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yashraj.BlogIt.Model.Blog;
import com.yashraj.BlogIt.R;

import java.util.HashMap;
import java.util.Map;
import android.app.ProgressDialog;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Button addpostbutton;
    private EditText addposttitle,addpostdesc;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private FirebaseUser muser;
    private FirebaseAuth mAuth;
    private Uri ImageUri;
    private ProgressDialog progressBar;
    private static  final int GALLERY_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        progressBar=new ProgressDialog(this);
        imageButton=findViewById(R.id.AddimageButtonID);
        addpostbutton=findViewById(R.id.AddpostButtonID);
        addposttitle=findViewById(R.id.addpostedittextID);
        addpostdesc=findViewById(R.id.addpostdescriptionedittext);

        mStorage=FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();
        String userid=muser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("mUsers").child("Users_post").child(userid).child(userid);

        addpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startposting();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            ImageUri=data.getData();
            imageButton.setImageURI(ImageUri);

        }
    }

    private void startposting() {

        final String posttile=addposttitle.getText().toString().trim();
        final String postdesc=addpostdesc.getText().toString().trim();
        //startposting


        if(!posttile.equals("") && !postdesc.equals("") && ImageUri!=null ){

                final StorageReference filepath=mStorage.child("Blog_Images").child(ImageUri.getLastPathSegment());
                progressBar.setMessage("Uploading Post...");
                progressBar.show();
                filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference newPost=databaseReference.push();

                                Map<String,String> datatosave=new HashMap<>();
                                datatosave.put("title",posttile);
                                datatosave.put("description",postdesc);
                                datatosave.put("dateadded",String.valueOf(System.currentTimeMillis()));
                                datatosave.put("image",uri.toString());
                                datatosave.put("userID",muser.getUid());

                                newPost.setValue(datatosave);

                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Post Uploaded",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddPostActivity.this,PostlistActivity.class));
                                finish();
                            }
                        });

                    }
                });

        }
    }

}