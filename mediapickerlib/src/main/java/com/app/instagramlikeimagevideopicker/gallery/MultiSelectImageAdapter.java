package com.app.instagramlikeimagevideopicker.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import com.app.instagramlikeimagevideopicker.R;

public class MultiSelectImageAdapter extends PagerAdapter {

    private final Context context;
    private final List<String> addresses;
    private final SelectListener sl;
    private LayoutInflater inflater;

    MultiSelectImageAdapter(Context context, List<String> addresses, SelectListener sl) {
        this.context = context;
        this.addresses = addresses;
        this.sl = sl;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String model = addresses.get(position);

        View convertView = inflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = convertView.findViewById(R.id.iv_image);
        ImageView cropView = convertView.findViewById(R.id.iv_crop);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        imageView.setOnClickListener(w -> sl.onClick(model, position));
        cropView.setOnClickListener(w -> sl.onClick(model, position));

//        Picasso.get().load(Uri.parse(model)).into(imageView);
        Glide.with(context)
                .load(model)
                .into(imageView);
        container.addView(convertView);

        return convertView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
