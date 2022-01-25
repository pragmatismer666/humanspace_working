package com.dappcloud.humanspace.User.SigninSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.EditProfileActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.FeaturedActivity;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp7thScreen extends AppCompatActivity {

    private CircleImageView image_profile;
    private TextView change_profile_photo, name;
    private EditText username;
    private Button done;
    private RelativeLayout photo_title;
    private LottieAnimationView progress_bar;

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up7th_screen);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        image_profile = findViewById(R.id.image_profile);
        change_profile_photo = findViewById(R.id.change_profile_photo);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        photo_title = findViewById(R.id.photo_title);
        progress_bar = findViewById(R.id.progress_bar);
        done = findViewById(R.id.done);

        photo_title.setVisibility(View.GONE);
        image_profile.setVisibility(View.GONE);
        change_profile_photo.setVisibility(View.GONE);

        userDetails();
        done.setOnClickListener(v -> signIn());
        change_profile_photo.setOnClickListener(v -> updatePic());

    }

    private void updatePic() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(SignUp7thScreen.this);
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
                    Toast.makeText(SignUp7thScreen.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(SignUp7thScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(SignUp7thScreen.this, "No image selected!", Toast.LENGTH_SHORT).show();
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

                if(user.getAccount().equals("Personal")) {
                    photo_title.setVisibility(View.VISIBLE);
                    image_profile.setVisibility(View.VISIBLE);
                    change_profile_photo.setVisibility(View.VISIBLE);
                }

                if(user.getAccount().equals("Business")) {
                    image_profile.setVisibility(View.VISIBLE);
                }

                name.setText(user.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void signIn() {
        if (!validateUsername()) {
            return;
        }

        String user = username.getText().toString().trim();
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(user);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progress_bar.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                    username.setError("Username taken use another");
                    username.requestFocus();

                } else {
                    updateUsername();
                    Intent intent = new Intent(getApplicationContext(), FeaturedActivity.class);
                    startActivity(intent);
                    progress_bar.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUsername() {
        final ProgressDialog pd = new ProgressDialog(SignUp7thScreen.this);
        pd.setMessage("Saving...");
        pd.show();

        updateProfile(username.getText().toString().trim());
        pd.dismiss();
        finish();
    }

    private void updateProfile(String userName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", userName);

        reference.updateChildren(hashMap);
    }

    private boolean validateUsername() {
        String _username = username.getText().toString().trim();

        if (_username.isEmpty()) {
            username.setError("Please enter your username");
            username.requestFocus();
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }
}
