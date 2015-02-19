package com.itgarage.harvey.gamecollections.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    private List<Game> gamesList;

    public GameListAdapter(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    @Override
    public void onBindViewHolder(GameListViewHolder holder, int position) {
        Game game = gamesList.get(position);
        holder.titleTextView.setText(game.getTitle());
        String platform = game.getPlatform();
        if (platform != null) {
            holder.platformTextView.setText(platform);
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

        public GameListViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageViewGameImage);
            titleTextView = (TextView) view.findViewById(R.id.textViewGameTitle);
            platformTextView = (TextView) view.findViewById(R.id.textViewGamePlatform);
        }
    }
}
