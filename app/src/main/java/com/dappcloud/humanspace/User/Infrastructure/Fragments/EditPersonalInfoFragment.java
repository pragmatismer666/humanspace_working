package com.dappcloud.humanspace.User.Infrastructure.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.EditProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPersonalInfoFragment extends Fragment {

    ImageView back_press;
    TextView username, email, phone, gender, birthday, username_birthday, username_gender;
    Button change_username, change_email, change_phone, change_gender, change_birthday;
    CircleImageView image_profile_birthday, image_profile_gender;
    DatePicker age_picker;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    BottomSheetDialog bottomSheetDialog;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_personal_info, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back_press = view.findViewById(R.id.back_press);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        gender = view.findViewById(R.id.gender);
        birthday = view.findViewById(R.id.birthday);
        change_username = view.findViewById(R.id.change_username);
        change_email = view.findViewById(R.id.change_email);
        change_phone = view.findViewById(R.id.change_phone);
        change_gender = view.findViewById(R.id.change_gender);
        change_birthday = view.findViewById(R.id.change_birthday);

        userDetails();
        updateGender();
        updateBirthday();
        back_press.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));

        return view;
    }

    private void updateBirthday() {
        change_birthday.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

            View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_edit_birthday_bottom_sheet,
                    (ViewGroup) v.findViewById(R.id.bottom_sheet_edit_birthday));

            image_profile_birthday = sheetView.findViewById(R.id.image_profile_birthday);
            username_birthday = sheetView.findViewById(R.id.username_birthday);
            age_picker = sheetView.findViewById(R.id.age_picker);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    try {
                        Glide.with(getContext()).load(user.getImageurl()).into(image_profile_birthday);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    username_birthday.setText(String.format(
                            user.getUsername()
                    ));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sheetView.findViewById(R.id.save_birthday).setOnClickListener(v1 -> {
                if (!validateAge()) {
                    return;
                }

                int day = age_picker.getDayOfMonth();
                int month = age_picker.getMonth()+1;
                int year = age_picker.getYear();
                String _date = day+"/"+month+"/"+year;

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("date", _date);

                reference1.updateChildren(hashMap);

                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
    }

    private void updateGender() {
        change_gender.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

            final View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_edit_gender_bottom_sheet,
                    (ViewGroup) v.findViewById(R.id.bottom_sheet_edit_gender));

            image_profile_gender = sheetView.findViewById(R.id.image_profile_gender);
            username_gender = sheetView.findViewById(R.id.username_gender);
            radioGroup = sheetView.findViewById(R.id.radio_group);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    try {
                        Glide.with(getContext()).load(user.getImageurl()).into(image_profile_gender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    username_gender.setText(String.format(
                            user.getUsername()
                    ));
                    if (user.getGender().equals("Male")) {
                        radioGroup.check(R.id.male);
                    } else if (user.getGender().equals("Female")) {
                        radioGroup.check(R.id.female);
                    } else {
                        radioGroup.check(R.id.others);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sheetView.findViewById(R.id.save_gender).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!validateGender()) {
                        return;
                    }

                    selectedGender = sheetView.findViewById(radioGroup.getCheckedRadioButtonId());
                    String _gender = selectedGender.getText().toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("gender", _gender);

                    reference.updateChildren(hashMap);

                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        });
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

                username.setText(String.format(
                        user.getUsername()
                ));
                email.setText(String.format(
                        user.getEmail()
                ));
                phone.setText(String.format(
                        user.getPhone()
                ));
                gender.setText(String.format(
                        user.getGender()
                ));
                birthday.setText(String.format(
                        user.getDate()
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = age_picker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 12) {
            Toast.makeText(getContext(), "You must be at least 13 years of age!", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

}
