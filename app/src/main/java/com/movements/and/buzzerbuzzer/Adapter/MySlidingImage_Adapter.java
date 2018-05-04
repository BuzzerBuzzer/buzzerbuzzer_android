package com.movements.and.buzzerbuzzer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Model.UserPic;
import com.movements.and.buzzerbuzzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by samkim on 2017. 4. 26..
 */
//유저 이미지 슬라이드 어댑터
public class MySlidingImage_Adapter extends PagerAdapter {
    private ArrayList<UserPic> IMAGES;
    private LayoutInflater inflater;
    private Context mContext;
    private SharedPreferences setting;
    private int selectedPosition;//setSelectedPosition 1

    public MySlidingImage_Adapter(Context context, ArrayList<UserPic> IMAGES) {
        this.mContext = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View itemView = inflater.inflate(R.layout.gallary, view, false);
        final UserPic friends = IMAGES.get(position);
        assert itemView != null;
        PhotoViewAttacher mAttacher;
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.image1);

        try {
            mAttacher = new PhotoViewAttacher(imageView);
            // imageView.setImageResource(IMAGES.get(position));
//            Picasso.with(mContext)
//                    .load(friends.getUser_pic_src())
//                    .placeholder(R.drawable.user_prof_bg)
//                    .error(R.drawable.user_prof_bg)
//                    .into(imageView);
            Glide.with(mContext)
                    .load(friends.getUser_pic_src())
                    .placeholder(R.drawable.user_prof_bg)
                    .error(R.drawable.user_prof_bg)
                    .fitCenter()
                    .into(imageView);
        } catch (Exception e) {

        }

        view.addView(itemView, 0);

        return itemView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        // return super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}