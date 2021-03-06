package com.itgarage.harvey.gamecollections.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.io.InputStream;
import java.util.List;

/**
 * This class is used to create the items in navigate drawer list.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private String profile;        //int Resource for header view profile_fb picture
    Context context;
    private NaviDrawerActivity activity;
    private String FB_OR_GOOGLE;
    private static final String TAG = "DrawerListAdapter";
    ViewHolder holder;
    GamesDataSource dataSource;
    List<Game> gameList, favouriteList, lendList, wishList;

    /**
     * Creating a ViewHolder which extends the RecyclerView View Holder
     * ViewHolder are used to to store the inflated views in order to recycle them
     */

    public static class ViewHolder extends RecyclerView.ViewHolder{
        int Holderid;

        TextView textView;
        ImageView imageView;
        //ImageView profile_fb;
        ProfilePictureView profile_fb;
        ImageView profile_google;
        TextView Name;
        //TextView email;
        Context context;
        NaviDrawerActivity activity;
        TextView allGameTotalTv, favouriteGameTotalTv, lendGameTotalTv, wishGameTotalTv;


        public ViewHolder(View itemView, int ViewType, Context c, NaviDrawerActivity activity) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            itemView.setClickable(true);
            this.activity = activity;
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.drawer_listItem_text); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.drawer_listItem_icon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {
                Name = (TextView) itemView.findViewById(R.id.username_text);         // Creating Text View object from header.xml for name
                //email = (TextView) itemView.findViewById(R.id.user_email_text);       // Creating Text View object from header.xml for email
                //profile_fb = (ImageView) itemView.findViewById(R.id.user_image);// Creating Image view object from header.xml for profile_fb pic
                profile_fb = (ProfilePictureView) itemView.findViewById(R.id.user_image_fb);
                profile_google = (ImageView) itemView.findViewById(R.id.user_image_google);
                Holderid = 0;                                             // Setting holder id = 0 as the object being populated are of type header view
                allGameTotalTv = (TextView) itemView.findViewById(R.id.allGameTotalTv);
                favouriteGameTotalTv = (TextView) itemView.findViewById(R.id.FavouriteGameTotalTv);
                lendGameTotalTv = (TextView) itemView.findViewById(R.id.lendGameTotalTv);
                wishGameTotalTv = (TextView) itemView.findViewById(R.id.wishGameTotalTv);
            }
        }
    }

    /**
     * Constructor for DrawerListAdapter.
     * @param Titles Array contains titles.
     * @param Icons Array contains icons.
     * @param Name User name.
     * @param Profile Facebook profile id or Google+ profile image URL.
     * @param FB_OR_GOOGLE Login with Facebook or Google+.
     * @param passedContext Context passed in.
     * @param activity The navi activity.
     */
    public DrawerListAdapter(String Titles[], int Icons[], String Name, String Profile, String FB_OR_GOOGLE, Context passedContext, NaviDrawerActivity activity) { // DrawerListAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile_fb pic are passed from the main activity as we
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        name = Name;
        //email = Email;
        profile = Profile;                     //here we assign those passed values to the values we declared here
        this.context = passedContext;
        this.activity = activity;
        this.FB_OR_GOOGLE = FB_OR_GOOGLE;
        dataSource = new GamesDataSource(activity);
        dataSource.open();
        gameList = dataSource.getAllGames();
        favouriteList = dataSource.getAllFavouriteGames();
        wishList = dataSource.getAllWishGames();
        lendList = dataSource.getAllLendGames();
        dataSource.close();
        //in adapter
    }
    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType, context, activity); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType, context, activity); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerListAdapter.ViewHolder holder, int position) {
        this.holder = holder;
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons
        } else {

            //holder.profile_fb.setImageResource(profile_fb);           // Similarly we set the resources for header view
            switch (FB_OR_GOOGLE) {
                case "facebook": //facebook login
                    Log.i(TAG, "facebook");
                    holder.profile_fb.setProfileId(profile);
                    holder.profile_fb.setVisibility(View.VISIBLE);
                    break;
                case "google": //google login
                    Log.i(TAG, "google profile "+profile);
                    new LoadProfileImage(holder.profile_google).execute(profile);
                    holder.profile_google.setVisibility(View.VISIBLE);
                    break;
                case "":
                    Log.i(TAG, "no login");
                    holder.profile_fb.setVisibility(View.GONE);
                    holder.profile_google.setVisibility(View.GONE);
                    break;
            }
            holder.Name.setText(name);
            dataSource.open();
            gameList = dataSource.getAllGames();
            favouriteList = dataSource.getAllFavouriteGames();
            wishList = dataSource.getAllWishGames();
            lendList = dataSource.getAllLendGames();
            dataSource.close();
            // calculate how many games are in DB
            if(gameList == null){
                holder.allGameTotalTv.setText(activity.getString(R.string.all_game_total_tv)+"\t\t"+String.valueOf(0));
            }else {
                holder.allGameTotalTv.setText(activity.getString(R.string.all_game_total_tv) + "\t\t" + String.valueOf(gameList.size()));
            }
            if(favouriteList == null){
                holder.favouriteGameTotalTv.setText(activity.getString(R.string.favourite_game_total_tv)+"\t"+String.valueOf(0));
            }else {
                holder.favouriteGameTotalTv.setText(activity.getString(R.string.favourite_game_total_tv) + "\t" + String.valueOf(favouriteList.size()));
            }
            if(wishList == null){
                holder.wishGameTotalTv.setText(activity.getString(R.string.wish_game_total_tv)+"\t"+String.valueOf(0));
            }else {
                holder.wishGameTotalTv.setText(activity.getString(R.string.wish_game_total_tv) + "\t" + String.valueOf(wishList.size()));
            }
            if(lendList == null) {
                holder.lendGameTotalTv.setText(activity.getString(R.string.lend_game_total_tv) + "\t" + String.valueOf(0));
            }else {
                holder.lendGameTotalTv.setText(activity.getString(R.string.lend_game_total_tv) + "\t" + String.valueOf(lendList.size()));
            }
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Update numbers those indicate the numbers of different kind of games.
     */
    public void updateNumberCalculate(){
        dataSource.open();
        gameList = dataSource.getAllGames();
        favouriteList = dataSource.getAllFavouriteGames();
        wishList = dataSource.getAllWishGames();
        lendList = dataSource.getAllLendGames();
        dataSource.close();
        notifyDataSetChanged();
    }
}
