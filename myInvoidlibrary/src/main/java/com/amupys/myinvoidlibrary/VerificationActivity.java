package com.amupys.myinvoidlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath = null;
    private ImageView imageView;
    private CardView pass, driving, adhar;
    private RelativeLayout image_lay;
    private Button btn_back;
    private Class nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        imageView = findViewById(R.id.img_doc);
        pass=findViewById(R.id.passport);
        driving=findViewById(R.id.Driving);
        adhar=findViewById(R.id.adhar);
        image_lay= findViewById(R.id.verified);
        btn_back= findViewById(R.id.btn_back);

        nextActivity = (Class) getIntent().getSerializableExtra("activity");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerificationActivity.this, nextActivity));
                Toast.makeText(VerificationActivity.this, "Document Verified", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        };

        pass.setOnClickListener(listener);
        driving.setOnClickListener(listener);
        adhar.setOnClickListener(listener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString());
                selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    // IF LOCAL IMAGE, NO MATTER IF ITS DIRECTLY FROM GALLERY (EXCEPT PICASSA ALBUM),
                    // OR OI/ASTRO FILE MANAGER. EVEN DROPBOX IS SUPPORTED BY THIS BECAUSE DROPBOX DOWNLOAD THE IMAGE
                    // IN THIS FORM - file:///storage/emulated/0/Android/data/com.dropbox.android/...
                    System.out.println("local image");
                } else {
                    System.out.println("picasa image!");
                    loadPicasaImageFromGallery(selectedImageUri);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {  MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        else
            return uri.getPath();               // FOR OI/ASTRO/Dropbox etc
    }

    private void loadPicasaImageFromGallery(final Uri uri) {
        final Handler handler = new Handler();
        String[] projection = {  MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (columnIndex != -1) {
                try {
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("bitmap", bitmap);
                    user.put("Manufacturer", Build.MANUFACTURER);
                    database.collection("User")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(VerificationActivity.this, "Document uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    // THIS IS THE BITMAP IMAGE WE ARE LOOKING FOR.
                    Glide.with(VerificationActivity.this)
                            .load(bitmap)
                            .centerCrop()
                            .thumbnail(.5f)
                            .into(imageView);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            VerificationActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    image_lay.setVisibility(View.VISIBLE);
                                    btn_back.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        cursor.close();
    }
}