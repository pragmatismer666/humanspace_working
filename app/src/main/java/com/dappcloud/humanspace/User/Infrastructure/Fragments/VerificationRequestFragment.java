package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.BRequest;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Databases.VRequest;
import com.dappcloud.humanspace.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class VerificationRequestFragment extends Fragment {

    ImageView back_press;
    LinearLayout no_request;
    LinearLayout requested;
    CircleImageView image_profile;
    TextView name;
    TextView sent_request;
    TextView category_name;
    TextView status;
    TextView response;

    TextView username;
    TextInputLayout fullname;
    TextInputLayout known_as;
    TextInputLayout category;
    AutoCompleteTextView category_edt;
    TextView choose_photo;
    Button btn_send_verification;
    ProgressBar progress_bar;

    FirebaseUser firebaseUser;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private static final int IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification_request, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("verify_acc_files");

        back_press = view.findViewById(R.id.back_press);
        no_request = view.findViewById(R.id.no_request);
        requested = view.findViewById(R.id.requested);
        userRequests();

        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        known_as = view.findViewById(R.id.known_as);
        category = view.findViewById(R.id.category);
        category_edt = view.findViewById(R.id.category_edt);
        choose_photo = view.findViewById(R.id.choose_photo);
        btn_send_verification = view.findViewById(R.id.btn_send_verification);
        progress_bar = view.findViewById(R.id.progress_bar);

        image_profile = view.findViewById(R.id.image_profile);
        name = view.findViewById(R.id.name);
        sent_request = view.findViewById(R.id.sent_request);
        category_name = view.findViewById(R.id.category_name);
        status = view.findViewById(R.id.status);
        response = view.findViewById(R.id.response);

        categoryDropdown();
        userDetails();
        requestDetails();
        back_press.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountFragment()).commit());
        choose_photo.setOnClickListener(v -> openImage());
        btn_send_verification.setOnClickListener(v -> send());

        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Something went wrong, try later!", Toast.LENGTH_SHORT).show();
        }
    }

    private void send() {

        if (!validateFullname() | !validateKnownas() | !validateCategory()) {
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);
        btn_send_verification.setVisibility(View.INVISIBLE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
        String currentDateandTime = simpleDateFormat.format(new Date());

        final String userId = firebaseUser.getUid();
        final String fullName = fullname.getEditText().getText().toString().trim();
        final String knownAs = known_as.getEditText().getText().toString().trim();
        final String categoryType = category_edt.getText().toString().trim();
        final String status = "Pending";
        final String submitted = currentDateandTime;

        if (imageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    final String myUri = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("VRequests").child(userId);
                    VRequest newRequest = new VRequest(userId, fullName, knownAs, categoryType, myUri, status, submitted);
                    reference.setValue(newRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new AccountFragment()).commit();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Failed to upload!", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_send_verification.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.INVISIBLE);
                btn_send_verification.setVisibility(View.VISIBLE);
            });
        } else {
            Toast.makeText(getActivity(), "No image selected!", Toast.LENGTH_SHORT).show();
            progress_bar.setVisibility(View.INVISIBLE);
            btn_send_verification.setVisibility(View.VISIBLE);
        }
    }

    private void userDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);
                try {
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                name.setText(String.format(
                        user.getUsername()
                ));
                username.setText(String.format(
                        user.getUsername()
                ));
                fullname.getEditText().setText(String.format(
                        user.getFullName()
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userRequests() {
        Query checkUser = FirebaseDatabase.getInstance().getReference("VRequests").orderByChild("userid").equalTo(firebaseUser.getUid());
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                if (snapshot.exists()) {
                    no_request.setVisibility(View.GONE);
                    requested.setVisibility(View.VISIBLE);
                } else {
                    no_request.setVisibility(View.VISIBLE);
                    requested.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requestDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("BRequests").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                if (snapshot.exists()) {
                    BRequest request = snapshot.getValue(BRequest.class);
                    sent_request.setText("Your request is being processed.");
                    category_name.setText(request.getCategory());
                    status.setText(request.getStatus());
                    response.setText("Be patient this will take time as our team will be reviewing your profile.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validateFullname() {
        String val = fullname.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Field must not be empty!");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateKnownas() {
        String val = known_as.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            known_as.setError("Field must not be empty!");
            return false;
        } else {
            known_as.setError(null);
            known_as.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCategory() {
        String val = category.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            category.setError("Field must not be empty!");
            return false;
        } else {
            category.setError(null);
            category.setErrorEnabled(false);
            return true;
        }
    }

    private void categoryDropdown() {
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.dropdown_menu_items,
                categories
        );
        category_edt.setAdapter(cityAdapter);
    }

    private static final String[] categories = new String[]{
            "News/Media", "Sports", "Government/Politics", "Music", "Fashion", "Entertainment", "Blogger/Influencer", "Business/Brand/Organization", "Taxi/Commuting",
            "Exercise/Fitness", "Tech"

    };

}
