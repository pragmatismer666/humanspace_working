package com.dappcloud.humanspace.User.Infrastructure.Activities;

import static androidx.core.graphics.TypefaceCompatUtil.getTempFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity {

    ImageView close, image_added;
    TextView share, edit_image;
    CircleImageView image_profile;
    EditText caption;

    Uri imageUri;
    String myUrl = "";
    StorageTask storageTask;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    AnstronCoreHelper coreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("post");
        coreHelper = new AnstronCoreHelper(this);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        share = findViewById(R.id.share);
        edit_image = findViewById(R.id.edit_image);
        image_profile = findViewById(R.id.image_profile);
        caption = findViewById(R.id.caption);


        close.setOnClickListener(v -> finish());
        share.setOnClickListener(v -> uploadImage());
        edit_image.setOnClickListener(v -> {
             startActivity(new Intent(getApplicationContext(), AddPostActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(AddPostActivity.this);
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

    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Posting");
            pd.show();
            //Compression
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                    +"."+ getFileExtension(imageUri));

            storageTask = imageReference.putFile(imageUri);
            storageTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    myUrl = downloadUri.toString();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                    String currentDate = simpleDateFormat.format(new Date());

                    String publisher = firebaseUser.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
                    String postId = reference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postUrl", myUrl);
                    hashMap.put("type", "Image");
                    hashMap.put("createdOn", currentDate);
                    hashMap.put("createdAt", ServerValue.TIMESTAMP);
                    hashMap.put("caption", caption.getText().toString());
                    hashMap.put("postId", postId);
                    hashMap.put("publisher", publisher);
                    reference.child(postId).setValue(hashMap);

                    Toast.makeText(AddPostActivity.this, "You just posted an image!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                } else {
                    Toast.makeText(AddPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(AddPostActivity.this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            image_added.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No image was selected, try later!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddPostActivity.this, UserDashboardActivity.class));
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
