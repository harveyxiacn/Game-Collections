package com.itgarage.harvey.gamecollections.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.GameDetailActivity;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.List;

import me.xiaopan.android.spear.SpearImageView;

/**
 * Created by harvey on 2015-02-23.
 */
public class ImageSlideAdapter extends PagerAdapter implements View.OnClickListener, ViewPager.OnPageChangeListener{
    Context context;
    List<Game> gamesList;
    LayoutInflater mInfalter;
    int[] ids;
    int position;

    public ImageSlideAdapter(Context context, List<Game> gamesList, ViewPager viewPager){
        Log.i("slide", "constructor game list:"+gamesList.toString());
        this.context = context;
        this.gamesList = gamesList;
        ids = new int[]{-1, -1, -1};
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    /**
     * Update list data if the dataSet is changed.
     * @param gamesList The new games list.
     */
    public void updateList(List<Game> gamesList){
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }

    // how many slides will be created
    @Override
    public int getCount() {
        if(gamesList==null)
            return 0;
        return gamesList.size()%3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mInfalter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = mInfalter.inflate(R.layout.container_slider_home, container, false);
        // define first position
        position = position*3;
        // set up first layout
        Log.i("slide", "instantiateItem");
        LinearLayout linearLayout1 = (LinearLayout) myView.findViewById(R.id.firstLinearLayout);
        TextView textView1 = (TextView) myView.findViewById(R.id.gameIdTv1);
        SpearImageView imageView1 = (SpearImageView) myView.findViewById(R.id.imageViewGameImageSlide1);
        if(position<gamesList.size()) {
            Game game = gamesList.get(position);
            Log.i("slide", game.getTitle());
            ids[0] = game.getId();
            textView1.setText(String.valueOf(game.getId()));
            imageView1.setImageFromUri(game.getMediumImage());
            linearLayout1.setOnClickListener(this);
        }
        // set up second layout
        LinearLayout linearLayout2 = (LinearLayout) myView.findViewById(R.id.secondLinearLayout);
        TextView textView2 = (TextView) myView.findViewById(R.id.gameIdTv2);
        SpearImageView imageView2 = (SpearImageView) myView.findViewById(R.id.imageViewGameImageSlide2);
        if(position+1<gamesList.size()) {
            Game game = gamesList.get(position+1);
            Log.i("slide", game.getTitle());
            ids[1] = game.getId();
            textView2.setText(String.valueOf(game.getId()));
            imageView2.setImageFromUri(game.getMediumImage());
            linearLayout2.setOnClickListener(this);
        }
        // set up third layout
        LinearLayout linearLayout3 = (LinearLayout) myView.findViewById(R.id.thirdLinearLayout);
        TextView textView3 = (TextView) myView.findViewById(R.id.gameIdTv3);
        SpearImageView imageView3 = (SpearImageView) myView.findViewById(R.id.imageViewGameImageSlide3);
        if(position+2<gamesList.size()) {
            Game game = gamesList.get(position+2);
            Log.i("slide", game.getTitle());
            ids[2] = game.getId();
            textView3.setText(String.valueOf(game.getId()));
            imageView3.setImageFromUri(game.getMediumImage());
            linearLayout3.setOnClickListener(this);
        }

        ((ViewPager)container).addView(myView);
        return myView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    @Override
    public void onClick(View v) {
        int id = -1;
        // get the clicked view id
        switch (v.getId()){
            case R.id.firstLinearLayout:
                id = gamesList.get(position).getId();
                break;
            case R.id.secondLinearLayout:
                id = gamesList.get(position+1).getId();
                break;
            case R.id.thirdLinearLayout:
                id = gamesList.get(position+2).getId();
                break;
        }
        if(id!=-1) {
            // if the game id is not -1, start a game detail activity to show game's details.
            Intent intent = new Intent(context, GameDetailActivity.class);
            intent.putExtra("adapter", "slideAdapter");
            intent.putExtra("game id", id);
            Log.i("id", "" + id);
            context.startActivity(intent);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // define the first game's position in gamesList
        this.position = position*3;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
