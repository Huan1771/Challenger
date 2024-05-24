package com.challenger.app.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.challenger.app.Adapter.ChallengeAdapter;
import com.challenger.app.Model.Challenge;
import com.challenger.app.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.naturenavi.app.adapter.TripItemAdapter;

import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mMirebaseAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView mLegkorabbiRecyclerView;
    private ArrayList<Challenge> ChallangeList;
    private ArrayList<Challenge> legkorabbiChallangeList;
    private ChallengeAdapter mChallengeAdapter;
    private ChallengeAdapter mLegkorabbiChallengeAdapter;
    FirebaseFirestore mFirestore;
    private CollectionReference mChallenge;

    private TextView legnepszerubbText,legkorabbanText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        legnepszerubbText = findViewById(R.id.textView5);
        legnepszerubbText.setText(Html.fromHtml("<u>Legnépszerűbb kirándulásaink:</u>"));


        legkorabbanText = findViewById(R.id.textView6);
        legkorabbanText.setText(Html.fromHtml("<u>Legkorábban induló kihívásaink:</u>"));



        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mMirebaseAuth = FirebaseAuth.getInstance();

        if(mUser == null){
           System.out.println("Nincs hitelesítve a user");
            finish();
        }else {
            System.out.println("Sikeres bejelentkezés");
        }

        mRecyclerView = findViewById(R.id.tripProfileRecyclerView);
        mLegkorabbiRecyclerView = findViewById(R.id.legkorabbiRecycleView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mLegkorabbiRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ChallangeList = new ArrayList<>();
        legkorabbiChallangeList = new ArrayList<>();

        mChallengeAdapter = new ChallengeAdapter(this,ChallangeList);
        mLegkorabbiChallengeAdapter = new ChallengeAdapter(this, legkorabbiChallangeList);

       mRecyclerView.setAdapter(mChallengeAdapter);
       mLegkorabbiRecyclerView.setAdapter(mLegkorabbiChallengeAdapter);


        mFirestore= FirebaseFirestore.getInstance();
        mChallenge = mFirestore.collection("challenges");

        queryChallenge();
        queryEarliestTrips();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void queryChallenge() {
        ChallangeList.clear();

        mChallenge.orderBy("participant", Query.Direction.DESCENDING).limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Challenge challenge = document.toObject(Challenge.class);
                challenge.setId(document.getId());
                ChallangeList.add(challenge);
            }

            if (ChallangeList.size() == 0) {
                initializeChallengeData();
                queryChallenge();
            }
            mChallengeAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryEarliestTrips() {
        legkorabbiChallangeList.clear();

        mChallenge.orderBy("startDate", Query.Direction.ASCENDING).limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Challenge challenge = document.toObject(Challenge.class);
                challenge.setId(document.getId());
                legkorabbiChallangeList.add(challenge);
            }
            if (legkorabbiChallangeList.size() == 0) {
                initializeChallengeData();
                queryEarliestTrips();
            }

            mLegkorabbiChallengeAdapter.notifyDataSetChanged();
        });
    }


    private void initializeChallengeData() {
        String[] challengeName = getResources().getStringArray(R.array.challenge_names);
        String[] challengePont = getResources().getStringArray(R.array.challenge_megszerezheto_pont);
        String[] challengeStartDate = getResources().getStringArray(R.array.challenge_start_date);
        String[] challengeEndDate = getResources().getStringArray(R.array.challenge_end_date);
        String[] challengeDescriptionText = getResources().getStringArray(R.array.challenge_description_text);
        TypedArray challengeImageResources = getResources().obtainTypedArray(R.array.challenge_images);
        String[] challengCategory = getResources().getStringArray(R.array.challenge_category);
        AtomicInteger transactionCount = new AtomicInteger(challengeName.length);

        for (int i = 0; i < challengeName.length; i++) {
            final int index = i;
            DocumentReference newChallengeRef = mChallenge.document(challengeName[i]);
            FirebaseFirestore.getInstance().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(newChallengeRef);
                if (!snapshot.exists()) {
                    Challenge challenge = new Challenge(challengeStartDate[index], challengeEndDate[index], challengeDescriptionText[index], challengeName[index],
                            challengeImageResources.getResourceId(index, 0), challengePont[index] ,challengCategory[index],0);
                    challenge.setId(newChallengeRef.getId());
                    transaction.set(newChallengeRef, challenge);
                    return null;
                }
                return null;
            }).addOnSuccessListener(aVoid -> {
                if (transactionCount.decrementAndGet() == 0) {
                    challengeImageResources.recycle();
                }
            }).addOnFailureListener(e -> {
                Log.e("Transaction", "Transaction failure.", e);
                if (transactionCount.decrementAndGet() == 0) {
                    challengeImageResources.recycle();
                }
            });
        }
    }



    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    public void goProfilePage(View view) {
        startActivity(new Intent(this,ProfileActivity.class));
    }



    public void deleteTrip(View view) {
        mMirebaseAuth = FirebaseAuth.getInstance();
        mUser = mMirebaseAuth.getCurrentUser();
        if (mUser != null) {

            String challengeId = getIntent().getStringExtra("challenge_id");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference tripRef = db.collection("trips").document(challengeId);

            tripRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TripDescriptionActivity", "Trip successfully deleted!");
                        Toast.makeText(this, "Trip deleted successfully!", Toast.LENGTH_SHORT).show();
                        showSuccessDialog();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TripDescriptionActivity", "Error deleting trip", e);
                        Toast.makeText(this, "Failed to delete trip", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("TripDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to delete trips", Toast.LENGTH_SHORT).show();
        }


    }



    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Törlés megerősítése");
        builder.setMessage("Gratulálok, sikeres törlést hajtottál végre!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();  // Dialog bezárása az OK gombra kattintva
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


















}