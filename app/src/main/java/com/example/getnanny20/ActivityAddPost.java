package com.example.getnanny20;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;


public class ActivityAddPost extends AppCompatActivity{
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int REQUEST_LOCATION = 1;
    private StorageReference storageReference;
    private Uri contentUri;
    private String imageFileName;
    private String imageLink;
    private boolean isImgOk = false;
    private boolean isDateOk = false;
    private boolean isParent;
    private FusedLocationProviderClient client;
    private LocationManager locationManager;
    private double lat, lon;
    private MaterialDatePicker.Builder builder;
    private MaterialDatePicker materialDatePicker;
    private String dateString;

    private MaterialTextView addpost_TXT_title;
    private TextInputLayout addpost_EDT_hourlyrate;
    private TextInputLayout addpost_EDT_description;
    private TextInputLayout addpost_EDT_experience;
    private TextInputLayout addpost_EDT_age;
    private MaterialButton addpost_BTN_back;
    private ShapeableImageView addpost_IMG_addpicture;
    private MaterialButton addpost_BTN_share;
    private MaterialButton addpost_BTN_datePicker;
    private TextInputLayout[] allFields;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            isParent = extras.getBoolean("isParent");
        }
        fAuth = FirebaseAuth.getInstance();
        findViews();
        initDate();
        updateUI();
        initBTNs();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void updateUI() {
        if(isParent){
            addpost_TXT_title.setText("Find The Best Babysitter");
            addpost_EDT_hourlyrate.setHint("Number Of Children");
            addpost_BTN_datePicker.setVisibility(View.VISIBLE);
            addpost_EDT_experience.setVisibility(View.INVISIBLE);
            addpost_EDT_age.setVisibility(View.INVISIBLE);
        }
        else{
            isDateOk = true;
            addpost_TXT_title.setText("Get a Job Next to You");
            addpost_EDT_hourlyrate.setHint("Your Hourly Rate");
            addpost_BTN_datePicker.setVisibility(View.INVISIBLE);
            addpost_EDT_experience.setVisibility(View.VISIBLE);
            addpost_EDT_age.setVisibility(View.VISIBLE);
        }
    }


    private void initBTNs() {
        addpost_BTN_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { datePick();}
        });
        addpost_IMG_addpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { imgUpload();}
        });

        addpost_BTN_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    share();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        addpost_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ActivityAddPost.this, ActivityMenu.class);
                startActivity(intent);
            }
        });
    }
    private void initDate() {
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select A Date");
        materialDatePicker = builder.build();
    }
    private void share() throws IOException {
        if (isValid()) {

            FirebaseUser user = fAuth.getCurrentUser();

            Post post = new Post().
                    setUserID(user.getUid()).
                    setPhoneNumber("").
                    setName("").
                    setDescription(addpost_EDT_description.getEditText().getText().toString()).
                    setImage("").
                    setIsParent(isParent).
                    setLat(lat).setLon(lon);

            if(isParent){
                post.setNumOfChildren(Integer.valueOf(addpost_EDT_hourlyrate.getEditText().getText().toString()));
                post.setDateString(dateString);
            }
            else{
                post.setHourlyRate(Integer.valueOf(addpost_EDT_hourlyrate.getEditText().getText().toString()));
                post.setAge(Integer.valueOf(addpost_EDT_age.getEditText().getText().toString()));
                post.setYearsOfExperience(Integer.valueOf(addpost_EDT_experience.getEditText().getText().toString()));
            }


            uploadImageToFirebase(contentUri, post, user);

            finish();
            Intent intent = new Intent(this, ActivityMap.class);
            intent.putExtra("isParent", isParent);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "One or more field are invalid!", Toast.LENGTH_LONG).show();
    }


    private void imgUpload() {
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            contentUri = data.getData();
            addpost_IMG_addpicture.setImageURI(contentUri);
            isImgOk = true;
        }
    }

    private void uploadImageToFirebase(Uri contentUri, Post post, FirebaseUser user) {
        imageFileName = "post_" + post.getUserID() + "." + getFileExt(contentUri);
        final StorageReference image = storageReference.child("pictures/" + imageFileName);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://get-nanny-1a620-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("users");
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageLink = "" + uri.toString();
                        post.setImage(imageLink);
                        MyFirebaseDB.CallBack_Users callBack_users = new MyFirebaseDB.CallBack_Users() {
                            @Override
                            public void dataReady(ArrayList<User> users) {
                                for (int i = 0; i < users.size(); i++) {
                                    if(users.get(i).getUserID().equals(user.getUid())){
                                        post.setName(users.get(i).getName());
                                        post.setPhoneNumber(users.get(i).getPhoneNumber());
                                        users.get(i).setPost(post);
                                        myRef.child(user.getUid()).setValue(users.get(i));
                                    }

                                }
                            }
                        };
                        MyFirebaseDB.getUsers(callBack_users);
                    }
                });

                Toast.makeText(ActivityAddPost.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityAddPost.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private boolean isValid() {
        for (TextInputLayout field : allFields) {
            if (field.getError() != null || field.getEditText().getText().toString().isEmpty())
                return false;
        }
        if (!isImgOk || !isDateOk)
            return false;
        return true;
    }


    private void findViews() {
        addpost_TXT_title = findViewById(R.id.addpost_TXT_title);
        addpost_EDT_hourlyrate = findViewById(R.id.addpost_EDT_hourlyrate);
        addpost_EDT_description = findViewById(R.id.addpost_EDT_description);
        addpost_IMG_addpicture = findViewById(R.id.addpost_IMG_addpicture);
        addpost_BTN_share = findViewById(R.id.addpost_BTN_share);
        addpost_BTN_back = findViewById(R.id.addpost_BTN_back);
        addpost_EDT_experience = findViewById(R.id.addpost_EDT_experience);
        addpost_EDT_age = findViewById(R.id.addpost_EDT_age);
        addpost_BTN_datePicker = findViewById(R.id.addpost_BTN_datePicker);
        if(isParent){
            allFields = new TextInputLayout[] {
                    addpost_EDT_hourlyrate,
                    addpost_EDT_description,
            };
        }
        else{
            allFields = new TextInputLayout[] {
                    addpost_EDT_hourlyrate,
                    addpost_EDT_description,
                    addpost_EDT_age,
                    addpost_EDT_experience
            };
        }

    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                ActivityAddPost.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ActivityAddPost.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double lon = locationGPS.getLongitude();
                this.lat = lat;
                this.lon = lon;
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void datePick() {
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Object selection) {
                dateString = "" + materialDatePicker.getHeaderText();
                isDateOk = true;
            }
        });
    }


}
