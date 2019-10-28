package com.example.giftshop.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.example.giftshop.Model.Product;
import com.example.giftshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LastEventAdapter_loop extends LoopingPagerAdapter<Product> {

    public LastEventAdapter_loop(Context context, List<Product> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.last_product_iten, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        final ImageView imageView = convertView.findViewById(R.id.items);
                Glide.with(context)
                        .load(itemList.get(listPosition).getPicture_url())
                        .centerCrop()
                        .into(imageView);
    }
}
