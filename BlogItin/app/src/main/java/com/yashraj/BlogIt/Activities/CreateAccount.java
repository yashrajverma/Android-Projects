package com.yashraj.BlogIt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yashraj.BlogIt.R;

import android.app.ProgressDialog;
import android.widget.Toast;

public class CreateAccount extends AppCompatActivity {
    private Button signupbutton;
    private EditText firstname, lastname, email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressBar;
    private ImageButton profilepic;
    private Uri resultUri = null;
    private Uri propicuri;
    private StorageReference storageReference;
    public static final int GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        signupbutton = findViewById(R.id.signupbuttonID);
        firstname = findViewById(R.id.actfirstname_editID);
        lastname = findViewById(R.id.actlastname_editID);
        email = findViewById(R.id.actemail_editID);
        password = findViewById(R.id.actpassword_editID);
        progressBar = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference().child("mUsers_Profile_pics");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mUsers").child("Users_post");

        profilepic = findViewById(R.id.profilepicId);


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);

            }
        });


        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            if (resultUri == null) {
                                Toast.makeText(getApplicationContext(), "Please Add Image", Toast.LENGTH_LONG).show();
                            } else {
                                signIn();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Email Already Exits", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

    }

    private void signIn() {
        final String firstnamestr = firstname.getText().toString();
        final String lastnamestr = lastname.getText().toString();
        final String emailstr = email.getText().toString();
        final String passwordstr = password.getText().toString();
        Log.d("Password :", passwordstr);
        if (!firstnamestr.equals("") && !lastnamestr.equals("") && !emailstr.equals("") && !passwordstr.equals("")) {
            mAuth.createUserWithEmailAndPassword(emailstr, passwordstr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if (authResult != null) {
                        progressBar.setMessage("Creating Account...");
                        progressBar.show();
                        StorageReference profilepicstore = storageReference.child("mUsers_Profile_pics").child(resultUri.getLastPathSegment());
                        profilepicstore.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference newuser = databaseReference.child(userid);
                                newuser.child("First Name").setValue(firstnamestr);
                                newuser.child("Last Name").setValue(lastnamestr);
                                newuser.child("image").setValue(resultUri.toString());

                                progressBar.dismiss();

                                Intent intent = new Intent(CreateAccount.this, PostlistActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });

                    }
                }
            });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) {
            propicuri = data.getData();
            CropImage.activity(propicuri).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profilepic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}