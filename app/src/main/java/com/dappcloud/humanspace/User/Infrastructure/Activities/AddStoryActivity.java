package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStoryActivity extends AppCompatActivity {

    ImageView close, image_added;
    TextView share, edit_image;
    CircleImageView image_profile;
    EditText caption;

    private Uri mImageUri;
    String myUrl = "";
    private StorageTask storageTask;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("story");

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        share = findViewById(R.id.share);
        edit_image = findViewById(R.id.edit_image);
        image_profile = findViewById(R.id.image_profile);
        caption = findViewById(R.id.caption);

        share.setOnClickListener(v -> publishStory());
        close.setOnClickListener(v -> finish());
        edit_image.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddStoryActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        CropImage.activity()
                .setAspectRatio(9, 16)
                .start(AddStoryActivity.this);

        userDetails();
    }

    private void userDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void publishStory() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();

        if (mImageUri != null) {
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            storageTask = imageReference.putFile(mImageUri);
            storageTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    myUrl = downloadUri.toString();

                    String myid = firebaseUser.getUid();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(myid);

                    String storyid = reference.push().getKey();
                    long timeend = System.currentTimeMillis() + 86400000; //1 day

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageurl", myUrl);
                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                    hashMap.put("timeend", timeend);
                    hashMap.put("storyId", storyid);
                    hashMap.put("caption", caption.getText().toString());
                    hashMap.put("userId", myid);
                    reference.child(storyid).setValue(hashMap);

                    Toast.makeText(AddStoryActivity.this, "Your story has been updated!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                } else {
                    Toast.makeText(AddStoryActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(AddStoryActivity.this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            image_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "No Image was selected, try later!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this, UserDashboardActivity.class));
            finish();
        }
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
