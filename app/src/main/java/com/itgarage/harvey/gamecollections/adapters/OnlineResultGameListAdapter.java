package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.OnlineGameResultActivity;
import com.itgarage.harvey.gamecollections.activities.GameDetailActivity;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

import me.xiaopan.android.spear.DisplayOptions;
import me.xiaopan.android.spear.SpearImageView;

public class OnlineResultGameListAdapter extends RecyclerView.Adapter<OnlineResultGameListAdapter.GameListViewHolder> {

    List<Game> gamesList;
    String mediumImage;
    GameListViewHolder holder;
    Activity activity;
    OnlineResultGameListAdapter adapter;

    public OnlineResultGameListAdapter(List<Game> gamesList, Activity activity) {
        this.gamesList = gamesList;
        this.activity = activity;
    }

    public void update(List<Game> gamesList){
        this.gamesList = gamesList;
        notifyDataSetChanged();
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
        holder.titleTextView.setText(game.getTitle());

        String platform = game.getPlatform();
        if(!platform.equals("")){
            holder.platformTextView.setText(platform);
        }

        mediumImage = game.getMediumImage();
        holder.imageView.setImageFromUri(mediumImage);
        holder.game = game;
    }

    @Override
    public GameListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cardview_games_keyword_result_list_row, parent, false);
        return new GameListViewHolder(view, activity);
    }

    public static class GameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected SpearImageView imageView;
        protected TextView titleTextView, platformTextView;
        Activity activity;
        static final String TAG_VIEW = "TAG_VIEW";
        Game game;

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
        }

        @Override
        public void onClick(View v) {
            if(v.getTag().equals(TAG_VIEW)){
                GamesDataSource dataSource = new GamesDataSource(activity);
                dataSource.open();
                Game gameFromDB = dataSource.getGameByUPC(game.getUpcCode());
                if(gameFromDB==null) {
                    Log.i("adapter", "not existed game");
                    Intent intent = new Intent(activity, OnlineGameResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("operation", "Keyword Search");
                    bundle.putString("title", game.getTitle());
                    bundle.putString("platform", game.getPlatform());
                    bundle.putString("hardPlatform", game.getHardwarePlatform());
                    bundle.putString("genre", game.getGenre());
                    bundle.putString("mediumImage", game.getMediumImage());
                    bundle.putString("edition", game.getEdition());
                    bundle.putString("manufacturer", game.getManufacturer());
                    bundle.putString("publicationDate", game.getPublicationDate());
                    bundle.putString("releaseDate", game.getReleaseDate());
                    bundle.putInt("rating", game.getRating());
                    bundle.putString("smallImage", game.getSmallImage());
                    bundle.putString("largeImage", game.getLargeImage());
                    bundle.putString("upc", game.getUpcCode());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }else {
                    Intent intent = new Intent(activity, GameDetailActivity.class);
                    intent.putExtra("pass barcode to game detail", game.getUpcCode());
                    activity.startActivity(intent);
                }
            }
        }
    }
}
