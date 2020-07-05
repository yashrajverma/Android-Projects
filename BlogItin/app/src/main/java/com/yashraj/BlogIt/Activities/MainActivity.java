package com.yashraj.BlogIt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.yashraj.BlogIt.Data.BlogRecyclerAdapter;
import com.yashraj.BlogIt.Model.Blog;
import com.yashraj.BlogIt.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_CODE =9001 ;
    private EditText email,password;
    private Button loginbutton,createaccbutton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googlesinginbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);


        googlesinginbutton=findViewById(R.id.googlesingbutton);

        googlesinginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });



        email=findViewById(R.id.emailedittext);
        password=findViewById(R.id.passwordedittext);
        loginbutton=findViewById(R.id.loginbutton);
        createaccbutton=findViewById(R.id.createaccountbutton);
        mAuth=FirebaseAuth.getInstance();


        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 mUser=firebaseAuth.getCurrentUser();
                if(mUser!=null){
                    Log.d("User","User Logged In");
                    startActivity(new Intent(MainActivity.this,PostlistActivity.class));
                    finish();
                    Toast.makeText(MainActivity.this,"Signed In!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("User","User Logged Out");
                }
            }
        };



        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String emailstring=email.getText().toString();
                String passtring=password.getText().toString();
                if(!emailstring.equals("") && !passtring.equals("")){
                    mAuth.signInWithEmailAndPassword(emailstring,passtring).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Snackbar.make(v,"Failed to login",Snackbar.LENGTH_LONG).show();
                                Log.d("Email-----------",emailstring);
                            }else{
                                Snackbar.make(v,"Login Successful",Snackbar.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this,PostlistActivity.class));

                            }
                        }
                    });
                }

            }
        });
        createaccbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(MainActivity.this,CreateAccount.class));
                finish();
            }
        });

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        googleSignInClient=GoogleSignIn.getClient(this,gso);

    }

    private void googleSignIn() {
        Intent signintent=googleSignInClient.getSignInIntent();
        startActivityForResult(signintent,RC_SIGN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_CODE){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                e.printStackTrace();
            }
        }

    }
    private  void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(),"Authentication Success",Toast.LENGTH_LONG).show();
                    updateUI(user);
                    startActivity(new Intent(MainActivity.this,PostlistActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_LONG).show();
                    updateUI(null);
                }
            }
        });
    }
    private void updateUI(FirebaseUser user){
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(user!=null){
            String personame=account.getDisplayName();
            String email=account.getEmail();
            Toast.makeText(getApplicationContext(),personame+" "+email,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.signout){
            mAuth.signOut();
            View parentlayout =findViewById(android.R.id.content);
            Snackbar.make(parentlayout,"Signed Out",Snackbar.LENGTH_LONG).show();
        }
        if(item.getItemId()==R.id.add_post){
           startActivity(new Intent(MainActivity.this,AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

}