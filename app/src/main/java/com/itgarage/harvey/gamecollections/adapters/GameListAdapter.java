package com.itgarage.harvey.gamecollections.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.BarcodeResultActivity;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import me.xiaopan.android.spear.DisplayOptions;
import me.xiaopan.android.spear.SpearImageView;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    List<Game> gamesList;
    String mediumImage;
    Bitmap bitmap;
    GameListViewHolder holder;
    Activity activity;
    GameListAdapter adapter;

    public GameListAdapter(List<Game> gamesList, Activity activity) {
        this.gamesList = gamesList;
        this.activity = activity;
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
        if (!platform.equals("")) {
            holder.platformTextView.setText(platform);
        }

        mediumImage = game.getMediumImage();
        //new ImageDownloader().execute(mediumImage);
        holder.imageView.setImageFromUri(mediumImage);

        String genre = game.getGenre();
        if (!genre.equals("")) {
            holder.genreTextView.setText("Genre: " + genre);
            holder.genreTextView.setVisibility(View.VISIBLE);
        } else {
            //holder.gameAttributesLayout.removeView(holder.genreTextView);
            holder.genreTextView.setVisibility(View.GONE);
        }

        String hardwarePlatform = game.getHardwarePlatform();
        if (!hardwarePlatform.equals("")) {
            holder.hardwarePlatformTextView.setText("Hardware Platform: " + hardwarePlatform);
            holder.hardwarePlatformTextView.setVisibility(View.VISIBLE);
        } else {
            //holder.gameAttributesLayout.removeView(holder.hardwarePlatformTextView);
            holder.hardwarePlatformTextView.setVisibility(View.GONE);
        }

        String edition = game.getEdition();
        if (!edition.equals("")) {
            holder.editionTextView.setText("Edition: " + edition);
            holder.editionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.editionTextView.setVisibility(View.GONE);
        }

        String manufacturer = game.getManufacturer();
        if (!manufacturer.equals("")) {
            holder.manufacturerTextView.setText("Manufacturer: " + manufacturer);
            holder.manufacturerTextView.setVisibility(View.VISIBLE);
        } else {
            holder.manufacturerTextView.setVisibility(View.GONE);
        }

        String publicationDate = game.getPublicationDate();
        if (!publicationDate.equals("")) {
            holder.publicationDateTextView.setText("Publication Date: " + publicationDate);
            holder.publicationDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.publicationDateTextView.setVisibility(View.GONE);
        }

        String releaseDate = game.getReleaseDate();
        if (!releaseDate.equals("")) {
            holder.releaseDateTextView.setText("Release Date: " + releaseDate);
            holder.releaseDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.releaseDateTextView.setVisibility(View.GONE);
        }

        int rating = game.getRating();
        if (rating != -1) {
            holder.gameRating.setRating((float)rating);
            holder.gameRatingSmall.setRating((float)rating);
        } else {
            holder.gameRatingLayout.setVisibility(View.GONE);
            holder.gameRatingSmall.setVisibility(View.GONE);
        }

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
        protected LinearLayout gameAttributesLayout, gameRatingLayout, borrowerInfoLayout;
        protected TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
                editionTextView, publicationDateTextView, releaseDateTextView;
        protected RatingBar gameRating, gameRatingSmall;
        Activity activity;
        private static final String TAG_ADD_TO_DB = "TAG_ADD_TO_DB";
        private static final String TAG_ADD_BORROWER = "TAG_ADD_BORROWER";
        private static final String TAG_ADD_RATING = "TAG_ADD_RATING";
        List<Game> gamesList;
        FloatingActionMenu actionMenuGameDetail;
        TextView ratingTextView;

        public GameListViewHolder(View view, Activity activity) {
            super(view);

            this.activity = activity;
            imageView = (SpearImageView) view.findViewById(R.id.imageViewGameImage);
            DisplayOptions displayOptions = new DisplayOptions(activity);
            displayOptions.loadFailDrawable(R.drawable.no_image_medium);
            imageView.setDisplayOptions(displayOptions);

            titleTextView = (TextView) view.findViewById(R.id.textViewGameTitle);
            platformTextView = (TextView) view.findViewById(R.id.textViewGamePlatform);

            gameAttributesLayout = (LinearLayout) view.findViewById(R.id.gameAttributesLayout);

            genreTextView = new TextView(view.getContext());
            genreTextView.setTextSize(20);
            gameAttributesLayout.addView(genreTextView);

            hardwarePlatformTextView = new TextView(view.getContext());
            hardwarePlatformTextView.setTextSize(20);
            gameAttributesLayout.addView(hardwarePlatformTextView);

            editionTextView = new TextView(view.getContext());
            editionTextView.setTextSize(20);
            gameAttributesLayout.addView(editionTextView);

            manufacturerTextView = new TextView(view.getContext());
            manufacturerTextView.setTextSize(20);
            gameAttributesLayout.addView(manufacturerTextView);

            publicationDateTextView = new TextView(view.getContext());
            publicationDateTextView.setTextSize(20);
            gameAttributesLayout.addView(publicationDateTextView);

            releaseDateTextView = new TextView(view.getContext());
            releaseDateTextView.setTextSize(20);
            gameAttributesLayout.addView(releaseDateTextView);

            gameRatingLayout = (LinearLayout) view.findViewById(R.id.gameRatingLayout);
            ratingTextView = (TextView) view.findViewById(R.id.gameRatingText);

            gameRating = (RatingBar) view.findViewById(R.id.gameRatingBar);
            gameRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingTextView.setText("Rating: "+String.valueOf((int)ratingBar.getRating()));
                }
            });

            borrowerInfoLayout = (LinearLayout) view.findViewById(R.id.gameBorrowerInfoLayout);

            gameRatingSmall = (RatingBar) view.findViewById(R.id.gameRatingBarSmall);

            if (activity.getClass() == BarcodeResultActivity.class) {
                createGameDetailFloatingActionButtons();
                setVisibilities(true);
            }
            if (activity.getClass() == NaviDrawerActivity.class){
                setVisibilities(false);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getTag().equals(TAG_ADD_TO_DB)) {
                Toast.makeText(activity, "Add to DB", Toast.LENGTH_SHORT).show();
                Game game = gamesList.get(0);
                if (gameRatingLayout.getVisibility() == View.VISIBLE) {
                    RatingBar ratingBar = (RatingBar) activity.findViewById(R.id.gameRatingBar);
                    game.setRating((int) ratingBar.getRating());
                }
                GamesDataSource dataSource = new GamesDataSource(activity);
                dataSource.open();
                Log.i("DB operation", "DB opened.");
                long insertId = dataSource.addGame(game);
                if(insertId != -1){
                    Toast.makeText(activity, "Successfully Add to DB", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
                actionMenuGameDetail.close(true);
            }
            if (v.getTag().equals(TAG_ADD_BORROWER)) {
                Toast.makeText(activity, "Add Borrower", Toast.LENGTH_SHORT).show();
                actionMenuGameDetail.close(true);
            }

            if (v.getTag().equals(TAG_ADD_RATING)) {
                if(gameRatingLayout.getVisibility() == View.GONE) {
                    Toast.makeText(activity, "Add Rating", Toast.LENGTH_SHORT).show();
                    gameRatingLayout.setVisibility(View.VISIBLE);
                }else if(gameRatingLayout.getVisibility() == View.VISIBLE){
                    Toast.makeText(activity, "Remove Rating", Toast.LENGTH_SHORT).show();
                    gameRatingLayout.setVisibility(View.GONE);
                }
                actionMenuGameDetail.close(true);
            }

        }

        public void createGameDetailFloatingActionButtons() {
            ImageView floatingActionButtonIcon = new ImageView(activity);
            floatingActionButtonIcon.setImageResource(R.drawable.ic_action_game);
            // Create a button to attach the menu:
            FloatingActionButton actionButton = new FloatingActionButton.Builder(activity)
                    .setContentView(floatingActionButtonIcon)
                    .setBackgroundDrawable(R.drawable.selector_button_cyan)
                    .build();
            // Create menu items:
            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(activity);
            itemBuilder.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_button_cyan));
            // repeat many times:
            ImageView itemAddToDBIcon = new ImageView(activity);
            itemAddToDBIcon.setImageResource(R.drawable.ic_add_to_db);
            SubActionButton addToDBButton = itemBuilder.setContentView(itemAddToDBIcon).build();
            addToDBButton.setOnClickListener(this);
            addToDBButton.setTag(TAG_ADD_TO_DB);

            ImageView itemAddContactIcon = new ImageView(activity);
            itemAddContactIcon.setImageResource(R.drawable.ic_add_borrower);
            SubActionButton addContactButton = itemBuilder.setContentView(itemAddContactIcon).build();
            addContactButton.setOnClickListener(this);
            addContactButton.setTag(TAG_ADD_BORROWER);

            ImageView itemAddRatingIcon = new ImageView(activity);
            itemAddRatingIcon.setImageResource(R.drawable.ic_add_rating);
            SubActionButton addRatingButton = itemBuilder.setContentView(itemAddRatingIcon).build();
            addRatingButton.setOnClickListener(this);
            addRatingButton.setTag(TAG_ADD_RATING);
            // Create the menu with the items:
            actionMenuGameDetail = new FloatingActionMenu.Builder(activity)
                    .addSubActionView(addToDBButton)
                    .addSubActionView(addContactButton)
                    .addSubActionView(addRatingButton)
                    .attachTo(actionButton)
                    .build();
        }

        public void setVisibilities(boolean isVisible){
            if(isVisible) {
                gameAttributesLayout.setVisibility(View.VISIBLE);
                //gameRatingLayout.setVisibility(View.VISIBLE);
                //borrowerInfoLayout.setVisibility(View.VISIBLE);
                gameRatingSmall.setVisibility(View.GONE);
            }else {
                gameAttributesLayout.setVisibility(View.GONE);
                gameRatingLayout.setVisibility(View.GONE);
                borrowerInfoLayout.setVisibility(View.GONE);
                gameRatingSmall.setVisibility(View.VISIBLE);
            }
        }
    }

    class ImageDownloader extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... args) {

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                holder.imageView.setImageBitmap(image);
            }
        }
    }
}
