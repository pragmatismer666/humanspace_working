package com.dappcloud.humanspace.User.SigninSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dappcloud.humanspace.Databases.User;
import com.dappcloud.humanspace.Databases.UserLocation;
import com.dappcloud.humanspace.Maps.Utils.Utils;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.Infrastructure.Activities.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp6thScreen extends AppCompatActivity {

    private CheckBox checkbox;
    private Button next;
    private RelativeLayout textView;
    private LottieAnimationView progressBar;

    String userId, userPhone, email, newsLater, name, gender, date, account, category, city, profession, interest, bio, isVerified, isFeature, website, imageurl, status, username;
    private RadioGroup m_accountRadios;
    private RadioGroup m_categoryRadios1;
    private RadioGroup m_categoryRadios2;
    private LinearLayout businessCategoriesLayout;
    private TextView option_desc, check_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up6th_screen);

        checkbox = findViewById(R.id.checkbox);
        next = findViewById(R.id.next);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progress_bar);
        option_desc = findViewById(R.id.option_desc);
        check_error = findViewById(R.id.check_error);
        m_accountRadios = findViewById(R.id.accountRadios);
        m_categoryRadios1 = findViewById(R.id.categoryRadios1);
        m_categoryRadios1.setOnCheckedChangeListener(listener1);
        m_categoryRadios2 = findViewById(R.id.categoryRadios2);
        m_categoryRadios2.setOnCheckedChangeListener(listener2);
        businessCategoriesLayout = findViewById(R.id.businessCategories);
        businessCategoriesLayout.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        option_desc.setVisibility(View.GONE);
        check_error.setVisibility(View.GONE);

        userPhone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        newsLater = getIntent().getStringExtra("newsLater");
        name = getIntent().getStringExtra("name");
        gender = getIntent().getStringExtra("gender");
        date = getIntent().getStringExtra("date");
        account = "Personal";
        category = "Personal";
        city = "";
        username = "";
        profession = "";
        interest = "";
        bio = "";
        isVerified = "No";
        isFeature = "No";
        website = "";
        imageurl = "";
        status = "Offline";

        next.setOnClickListener(v -> nextScreen());
        m_accountRadios.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked) {
                account = checkedRadioButton.getText().toString();
                if ( account.equals("Business") ){
                    businessCategoriesLayout.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    option_desc.setVisibility(View.VISIBLE);
                    category = "Car";
                    checkImageUrl(category);
                }
                else {
                    category = "Personal";
                    if ( gender.equals(getString(R.string.female)) ) {
                            imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/user_avatar.png?alt=media&token=d3b2d210-92ec-472b-8c71-2d2756f20a5a";
                    }
                    else {
                            imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/user_avatar.png?alt=media&token=d3b2d210-92ec-472b-8c71-2d2756f20a5a";
                    }
                    m_categoryRadios2.setOnCheckedChangeListener(null);
                    m_categoryRadios2.clearCheck();
                    m_categoryRadios2.setOnCheckedChangeListener(listener2);
                    RadioButton car = (RadioButton)findViewById(R.id.carRadio);
                    car.setChecked(true);
                    businessCategoriesLayout.setVisibility(View.GONE);
                }
            }
        });

        if (checkbox.isChecked()) {
            check_error.setVisibility(View.GONE);
            checkbox.setError(null);
        }
    }

    private void nextScreen() {
        if (!validateCheckBox()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();
        UserLocation current = new UserLocation(-33.9, 18.5);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        User newUser = new User(userId, userPhone, email, newsLater, name, gender, date, account, category, city, profession, interest, bio, isVerified, isFeature, website, imageurl, status, username, current);
        reference.setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utils utils = new Utils();
                utils.currentUser(() -> {
                    utils.usersList(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), SignUp7thScreen.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                    //finish();
                });
            } else {
                progressBar.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                Toast.makeText(SignUp6thScreen.this, "You cannot use this email, it's already registered!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateCheckBox() {
        if (checkbox.isChecked()) {
            check_error.setVisibility(View.GONE);
            checkbox.setError(null);
            return true;
        } else {
            checkbox.setError("The check box must be checked to proceed");
            check_error.setVisibility(View.VISIBLE);
            check_error.setText("The check box must be checked to proceed");
            return false;
        }
    }

    private final RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if ( checkedId != -1 ) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && !account.equals("Personal")) {
                    m_categoryRadios2.setOnCheckedChangeListener(null);
                    m_categoryRadios2.clearCheck();
                    m_categoryRadios2.setOnCheckedChangeListener(listener2);
                    category = checkedRadioButton.getText().toString();
                    checkImageUrl(category);
                }
            }
        }
    };

    private final RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if ( checkedId != -1 ){
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && !account.equals("Personal")) {
                    m_categoryRadios1.setOnCheckedChangeListener(null);
                    m_categoryRadios1.clearCheck();
                    m_categoryRadios1.setOnCheckedChangeListener(listener1);
                    category = checkedRadioButton.getText().toString();
                    checkImageUrl(category);
                }
            }
        }
    };

    private void checkImageUrl(String category) {
        switch (category) {
            case "Car":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/car.png?alt=media&token=14a44ae6-5fe0-4bc0-9549-e9c7500fcf5f";
                break;
            case "Nanny":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/Nanny.jpg?alt=media&token=89cd9552-5583-447e-b7a5-615c1b1d5620";
                break;
            case "Cleaner":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/cleaner.jpg?alt=media&token=c59c8c24-4f79-474c-a399-5f626d150c6e";
                break;
            case "Doctor":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/Doctor.jpg?alt=media&token=a6913360-301b-4de7-ab41-75ba80d99bb3";
                break;
            case "Lawyer":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/Lawyer.jpg?alt=media&token=cc2c27b9-414c-4ff7-a1b0-16a13c569c94";
                break;
            case "Mechanic":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/Mechanic.jpg?alt=media&token=ef89c953-df36-4503-ad36-4cb017d57295";
                break;
            case "Stripper":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/stripper.png?alt=media&token=c7ab7549-488a-4e83-8034-b4716f719e11";
                break;
            case "Graphics Designer / Web Developer / Software Engineer":
                imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/web%20developer%20and%20graphic%20designers.jpg?alt=media&token=9184f853-a60b-467e-80a4-bbb9ed46d91c";
                break;
            case "Tutor":
               imageurl = "https://firebasestorage.googleapis.com/v0/b/human-space-2021-b0035.appspot.com/o/tutor.jpg?alt=media&token=c66b5275-fbc4-435e-b719-29a683cb6064";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + category);
        }
    }


}
