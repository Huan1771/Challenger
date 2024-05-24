package com.challenger.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.challenger.app.Activity.ChallengeDescriptionActivity;
import com.challenger.app.Activity.MainActivity;
import com.challenger.app.Activity.ProfileActivity;
import com.challenger.app.Model.Challenge;
import com.challenger.app.R;

import java.util.ArrayList;

public class ChallengeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Challenge> mChallengeData;
    // private ArrayList<Trip> mTripItemDataAll = new ArrayList<>();  // ez a szures miatt kell majd
    private Context mContext;
    private boolean showEmptyState = false;



    public ChallengeAdapter(Context mContext, ArrayList<Challenge> mChallengeData, boolean showEmptyState) {
        this.mChallengeData = mChallengeData;
        this.mContext = mContext;
        this.showEmptyState = showEmptyState;
    }

    public ChallengeAdapter(Context mContext, ArrayList<Challenge> mChallengeData) {
        this.mChallengeData = mChallengeData;
        // this.mTripItemDataAll = itemsData;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.challenge_first, parent, false);
            return new ViewHolder(inflate);
        }else {
            View emptyView = LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(emptyView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Challenge challenge = mChallengeData.get(position);
            if (challenge.getId() != null) {
                viewHolder.itemView.setOnClickListener(v -> onChallengeClick(challenge));
            } else {
                Log.e("AdapterError", "Trip ID is null for position " + position);
            }
            viewHolder.nameTxt.setText(challenge.getName());
            viewHolder.startDateTxt.setText(challenge.getStartDate());
            viewHolder.endDateTxt.setText(challenge.getEndDate());
            Glide.with(mContext)
                    .load(challenge.getImageResource())
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .into(viewHolder.itemImage);

            viewHolder.itemView.setOnClickListener(v -> onChallengeClick(challenge));
        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.itemView.setOnClickListener(v -> onEmptyViewClick());
        }

    }
    public void onChallengeClick(Challenge challenge) {
        if (challenge != null && challenge.getId() != null) {
            Intent intent = new Intent(mContext, ChallengeDescriptionActivity.class);
            intent.putExtra("challenge_id", challenge.getId());
            if (mContext instanceof MainActivity) {
                intent.putExtra("origin", "MainActivity");
            } else if (mContext instanceof ProfileActivity) {
                intent.putExtra("origin", "ProfileActivity");
            }
            mContext.startActivity(intent);
        } else {
            // Itt kezeljük le, ha a trip vagy a trip ID null
            Toast.makeText(mContext, "Hiba történt a challenge betöltésekor", Toast.LENGTH_LONG).show();
        }
    }

    private void onEmptyViewClick() {
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mChallengeData.size() > 0 ? mChallengeData.size() : (showEmptyState ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return mChallengeData.size() > 0 ? 0 : 1;  // Ha nincs adat, akkor az üres nézet
    }




    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt,startDateTxt,endDateTxt,priceTxt;
        private ImageView itemImage;

        public ViewHolder(View itemView){
            super(itemView);
            nameTxt = itemView.findViewById(R.id.tripNameText);
            startDateTxt = itemView.findViewById(R.id.startDateText);
            endDateTxt = itemView.findViewById(R.id.finishDateText);
            // priceTxt = itemView.findViewById(R.id.tripPriceTextNumber);
            itemImage = itemView.findViewById(R.id.tripImage);

        }

    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }















}
