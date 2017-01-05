package com.tree.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountActivity extends FragmentActivity {

    private Button mLogOut;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private  static final String TAG = "ACCOUNT_ACTIVITY";

    private DatabaseReference mDatabase;

    private TextView mName;
    private TextView mId;
    private TextView mEmail;
    private Button mAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mName = (TextView)findViewById(R.id.nameText);
        mId = (TextView)findViewById(R.id.idText);
        mEmail = (TextView)findViewById(R.id.emailText);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        mName.setText(mUser.getDisplayName());
        mEmail.setText(mUser.getEmail());
        mId.setText(mUser.getUid());

        mLogOut = (Button) findViewById(R.id.logOutButton);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        mAddUser = (Button) findViewById(R.id.buttonAddUser);
        mAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    writeNewUser(mUser.getUid(),mUser.getDisplayName(), mUser.getEmail());

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplication())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Authentication Some failure", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                //...
            }
        };
    }

    private void writeNewUser(String userId, String name, String email) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        User user = new User(name, email, date.toString());
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try{
                    //MyLogger.debug(
                    User user  = snapshot.getValue(User.class);
                    mName.setText(user.getUsername()+"E");
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));

                } catch (Throwable e) {

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });
        mDatabase.child(userId).setValue(user);
    }
    private void signOut(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
