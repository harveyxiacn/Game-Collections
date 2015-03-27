package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.GameDetailActivity;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

import me.xiaopan.android.spear.DisplayOptions;
import me.xiaopan.android.spear.SpearImageView;

/**
 * This recycler view adapter is used to fill game list for all/favourite/lend/wish
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder>{

    List<Game> gamesList;
    String mediumImage;
    GameListViewHolder holder;
    Activity activity;
    GameListAdapter adapter;
    boolean isGridLayout;

    /**
     * Constructor for GameListAdapter.
     * @param gamesList Games list that use for filling.
     * @param activity Current activity.
     * @param isGridLayout Current layout is Grid or List.
     */
    public GameListAdapter(List<Game> gamesList, Activity activity, boolean isGridLayout) {
        this.gamesList = gamesList;
        this.activity = activity;
        this.isGridLayout = isGridLayout;
    }

    /**
     * Update games list.
     * @param gamesList New list.
     */
    public void updateList(List<Game> gamesList) {
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }

    /**
     * Change layout.
     * @param isGridLayout Indicates is grid or list layout.
     */
    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        updateVisibilityByLayoutChange(isGridLayout);
    }

    /**
     * Update UIs when the layout switch between grid and list.
     * @param isGridLayout Indicates is grid or list layout.
     */
    public void updateVisibilityByLayoutChange(boolean isGridLayout){
        if(isGridLayout){
            holder.titleTextView.setVisibility(View.GONE);
            holder.platformTextView.setVisibility(View.GONE);
            //holder.imageViewPlatform.setVisibility(View.GONE);
            holder.gameRatingSmall.setVisibility(View.GONE);
        }else {
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.platformTextView.setVisibility(View.VISIBLE);
            //holder.imageViewPlatform.setVisibility(View.VISIBLE);
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
        holder.gameIdTv.setText(String.valueOf(game.getId()));
        // bind title text view with the game title
        holder.titleTextView.setText(game.getTitle());
        // bind platform text view with the game platform
        String platform = game.getPlatform();
        if (platform != null) {
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
            holder.imageViewPlatform.setVisibility(View.GONE);
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
        holder.favourite.setChecked(game.getFavourite()==1);
        holder.wish.setChecked(game.getWish()==1);
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

    /**
     * This view holder holds every games attributes.
     */
    public static class GameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "GameListViewHolder";
        protected SpearImageView imageView;
        ImageView imageViewPlatform;
        protected TextView titleTextView, platformTextView;
        protected RatingBar gameRatingSmall;
        TextView gameIdTv;
        Activity activity;
        public static final String GAME_ID_EXTRA = "game id";
        static final String TAG_VIEW = "TAG_VIEW";
        static final String TAG_RATING = "TAG_RATING";
        static final String TAG_WISH = "TAG_WISH";
        static final String TAG_FAVOURITE = "TAG_FAVOURITE";
        List<Game> gamesList;
        CheckBox favourite, wish;

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
            gameRatingSmall = (RatingBar) view.findViewById(R.id.gameRatingBarSmall);
            gameRatingSmall.setVisibility(View.VISIBLE);
            /*gameRatingSmall.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    GamesDataSource dataSource = new GamesDataSource(activity);
                    dataSource.open();
                    Game game = dataSource.getGame(Integer.parseInt(gameIdTv.getText().toString()));
                    Log.i(TAG, "update rating");
                    game.setRating((int)gameRatingSmall.getRating());
                    dataSource.updateGame(game);
                    if(NetworkStatus.isNetworkAvailable(activity)) {
                        CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(activity);
                        cognitoSyncGames.updateGame(game);
                        Log.i(TAG, "update rating cog");
                    }
                }
            });*/

            gameIdTv = (TextView) view.findViewById(R.id.gameId);

            favourite = (CheckBox) view.findViewById(R.id.favouriteCheckBox);
            favourite.setTag(TAG_FAVOURITE);
            //favourite.setOnClickListener(this);
            wish = (CheckBox) view.findViewById(R.id.wishCheckBox);
            wish.setTag(TAG_WISH);
            //wish.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            GamesDataSource dataSource = new GamesDataSource(activity);
            dataSource.open();
            Game game = dataSource.getGame(Integer.parseInt(gameIdTv.getText().toString()));
            Log.i(TAG, game.getTitle());
            /* enter game detail activity by clicking in game list*/
            if (v.getTag().equals(TAG_VIEW)) {
                if (activity.getClass() == NaviDrawerActivity.class) {
                    Intent intent = new Intent(activity, GameDetailActivity.class);
                    intent.putExtra(GAME_ID_EXTRA, gameIdTv.getText());
                    activity.startActivity(intent);
                }
            }
            /*if(v.getTag().equals(TAG_FAVOURITE)){
                if(favourite.isChecked())
                    game.setFavourite(1);
                else
                    game.setFavourite(0);
            }
            if(v.getTag().equals(TAG_WISH)){
                if(wish.isChecked())
                    game.setWish(1);
                else
                    game.setWish(0);

            }
            dataSource.updateGame(game);
            if(NetworkStatus.isNetworkAvailable(activity)) {
                CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(activity);
                cognitoSyncGames.updateGame(game);
            }
            dataSource.close();*/

        }
    }
}
