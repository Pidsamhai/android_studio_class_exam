package com.example.giftshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder> {
    private String [] mDataSet;
    private Context mContect;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> dataset){
        mContect = context;
        products = dataset;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holders, int position) {
        holders.setItem(position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;
        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
        public void setItem(final int position){
            title.setText(products.get(position).getName());
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
            StorageReference fileUploadPath = storageReference.child("product/" + products.get(position).getPicture());

            fileUploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String transitionName =  ViewCompat.getTransitionName(view);
                            Log.e("IMG", "onClick: " + transitionName);
                            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContect,view,transitionName);
                            Intent intent = new Intent(itemView.getContext(),ProductItemInfoActivity.class);
                            intent.putExtra("IMG_URL",uri.toString());
                            mContect.startActivity(intent,activityOptionsCompat.toBundle());
                        }
                    });
                    Glide.with(itemView.getContext())
                            .load(uri)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(img);
                    Log.e("Adaptter", "onSuccess: " + uri);
                }
            });

        }
    }

}
