package com.challenger.app.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.challenger.app.Adapter.ChallengeAdapter;
import com.challenger.app.Model.Challenge;
import com.challenger.app.Model.User;
import com.challenger.app.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {
    FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Challenge> mUserChallenges;
    private ChallengeAdapter challengeAdapter;
    TextView fullName,email,phone;

    ImageView profPicture;
    FirebaseFirestore mFirestoreDb;
    String userId;
    private DocumentReference userRef;

    private FirebaseUser currentUser;

    FirebaseStorage storage;
    StorageReference storageRef;

    TextView szemelyesLabel,lefoglaltText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        szemelyesLabel = findViewById(R.id.szemelyesLabel);
        lefoglaltText = findViewById(R.id.lefoglaltText);
        szemelyesLabel.setText(Html.fromHtml("<u>Személyes adatok:</u>"));
        lefoglaltText.setText(Html.fromHtml("<u>Jelenlegi kihívásaim</u>"));

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        profPicture = findViewById(R.id.profilePicture);

        fullName = findViewById(R.id.profileNameTextView);
        phone = findViewById(R.id.profilePhone);
        email = findViewById(R.id.profileEmail);

        mRecyclerView = findViewById(R.id.tripProfileRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mUserChallenges = new ArrayList<>();
        challengeAdapter = new ChallengeAdapter(this, mUserChallenges,true);
        mRecyclerView.setAdapter(challengeAdapter);

        initializeFirebase();

       // SafetyNetAppCheckProviderFactory factory = SafetyNetAppCheckProviderFactory.getInstance();
        //FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory);

    }

    private void initializeFirebase() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();
        mFirestoreDb = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        currentUser = mAuth.getCurrentUser();
        userRef = mFirestoreDb.collection("users").document(userId);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser(); // Biztosítjuk, hogy a currentUser friss legyen
        }
        queryUserProfile();
        loadUserChallenge();
    }

    //-------Képfeltötés , Galéria megnyitás stb... minden ami a profilképhez kapcsolódik------

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int GALLERY_REQUEST = 102;

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST);
            } else {
                openGallery();
            }
        } else {
            // Android 10 és újabb verziókon nincs szükség külön engedélyre a galériából való olvasáshoz
            openGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Engedély szükséges a galéria használatához", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST) {
            imageUri = data.getData();
            // Kép feltöltése a Storage-ba
            uploadImageToFirebase(imageUri);



        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("profileImages/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Frissítsd a felhasználó profilját az új kép URL-jével
                        updateUserProfile(imageUrl);

                        loadProfilePicture(imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Képfeltöltés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserProfile(String imageUrl) {
        userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        userRef.update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profilkép frissítve", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Profil frissítése sikertelen", Toast.LENGTH_SHORT).show());
    }

    private void loadProfilePicture(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference profileImageRef = storage.getReferenceFromUrl(imageUrl);
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                if (!isFinishing() && !isDestroyed()) { // Ellenőrizzük, hogy az aktivitás még nem zárult-e be
                    Glide.with(ProfileActivity.this)
                            .load(uri.toString())
                            .skipMemoryCache(true)  // Memória cache kihagyása
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transition(DrawableTransitionOptions.withCrossFade(100))
                            .into(profPicture);
                }
            }).addOnFailureListener(e -> {
                Log.d("ProfileActivity", "Failed to load image: " + e.getMessage());
            });
        }
    }

    public void profPicChange(View view) {
        checkUserPermission();
    }


    private void loadUserChallenge() {
        mUserChallenges.clear();

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                List<String> mUserChallengesId = user.getMyChallangesId();
                for (String challengeId : mUserChallengesId) {
                    mFirestoreDb.collection("challenges").document(challengeId).get().addOnSuccessListener(tripSnapshot -> {
                        if (tripSnapshot.exists()) {
                            Challenge Challenge = tripSnapshot.toObject(Challenge.class);
                            mUserChallenges.add(Challenge);
                            challengeAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void queryUserProfile() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                fullName.setText(user.getFullName());
                phone.setText(user.getPhoneNumber());
                email.setText(user.getEmail());

                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    loadProfilePicture(user.getProfileImageUrl());
                } else {
                    Log.d("ProfileActivity", "No valid image URL.");
                }




            }
        });
    }

    public void goMainPage(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

}