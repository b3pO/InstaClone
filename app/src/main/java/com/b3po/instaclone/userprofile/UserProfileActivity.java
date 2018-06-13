package com.b3po.instaclone.userprofile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.b3po.instaclone.R;
import com.b3po.instaclone.instaposthome.InstagramHome;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;

    // VIEW FIELDS
    EditText userProfileName, userProfileStatus;
    ImageView userProfileImageView;
    Button saveProfile;

    // FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    // FIREBASE DATABASE INSTANCE FIELDS
    DatabaseReference mUserDatabase;
    StorageReference mStorageRef;

    // IMAGE HOLD URI
    Uri imageHoldUri = null;

    // PROGRESS DIALOG
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // ASSIGN IDS
        userProfileName = findViewById(R.id.userProfileName);
        userProfileStatus = findViewById(R.id.userProfileStatus);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        saveProfile = findViewById(R.id.saveProfile);

        // ASSIGN INSTANCE TO FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // LOGIC TO CHECK USER
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    finish();
                    Intent moveToHome = new Intent(UserProfileActivity.this, InstagramHome.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToHome);
                }
            }
        };

        // PROGRESS DIALOG
        mProgress = new ProgressDialog(this);

        // FIREBASE DATABASE INSTANCE
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // ONCLICK SAVE PROFILE BUTTON
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // LOGIC FOR SAVING USER PROFILE
                saveUserProfile();
            }
        });

        // USER IMAGEVIEW ONCLICK LISTENER
        userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // LOGIC FOR PICKING IMAGE
                profilePicSelection();
            }
        });
    }

    private void saveUserProfile() {

        final String userName, userStatus;

        userName = userProfileName.getText().toString().trim();
        userStatus = userProfileStatus.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userStatus)) {

            if (imageHoldUri != null) {

                mProgress.setTitle("Saving profile");
                mProgress.setMessage("Please wait...");
                mProgress.show();

                final String profilePicUrl = imageHoldUri.getLastPathSegment();
                final StorageReference mChildStorage = mStorageRef.child("User_Profile").child(profilePicUrl);

                mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // final Uri imageUrl = (StorageReference) taskSnapshot.getDownloadUrl();
                        // StorageReference newRef = taskSnapshot.getStorage();
                        // Task<Uri> task = mChildStorage.getDownloadUrl();
                        // Log.v(profilePicUrl, taskSnapshot.getMetadata().toString());

                        mUserDatabase.child("username").setValue(userName);
                        mUserDatabase.child("status").setValue(userStatus);
                        mUserDatabase.child("userid").setValue(mAuth.getCurrentUser().getUid());
                        mUserDatabase.child("imageurl").setValue(profilePicUrl);

                        mProgress.dismiss();

                        finish();
                        Intent moveToHome = new Intent(UserProfileActivity.this, InstagramHome.class);
                        moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveToHome);
                    }
                });
            } else {

                Toast.makeText(UserProfileActivity.this, "Please select a profile picture", Toast.LENGTH_LONG).show();
            }
        } else {

            Toast.makeText(UserProfileActivity.this, "Please enter username and status", Toast.LENGTH_LONG).show();
        }
    }

    private void profilePicSelection() {

        // DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY
        final CharSequence[] items = {"Take Photo", "Choose from library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Add Photo!");

        // SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {

        // CHOOSE IMAGE FROM GALLERY
        Log.d("gola", "entered here");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    private void cameraIntent() {

        // CHOOSE CAMERA
        Log.d("gola", "entered here");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SAVE URI FROM GALLERY
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }

        // IMAGE CROP LIBRARY CODE
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                imageHoldUri = result.getUri();
                userProfileImageView.setImageURI(imageHoldUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.e("Image crop failure: ", error.toString());
            }
        }
    }
}
