package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.GameDetailActivity;
import com.itgarage.harvey.gamecollections.activities.OnlineGameResultActivity;
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
            //holder.platformTextView.setText(platform);
            //holder.platformTextView.setVisibility(View.GONE);
            switch (platform.toLowerCase()){
                case "neogeo pocket":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0000_neo_geo_pocket);
                    break;
                case "sega master system":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0001_sega_master_system);
                    break;
                case "sega game gear":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0002_sega_game_gear);
                    break;
                case "sega genesis":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0006_sega_genesis);
                    break;
                case "sega dreamcast":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0007_dream_cast);
                    break;
                case "xbox one":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0009_xbox_one);
                    break;
                case "xbox 360":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0010_xbox_360);
                    break;
                case "xbox":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0011_xbox);
                    break;
                case "playstation vita":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0012_ps_vita);
                    break;
                case "sony psp":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0013_psp);
                    break;
                case "playstation":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0017_playstation);
                    break;
                case "playstation 2":
                case "playstation2":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0016_playstation2);
                    break;
                case "playstation 3":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0015_playstation3);
                    break;
                case "playstation 4":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0014_playstation4);
                    break;
                case "game boy":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0018_gameboy);
                    break;
                case "game boy advance":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0019_gba);
                    break;
                case "nintendo wii u":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0020_wiiu);
                    break;
                case "nintendo wii":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0021_wii);
                    break;
                case "gamecube":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0022_gamecube);
                    break;
                case "nintendo 3ds":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0023_3ds);
                    break;
                case "nintendo ds":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0024_ds);
                    break;
                case "nintendo super nes":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0025_snes);
                    break;
                case "nintendo nes":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0026_nes);
                    break;
                case "nintendo 64":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0027_n64);
                    break;
                case "game boy color":
                    holder.imageViewPlatform.setImageResource(R.drawable.systemlogos_0023_gbc);
                    break;
                default:
                    holder.imageViewPlatform.setVisibility(View.GONE);
                    holder.platformTextView.setVisibility(View.VISIBLE);
                    holder.platformTextView.setText(platform);
            }
        }else {
            holder.imageViewPlatform.setVisibility(View.GONE);
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
        ImageView imageViewPlatform;

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
            imageViewPlatform = (ImageView) view.findViewById(R.id.imageViewGamePlatform);
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
