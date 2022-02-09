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
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
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

    private FusedLocationProviderClient client;
    private LocationManager locationManager;
    private double lat, lon;

    private TextInputLayout addpost_EDT_hourlyrate;
    private TextInputLayout addpost_EDT_description;
    private MaterialButton addpost_BTN_back;
    private ShapeableImageView addpost_IMG_addpicture;
    private MaterialButton addpost_BTN_share;
    private TextInputLayout[] allFields;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        fAuth = FirebaseAuth.getInstance();
        findViews();
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


    private void initBTNs() {
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

    private void share() throws IOException {
        if (isValid()) {

            FirebaseUser user = fAuth.getCurrentUser();

            Post post = new Post().
                    setUserID(user.getUid()).
                    setName(user.getDisplayName()).
                    setAmount(Integer.valueOf(addpost_EDT_hourlyrate.getEditText().getText().toString())).
                    setDateString(addpost_EDT_description.getEditText().toString()).
                    setImage("").
                    setLat(lat).setLon(lon);

            uploadImageToFirebase(contentUri, post, user);

            finish();
            Intent intent = new Intent(this, ActivityMenu.class);
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
                Toast.makeText(ActivityAddPost.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
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
        if (!isImgOk)
            return false;
        return true;
    }


    private void findViews() {
        addpost_EDT_hourlyrate = findViewById(R.id.addpost_EDT_hourlyrate);
        addpost_EDT_description = findViewById(R.id.addpost_EDT_description);
        addpost_IMG_addpicture = findViewById(R.id.addpost_IMG_addpicture);
        addpost_BTN_share = findViewById(R.id.addpost_BTN_share);
        addpost_BTN_back = findViewById(R.id.addpost_BTN_back);
        allFields = new TextInputLayout[] {
                addpost_EDT_hourlyrate,
                addpost_EDT_description
        };
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


}
