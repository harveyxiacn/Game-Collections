package com.itgarage.harvey.gamecollections.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.models.Game;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    List<Game> gamesList;
    String mediumImage;
    Bitmap bitmap;
    GameListViewHolder holder;

    public GameListAdapter(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public int getItemCount() {
        if(gamesList != null)
            return gamesList.size();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(GameListViewHolder holder, int position) {
        Game game = gamesList.get(position);
        this.holder = holder;
        holder.titleTextView.setText(game.getTitle());

        String platform = game.getPlatform();
        if (!platform.equals("")) {
            holder.platformTextView.setText(platform);
        }

        mediumImage = game.getMediumImage();
        new ImageDownloader().execute(mediumImage);

        String genre = game.getGenre();
        if(!genre.equals("")){
            holder.genreTextView.setText("Genre: "+genre);
            holder.genreTextView.setVisibility(View.VISIBLE);
        }else {
            //holder.gameAttributesLayout.removeView(holder.genreTextView);
            holder.genreTextView.setVisibility(View.GONE);
        }

        String hardwarePlatform = game.getHardwarePlatform();
        if(!hardwarePlatform.equals("")){
            holder.hardwarePlatformTextView.setText("Hardware Platform: "+hardwarePlatform);
            holder.hardwarePlatformTextView.setVisibility(View.VISIBLE);
        }else {
            //holder.gameAttributesLayout.removeView(holder.hardwarePlatformTextView);
            holder.hardwarePlatformTextView.setVisibility(View.GONE);
        }

        String edition = game.getEdition();
        if(!edition.equals("")){
            holder.editionTextView.setText("Edition: "+edition);
            holder.editionTextView.setVisibility(View.VISIBLE);
        }else {
            holder.editionTextView.setVisibility(View.GONE);
        }

        String manufacturer = game.getManufacturer();
        if(!manufacturer.equals("")){
            holder.manufacturerTextView.setText("Manufacturer: "+manufacturer);
            holder.manufacturerTextView.setVisibility(View.VISIBLE);
        }else {
            holder.manufacturerTextView.setVisibility(View.GONE);
        }

        String publicationDate = game.getPublicationDate();
        if(!publicationDate.equals("")){
            holder.publicationDateTextView.setText("Publication Date: "+publicationDate);
            holder.publicationDateTextView.setVisibility(View.VISIBLE);
        }else {
            holder.publicationDateTextView.setVisibility(View.GONE);
        }

        String releaseDate = game.getReleaseDate();
        if(!releaseDate.equals("")){
            holder.releaseDateTextView.setText("Release Date: "+releaseDate);
            holder.releaseDateTextView.setVisibility(View.VISIBLE);
        }else {
            holder.releaseDateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public GameListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cardview_games_list_row, parent, false);
        return new GameListViewHolder(view);
    }

    public static class GameListViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView titleTextView, platformTextView;
        protected LinearLayout gameAttributesLayout;
        protected TextView genreTextView, hardwarePlatformTextView, manufacturerTextView,
                editionTextView, publicationDateTextView, releaseDateTextView;

        public GameListViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageViewGameImage);
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
        }
    }

    class ImageDownloader extends AsyncTask<String, String, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... args) {

            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
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
            if(image!=null){
                holder.imageView.setImageBitmap(image);
            }
        }
    }
}
