package com.example.getnanny20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegistration extends AppCompatActivity {

    private TextInputLayout reg_EDT_email;
    private TextInputLayout reg_EDT_password;
    private TextInputLayout reg_EDT_name;
    private TextInputLayout reg_EDT_phone;
    private MaterialTextView reg_TXT_login;

    private TextInputLayout[] allFields;
    private MaterialButton reg_BTN_register;

    private FirebaseAuth fAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fAuth = FirebaseAuth.getInstance();

        findViews();
        initBTNs();
        checkFormValidation();

    }

    private boolean isValid() {
        for (TextInputLayout field : allFields) {
            if (field.getError() != null || field.getEditText().getText().toString().isEmpty())
                return false;
        }
        return true;
    }

    private void checkFormValidation() {

        Validator.Builder
                .make(reg_EDT_password)
                .addWatcher(new Validator.Watcher_Password("Invalid password"))
                .build();

        Validator.Builder
                .make(reg_EDT_phone)
                .addWatcher(new Validator.Watcher_Phone("Invalid phone number"))
                .build();

        Validator.Builder
                .make(reg_EDT_email)
                .addWatcher(new Validator.Watcher_Email("Invalid email"))
                .build();
    }

    private void initBTNs() {
        reg_BTN_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { register();}
        });

        reg_TXT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ActivityRegistration.this, ActivityStart.class);
                startActivity(intent);
            }
        });
    }

    private void register() {
        if (isValid()) {
            addToDB();
            Toast.makeText(this, "Registration completed!", Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, ActivityStart.class);
            startActivity(intent);
        }

        else
            Toast.makeText(this, "One or more field are invalid!", Toast.LENGTH_LONG).show();
    }

    private void addToDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://get-nanny-1a620-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("users");

        fAuth.createUserWithEmailAndPassword(reg_EDT_email.getEditText().getText().toString(), reg_EDT_password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            Toast.makeText(ActivityRegistration.this, "Verification mail has been sent!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ActivityRegistration.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(ActivityRegistration.this, "Registration completed!", Toast.LENGTH_LONG).show();
                    userID = fAuth.getCurrentUser().getUid();

                    User user = new User();
                    user.setUserID(userID.toString());
                    user.setEmail(reg_EDT_email.getEditText().getText().toString());
                    user.setPassword(reg_EDT_password.getEditText().getText().toString());
                    user.setName(reg_EDT_name.getEditText().getText().toString());
                    user.setPhoneNumber(reg_EDT_phone.getEditText().getText().toString());
                    user.setPost(new Post().setDescription(""));

                    myRef.child(userID).setValue(user);
                }
                else
                    Toast.makeText(ActivityRegistration.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void findViews() {
        reg_EDT_email = findViewById(R.id.reg_EDT_email);
        reg_EDT_password = findViewById(R.id.reg_EDT_password);
        reg_EDT_name = findViewById(R.id.reg_EDT_name);
        reg_EDT_phone = findViewById(R.id.reg_EDT_phone);
        reg_BTN_register = findViewById(R.id.reg_BTN_register);
        reg_TXT_login = findViewById(R.id.reg_TXT_login);

        allFields = new TextInputLayout[] {
                reg_EDT_email,
                reg_EDT_password,
                reg_EDT_name,
                reg_EDT_phone
        };
    }
}
