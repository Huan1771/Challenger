package com.challenger.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.challenger.app.Model.Challenge;
import com.challenger.app.Model.User;
import com.challenger.app.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChallengeDescriptionActivity extends AppCompatActivity {

    private TextView nameTxt, descriptionTxt, priceTxt,indulasText,erkezesText,megszerezhetoText;
    private Button cancelButton,bookButton;
    private ImageView tripImg;
    private FirebaseFirestore mFirestoreDb;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challange_description);

        initializeFirebase();
        initializeViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser(); // Biztosítjuk, hogy a currentUser friss legyen
        }
        String challengeId = getIntent().getStringExtra("challenge_id");
        loadTripDetails(challengeId);

    }



    private void initializeFirebase() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();
        mFirestoreDb = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initializeViews() {
        nameTxt = findViewById(R.id.TripName);
        descriptionTxt = findViewById(R.id.description);
        priceTxt = findViewById(R.id.TripePrice);
        tripImg = findViewById(R.id.descriptionImage);
        bookButton = findViewById(R.id.lefoglalButton);
        cancelButton = findViewById(R.id.lemondbutton);
        indulasText = findViewById(R.id.indulasText);
        erkezesText = findViewById(R.id.erkezesText);
        megszerezhetoText = findViewById(R.id.megszerezhetoPont);
    }

    private void loadTripDetails(String challengeId) {
        Log.d("ChallengeDescriptionActivity", "Betöltés megkezdése, challange ID: " + challengeId);
        mFirestoreDb.collection("challenges").document(challengeId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Challenge challenge = documentSnapshot.toObject(Challenge.class);
                if (challenge != null) {
                    nameTxt.setText(challenge.getName());
                    descriptionTxt.setText(challenge.getDescription());
                    priceTxt.setText(String.valueOf(challenge.getParticipant()));
                    indulasText.setText(challenge.getStartDate());
                    erkezesText.setText(challenge.getEndDate());
                    megszerezhetoText.setText(challenge.getMegszerezhetoPont());
                    if (challenge.getImageResource() != 0) {
                        Glide.with(this)
                                .load(challenge.getImageResource())
                                .into(tripImg);


                    } else {
                        Log.d("ChallengeDescriptionActivity", "Nincs érvényes kép azonosító.");
                    }

                } else {
                    Log.e("ChallengeDescriptionActivity", "Challenge objektum null.");
                }
            } else {
                Log.e("ChallengeDescriptionActivity", "Az Challenge nem található.");
            }
        }).addOnFailureListener(e -> {
            Log.e("ChallengeDescriptionActivity", "Hiba történt a Challenge adatainak lekérésekor: " + e.getMessage(), e);
        });

        checkIfChallengeBooked(challengeId);
    }


    public void toBook(View view) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();


            String challengeId = getIntent().getStringExtra("challenge_id");

            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.update("myChallangesId", FieldValue.arrayUnion(challengeId))
                    .addOnSuccessListener(aVoid -> {

                        Log.d("ChallengeDescriptionActivity", "challenge succes!");
                        Toast.makeText(this, "challenge successfully!", Toast.LENGTH_SHORT).show();
                        disableBookingButton();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ChallengeDescriptionActivity", "Error challenge", e);
                        Toast.makeText(this, "Failed challenge", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("ChallengeDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to book challenge", Toast.LENGTH_SHORT).show();
        }
    }

    public void toCancel(View view) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String tripId = getIntent().getStringExtra("challenge_id");

            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.update("myChallangesId", FieldValue.arrayRemove(tripId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ChallengeDescriptionActivity", "challenge successfully !");
                        Toast.makeText(this, "challenge successfully!", Toast.LENGTH_SHORT).show();


                       /*
                        challengeRef.update("participant", FieldValue.increment(1))
                                .addOnSuccessListener(aVoidParticipant -> {
                                    Log.d("ChallengeDescriptionActivity", "Participant count incremented!");
                                    loadTripDetails(challengeId);  // Frissíti a kihívás részleteit és a felületet
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ChallengeDescriptionActivity", "Failed to increment participant count", e);
                                });


                        enableBookingButton();



                        */


                    })
                    .addOnFailureListener(e -> {
                        Log.e("ChallengeDescriptionActivity", "Error g challenge", e);
                        Toast.makeText(this, "Failed  challenge", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("ChallengeDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to cancel challenge", Toast.LENGTH_SHORT).show();
        }
    }


    private void disableBookingButton() {
        bookButton.setEnabled(false);
        bookButton.setBackgroundColor(Color.GRAY);
        cancelButton.setEnabled(true);
        cancelButton.setBackgroundColor(Color.RED);
    }
    private void enableBookingButton() {
        bookButton.setEnabled(true);
        bookButton.setBackgroundColor(getColor(R.color.my_primary));
        cancelButton.setEnabled(false);
        cancelButton.setBackgroundColor(Color.GRAY);
    }

    private void checkIfChallengeBooked(String challangeId) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getMyChallangesId() != null ) {
                        if(user.getMyChallangesId().contains(challangeId)){
                            disableBookingButton();
                        }else {
                            enableBookingButton();
                        }

                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("TripDescriptionActivity", "Error retrieving user info: " + e.getMessage(), e);
            });
        }
    }


    public void goBack(View view) {
        String origin = getIntent().getStringExtra("origin");
        Intent intent;
        if ("ProfileActivity".equals(origin)) {
            intent = new Intent(this, ProfileActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
