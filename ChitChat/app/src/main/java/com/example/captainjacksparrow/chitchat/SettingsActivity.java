package com.example.captainjacksparrow.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout --
    private CircleImageView mImage;
    private TextView mName;
    private TextView mStatus;
    private Button changeStatus_btn;
    private Button changeImage_btn;


    private static final int GALLERY_PICK = 234;


    // Storage Firebase --
    private StorageReference mImageStorage;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mStatus = (TextView) findViewById(R.id.settings_status);
        changeImage_btn = (Button) findViewById(R.id.change_image_btn);
        changeStatus_btn = (Button) findViewById(R.id.change_status_btn);


        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Userdetails userdetails = new Userdetails();
                userdetails.setName(name);
                userdetails.setStatus(status);

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default")) {

                    Picasso.get().load(image).placeholder(R.drawable.default_avatar_large).into(mImage);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeStatus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("status_value", status_value);
                startActivity(statusIntent);

            }
        });

        changeImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                /*
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                        */

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data  != null && data.getData() != null) {

            Uri imageURI = data.getData();

            // Cropping image --
            CropImage.activity(imageURI)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);

            //Toast.makeText(SettingsActivity.this, imageURI, Toast.LENGTH_LONG).show();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgress = new ProgressDialog(SettingsActivity.this);
                //mProgress.setTitle("Uploding Image...");
                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setMessage("Uploding Image...");
                mProgress.setMax(100);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                Uri resultUri = result.getUri();

                String current_user_id = mCurrentUser.getUid();


                // Compress image & make it thumbnail image --
                File thumb_filePath = new File(resultUri.getPath());
                Bitmap thum_bitmap = null;
                try {

                    thum_bitmap = new Compressor(this)
                                        .setMaxWidth(200)
                                        .setMaxHeight(200)
                                        .setQuality(75)
                                        .compressToBitmap(thumb_filePath);

                } catch (IOException e) {

                    e.printStackTrace();

                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thum_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte =baos.toByteArray();



                StorageReference filePath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumbFilePath = mImageStorage.child("profile_images").child("thumb").child(current_user_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            final String download_url = task.getResult().getDownloadUrl().toString();

                            // Upload thumbnail image into Firebase --
                            UploadTask uploadTask = thumbFilePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map update_hashmap = new HashMap();
                                        update_hashmap.put("image", download_url);
                                        update_hashmap.put("thumb_image", thumb_download_url);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mProgress.dismiss();

                                                    Toast.makeText(SettingsActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                    } else {

                                        mProgress.dismiss();
                                        Toast.makeText(SettingsActivity.this, "There are some error in uploading thumb image", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });




                        } else {

                            mProgress.dismiss();
                            Toast.makeText(SettingsActivity.this, "There are some error in uploading image", Toast.LENGTH_LONG).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Failed"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                mProgress.dismiss();

                Toast.makeText(SettingsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;

        for (int i = 0; i<randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        return randomStringBuilder.toString();
    }
}
