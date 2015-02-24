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
    String title;
    int position;

    public ImageSlideAdapter(Context context, List<Game> gamesList, ViewPager viewPager){
        this.context = context;
        this.gamesList = gamesList;
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public void updateList(List<Game> gamesList){
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(gamesList==null)
            return 0;
        return gamesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mInfalter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = mInfalter.inflate(R.layout.container_slider_home, container, false);

        TextView textView = (TextView) myView.findViewById(R.id.image_description_slide);
        SpearImageView imageView = (SpearImageView) myView.findViewById(R.id.imageViewGameImageSlide);

        Game game = gamesList.get(position);
        title = game.getTitle();
        textView.setText(game.getTitle());
        imageView.setImageFromUri(game.getLargeImage());
        myView.setOnClickListener(this);
        //((AutoScrollViewPager)container).addView(myView);
        ((ViewPager)container).addView(myView);
        return myView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, GameDetailActivity.class);
        intent.putExtra("game position", position);
        Log.i("title", "" + title);
        Log.i("position", ""+position);
        context.startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.position = position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
