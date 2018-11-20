package doodoo.doodooordoodont;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by David on 4/5/2018.
 *
 * This Activity is for the user to see their account information and to preform different actions
 * related to their account. This is the page that replaces the login page.
 */

public class MyAccount extends Activity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        currUser = MainActivity.currUser;

        setContentView(R.layout.content_my_account);
        loginButton = findViewById(R.id.facebook_login);
        TextView textView = (TextView) ((SignInButton)findViewById(R.id.google_login)).getChildAt(0);
        textView.setText("Sign In with Google");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) { }
            @Override public void onCancel() { }
            @Override public void onError(FacebookException e) { }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Adds onClick to logout the user and sends user to the login screen
        final Button logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MyAccount.this,"Successfully signed out.",
                        Toast.LENGTH_SHORT).show();
                Intent toLogin = new Intent(MyAccount.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });

        //Adds onClick to prompt the user and confirm deletion of account
        final Button deleteAccount = findViewById(R.id.delete_button);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reauthUser();   //Reauthorizes the user if necessary

                //Creates the alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(MyAccount.this);
                alert.setMessage("Are you sure you wish to delete account?");
                alert.setCancelable(true);

                //If yes is selected it deletes the user from database
                alert.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                dialog.cancel();
                            }
                        });
                //If no is slected it cancels the dialog
                alert.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                alert.show();
            }
        });
    }

    /**
     * reauthUser:
     *
     * This method is used to reauthorize the user.
     */
    private void reauthUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Creates an alert to prompt for the user to reenter their password
        AlertDialog.Builder alert = new AlertDialog.Builder(MyAccount.this);
        final EditText edittext = new EditText(MyAccount.this);
        alert.setMessage("Please Re-enter your Password");
        alert.setTitle("Password Check");
        alert.setView(edittext);

        alert.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(),edittext.getText().toString());
                user.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated");
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }


    /**
     * onStart
     *
     * This method is run everytime the activity is started, even if it has already been created.
     * If this activity starts through an intent, this method is still called. It is used here to
     * grab the users information and populate the fields on screen.
     */
    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.username)).setText("Username: " + currUser.getName());
        ((TextView) findViewById(R.id.email)).setText("Email: " + currUser.getEmail());
        ((TextView) findViewById(R.id.numreviews)).setText("NumRevies: " + currUser.getNumReviews());
        ((TextView) findViewById(R.id.userid)).setText("UserID: " + currUser.getUserId());
        ((TextView) findViewById(R.id.gender)).setText("Gender: " + currUser.getGender());

        Uri photoUrl = user.getPhotoUrl();
        // Check if user's email is verified
        boolean emailVerified = user.isEmailVerified();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
