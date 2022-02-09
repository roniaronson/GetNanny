package com.example.getnanny20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
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
import java.util.List;
import java.util.Locale;

public class ActivityAddPost extends AppCompatActivity implements LocationListener{
    public static final int GALLERY_REQUEST_CODE = 105;

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
        checkLocationPermission();
        findViews();
        initBTNs();
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
            //FirebaseDatabase database = FirebaseDatabase.getInstance("https://weshare-70609-default-rtdb.firebaseio.com/");
            //DatabaseReference myRef = database.getReference("posts");
            FirebaseUser user = fAuth.getCurrentUser();

            Post post = new Post().
                    setUserID(user.getUid()).
                    setName(user.getDisplayName()).
                    setAmount(Integer.valueOf(addpost_EDT_hourlyrate.getEditText().getText().toString())).
                    setDateString(addpost_EDT_description.getEditText().toString()).
                    setImage("").
                    setLat(lat).setLon(lon);

            getLocation(post);
            uploadImageToFirebase(contentUri, post);
            Log.d("checkImg", "" + imageLink);
            post.setImage("" + imageLink);
            //myRef.child("meal_" + post.getMealId()).setValue(post);

            //MyFirebaseDB.setCounter("meals_counter", Meal.getCounter());

            finish();
            Intent intent = new Intent(this, ActivityMenu.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "One or more field are invalid!", Toast.LENGTH_LONG).show();
    }

    private void getLocation(Post post) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lon, 1);
        post.setLocation(addresses.get(0).getAddressLine(0));

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

    private void uploadImageToFirebase(Uri contentUri, Post post) {
        imageFileName = "post_" + post.getUserID() + "." + getFileExt(contentUri);
        final StorageReference image = storageReference.child("pictures/" + imageFileName);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageLink = "" + uri.toString();

                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://get-nanny-1a620-default-rtdb.firebaseio.com/");
                        DatabaseReference myRef = database.getReference("posts");
                        myRef.child("post_"+post.getUserID()).child("image").setValue(imageLink);
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

    private void checkLocationPermission() {
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(ActivityAddPost.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityAddPost.this,new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        }


        getLocation();
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, (LocationListener) ActivityAddPost.this);

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = location.getLatitude();
            lon = location.getLongitude();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(ActivityAddPost.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            lat = location.getLatitude();
            lon = location.getLongitude();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
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
}
