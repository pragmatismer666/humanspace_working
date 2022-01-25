package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.EditPersonalInfoFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView image_profile;
    private TextView change_profile_photo, save_profile;
    private ImageView close_edit_profile;
    private Button open_personal_info;
    private EditText change_fullName, change_bio, change_website, change_city, change_profession, change_interests;
    private FirebaseUser firebaseUser;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        save_profile = findViewById(R.id.save_profile);
        image_profile = findViewById(R.id.image_profile);
        change_profile_photo = findViewById(R.id.change_profile_photo);
        close_edit_profile = findViewById(R.id.close_edit_profile);
        open_personal_info = findViewById(R.id.open_personal_info);
        change_fullName = findViewById(R.id.change_fullName);
        change_bio = findViewById(R.id.change_bio);
        change_website = findViewById(R.id.change_website);
        change_city = findViewById(R.id.change_city);
        change_profession = findViewById(R.id.change_profession);
        change_interests = findViewById(R.id.change_interests);

        userDetails();
        save_profile.setOnClickListener(v -> saveProfile());

        close_edit_profile.setOnClickListener(v -> finish());
        change_profile_photo.setOnClickListener(v -> cropImage());
        image_profile.setOnClickListener(v -> cropImage());
        open_personal_info.setOnClickListener(v -> {
            Fragment fragment = new EditPersonalInfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.edit_container, fragment).commit();
        });

    }

    private void cropImage() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(EditProfileActivity.this);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return fileRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String myUri = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageurl", myUri);
                    reference.updateChildren(hashMap);

                    pd.dismiss();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            uploadImage();
        } else {
            Toast.makeText(this, "Something went wrong, try later!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {

        final ProgressDialog pd = new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("Saving...");
        pd.show();

        updateProfile(
                change_fullName.getText().toString().trim(),
                change_bio.getText().toString().trim(),
                change_profession.getText().toString().trim(),
                change_city.getText().toString().trim(),
                change_interests.getText().toString().trim(),
                change_website.getText().toString().trim());

        pd.dismiss();
        Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getApplicationContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                change_fullName.setText(String.format(
                        user.getFullName()
                ));
                change_bio.setText(String.format(
                        user.getBio()
                ));
                change_profession.setText(String.format(
                        user.getProfession()
                ));
                change_city.setText(String.format(
                        user.getCity()
                ));
                change_interests.setText(String.format(
                        user.getInterest()
                ));
                change_website.setText(String.format(
                        user.getWebsite()
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile(String fullName, String bio, String profession, String city, String interest, String website) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullName", fullName);
        hashMap.put("bio", bio);
        hashMap.put("profession", profession);
        hashMap.put("city", city);
        hashMap.put("interest", interest);
        hashMap.put("website", website);

        reference.updateChildren(hashMap);
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
