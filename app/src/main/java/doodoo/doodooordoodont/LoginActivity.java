package doodoo.doodooordoodont;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by David on 4/5/2018.
 */

public class LoginActivity extends Activity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        //Initializes the Facebook Sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //Initializes variables for google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Adds an OnEditorActionListener to listen for the Done button and login the user
        EditText password = (EditText) findViewById(R.id.password);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch(result) {
                    case EditorInfo.IME_ACTION_DONE:
                        if (validateForm()){
                            signIn(((TextView)findViewById(R.id.emailAddress)).getText().toString(),
                                    ((TextView)findViewById(R.id.password)).getText().toString());
                        }
                        break;
                }
                return true;
            }
        });

        //Changes the text of the Google sign in button
        TextView textView = (TextView) ((SignInButton)findViewById(R.id.google_login)).getChildAt(0);
        textView.setText("Sign In with Google");

        //Not used code for registering a callback of the facebook button
        loginButton = findViewById(R.id.facebook_login);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Adds the Createaccount dialog to the buttons onclicklistener
        final Button createAccount = (Button)findViewById(R.id.create_button);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creates the alertdialog and the layout to add the fields to
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("Please enter your information");
                alert.setTitle("Account Creation");
                final LinearLayout layout = new LinearLayout(LoginActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                //Username field
                final EditText username = new EditText(LoginActivity.this);
                username.setHint("Username");
                username.setWidth(layout.getWidth());

                //Password field
                final EditText pass = new EditText(LoginActivity.this);
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                pass.setHint("Password");
                pass.setWidth(layout.getWidth());

                //email field
                final EditText email = new EditText(LoginActivity.this);
                email.setHint("Email");
                email.setWidth(layout.getWidth());

                layout.addView(username);
                layout.addView(pass);       //Adds field to layout
                layout.addView(email);
                alert.setView(layout);

                //Sets the positive button to call create account with the fields
                alert.setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        createAccount(username.getText().toString(),pass.getText().toString(),email.getText().toString());
                    }
                });
                alert.show();

            }
        });

        //Adds onClick to login button to validate the entries and attempt to sign in user
        final Button login = (Button)findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    signIn(((TextView)findViewById(R.id.emailAddress)).getText().toString(),
                                        ((TextView)findViewById(R.id.password)).getText().toString());
                }
            }



        });

        //Adds onClick to logout the user and sends user to the login screen
        final Button logout = (Button)findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginActivity.this,"Successfully signed out.",
                        Toast.LENGTH_SHORT).show();
                Intent toLogin = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(toLogin);
            }
        });

        //Adds onClick to prompt the user and confirm deletion of account
        final Button deleteAccount = (Button)findViewById(R.id.delete_button);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reauthUser();   //Reauthorizes the user if necessary

                //Creates the alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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

    private void reauthUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        final EditText edittext = new EditText(LoginActivity.this);
        alert.setMessage("Please Re-enter your Password");
        alert.setTitle("Password Check");

        alert.setView(edittext);

        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
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

        alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

        AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(),"password");
        user.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "User re-authenticated");
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            ((TextView) findViewById(R.id.emailAddress)).setText(email);
            Uri photoUrl = user.getPhotoUrl();
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

    }

    private void createAccount(String username, String password, String email) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.signOut();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        while (mAuth.getCurrentUser() == null){}

        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this,"Successfully logged in.",
                                    Toast.LENGTH_SHORT).show();
                            Intent toHome = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(toHome);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;
        TextView mEmailField = (TextView)findViewById(R.id.emailAddress);
        TextView mPasswordField = (TextView)findViewById(R.id.password);

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
