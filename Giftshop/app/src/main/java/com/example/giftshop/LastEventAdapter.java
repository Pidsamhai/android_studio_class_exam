package com.example.giftshop;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LastEventAdapter extends PagerAdapter {

    private Context mContext;
    private List<Product> products;

    LastEventAdapter(Context context){
        mContext = context;
    }

    LastEventAdapter(Context context, List<Product> p){
        mContext = context;
        products = p;
    }

    private int[] img = new int[]{
        R.drawable.ic_add_shopping_cart_24dp, R.drawable.ic_arrow_back_24dp , R.drawable.ic_insert_photo_24dp
    };


    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(mContext);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
        StorageReference fileUploadPath = storageReference.child("product/" + products.get(position).getPicture());
        fileUploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .centerCrop()
                        .into(imageView);
                        container.addView(imageView);
            }
        });

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((ImageView) object);
    }
}
