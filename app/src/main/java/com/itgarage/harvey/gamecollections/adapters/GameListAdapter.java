package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.GameDetailActivity;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

import me.xiaopan.android.spear.DisplayOptions;
import me.xiaopan.android.spear.SpearImageView;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder>{

    List<Game> gamesList, origenalList;
    String mediumImage;
    GameListViewHolder holder;
    Activity activity;
    GameListAdapter adapter;
    boolean isGridLayout;

    public GameListAdapter(List<Game> gamesList, Activity activity, boolean isGridLayout) {
        this.gamesList = gamesList;
        this.activity = activity;
        this.isGridLayout = isGridLayout;
    }

    public void updateList(List<Game> gamesList) {
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        updateVisibilityByLayoutChange(isGridLayout);
    }

    public void updateVisibilityByLayoutChange(boolean isGridLayout){
        if(isGridLayout){
            holder.titleTextView.setVisibility(View.GONE);
            holder.platformTextView.setVisibility(View.GONE);
            holder.gameRatingSmall.setVisibility(View.GONE);
        }else {
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.platformTextView.setVisibility(View.VISIBLE);
            holder.gameRatingSmall.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (gamesList != null)
            return gamesList.size();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(GameListViewHolder holder, int position) {
        Game game = gamesList.get(position);
        this.holder = holder;
        this.adapter = this;
        // bind title text view with the game title
        holder.titleTextView.setText(game.getTitle());
        // bind platform text view with the game platform
        String platform = game.getPlatform();
        if (platform != null) {
            holder.platformTextView.setText(platform);
        }
        // bind rating bar with the game rating
        int rating = game.getRating();
        // display the small rating bar if the rating is not equal -1
        if (rating != -1) {
            holder.gameRatingSmall.setRating((float) rating);
        } else {
            holder.gameRatingSmall.setVisibility(View.GONE);
        }
        // bind image view with the medium image url
        mediumImage = game.getMediumImage();
        holder.imageView.setImageFromUri(mediumImage);
        updateVisibilityByLayoutChange(isGridLayout);
        holder.gamesList = gamesList;
    }

    @Override
    public GameListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cardview_games_list_row, parent, false);
        return new GameListViewHolder(view, activity);
    }

    public static class GameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected SpearImageView imageView;
        protected TextView titleTextView, platformTextView;
        protected RatingBar gameRatingSmall;
        Activity activity;

        static final String TAG_VIEW = "TAG_VIEW";
        List<Game> gamesList;

        public GameListViewHolder(View view, final Activity activity) {
            super(view);

            this.activity = activity;
            imageView = (SpearImageView) view.findViewById(R.id.imageViewGameImage);
            DisplayOptions displayOptions = new DisplayOptions(activity);
            displayOptions.loadFailDrawable(R.drawable.no_image_medium);
            imageView.setDisplayOptions(displayOptions);

            view.setOnClickListener(this);
            view.setTag(TAG_VIEW);

            titleTextView = (TextView) view.findViewById(R.id.textViewGameTitle);
            platformTextView = (TextView) view.findViewById(R.id.textViewGamePlatform);
            gameRatingSmall = (RatingBar) view.findViewById(R.id.gameRatingBarSmall);
            gameRatingSmall.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            /* enter game detail activity by clicking in game list*/
            if (v.getTag().equals(TAG_VIEW)) {
                if (activity.getClass() == NaviDrawerActivity.class) {
                    Intent intent = new Intent(activity, GameDetailActivity.class);
                    intent.putExtra("game position", getPosition());
                    Log.i("position", "" + getPosition());
                    activity.startActivity(intent);
                }
            }

        }
    }
}
