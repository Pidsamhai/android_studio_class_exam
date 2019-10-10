package com.example.giftshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private String[] mDataSet;
    private Context mContect;
    private List<Product> products;

    ProductAdapter(Context context, List<Product> dataset) {
        mContect = context;
        products = dataset;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items, parent, false);
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
        TextView title,t_tel;
        ImageView img;
        Button b_expand;
        LinearLayout expand_content;
        RelativeLayout call;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            b_expand = itemView.findViewById(R.id.b_expand);
            expand_content = itemView.findViewById(R.id.expand_content);
            call = itemView.findViewById(R.id.call);
            t_tel = itemView.findViewById(R.id.t_tel);
        }

        void setItem(final int position) {
            title.setText(products.get(position).getName());
            t_tel.setText(products.get(position).getTel());
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
            StorageReference fileUploadPath = storageReference.child("product/" + products.get(position).getPicture());

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+products.get(position).getTel()));
                    mContect.startActivity(intent);
                }
            });


            b_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expand_content.getVisibility() == View.GONE) {
                        expand_content.setVisibility(View.VISIBLE);
                        b_expand.setBackground(itemView.getResources().getDrawable(R.drawable.ic_expand_less_24dp));
                    } else {
                        expand_content.setVisibility(View.GONE);
                        b_expand.setBackground(itemView.getResources().getDrawable(R.drawable.ic_expand_more_24dp));
                    }
                }
            });

            fileUploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String transitionName = ViewCompat.getTransitionName(view);
                            Log.e("IMG", "onClick: " + transitionName);
                            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContect, view, transitionName);
                            Intent intent = new Intent(itemView.getContext(), ProductItemInfoActivity.class);
                            intent.putExtra("IMG_URL", uri.toString());
                            mContect.startActivity(intent, activityOptionsCompat.toBundle());
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
