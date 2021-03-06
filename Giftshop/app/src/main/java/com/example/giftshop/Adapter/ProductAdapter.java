package com.example.giftshop.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;
import com.example.giftshop.ProductItemInfoActivity;
import com.example.giftshop.R;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder> {
    private Context mContect;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> dataset) {
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
        TextView title,profile_name,product_price;
        ImageView img,facebook,line,location,call,profile_pic;
        Button b_expand;
        LinearLayout expand_content;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            profile_name = itemView.findViewById(R.id.profile_name);
            product_price = itemView.findViewById(R.id.product_price);
            img = itemView.findViewById(R.id.img);
            b_expand = itemView.findViewById(R.id.b_expand);
            expand_content = itemView.findViewById(R.id.expand_content);
            facebook = itemView.findViewById(R.id.img_facebook);
            line = itemView.findViewById(R.id.img_line);
            call = itemView.findViewById(R.id.img_call);
            location = itemView.findViewById(R.id.img_location);
            profile_pic = itemView.findViewById(R.id.img_profile);
        }

        @SuppressLint("DefaultLocale")
        void setItem(final int position) {
            title.setText(products.get(position).getName());
            Double _price = Double.parseDouble(products.get(position).getPrice());
            product_price.setText(String.format("( %,.2f ) ฿",_price).replace(".00",""));
            profile_name.setText(products.get(position).getU_name());

            if(!products.get(position).getU_pic().isEmpty() && products.get(position).getU_pic() != null){
                Glide.with(mContect)
                        .load(products.get(position).getU_pic())
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_pic);
            }

            if(!products.get(position).getFacebook_url().isEmpty()){
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = products.get(position).getFacebook_url();
                        url = url.replace("http://","");
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://" + url));
                        try{
                            mContect.startActivity(intent );
                        }catch (Exception e){
                            Toast.makeText(mContect,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                facebook.setVisibility(View.GONE);
            }

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + products.get(position).getTel()));
                    mContect.startActivity(intent);
                }
            });

            if(!products.get(position).getLon().trim().isEmpty() || !products.get(position).getLat().trim().isEmpty()){
                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strUri = "http://maps.google.com/maps?q=loc:" + products.get(position).getLat() + "," + products.get(position).getLon();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                        try{
                            mContect.startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(mContect,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else {
                location.setVisibility(View.GONE);
            }

            if(!products.get(position).getLine_url().isEmpty()){
                        line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(products.get(position).getLine_url()));
                        try{
                            mContect.startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(mContect,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else {
                line.setVisibility(View.GONE);
            }


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

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String transitionName = ViewCompat.getTransitionName(view);
                    //Log.e("IMG", "onClick: " + transitionName);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContect, view, transitionName);
                    Intent intent = new Intent(itemView.getContext(), ProductItemInfoActivity.class);
                    intent.putExtra(IntentStringHelper.PRODUCT_IMG_URL, products.get(position).getPicture_url());
                    intent.putExtra(IntentStringHelper.PROFILE_NAME, products.get(position).getU_name());
                    intent.putExtra(IntentStringHelper.PROFILE_PIC_URL, products.get(position).getU_pic());
                    intent.putExtra(IntentStringHelper.PRODUCT_NAME, products.get(position).getName());
                    intent.putExtra(IntentStringHelper.LINE_ID, products.get(position).getLine_id());
                    intent.putExtra(IntentStringHelper.PRODUCT_PRICE, products.get(position).getPrice());
                    intent.putExtra(IntentStringHelper.PRODUCT_TEL, products.get(position).getTel());
                    intent.putExtra(IntentStringHelper.PRODUCT_DESCRIPTION, products.get(position).getDescription());
                    intent.putExtra(IntentStringHelper.FACEBOOK_NAME, products.get(position).getFacebook_name());
                    intent.putExtra(IntentStringHelper.FACEBOOK_URL, products.get(position).getFacebook_url());
                    intent.putExtra(IntentStringHelper.LINE_URL, products.get(position).getLine_url());
                    intent.putExtra(IntentStringHelper.LAT, products.get(position).getLat());
                    intent.putExtra(IntentStringHelper.LON, products.get(position).getLon());
                    mContect.startActivity(intent, activityOptionsCompat.toBundle());
                }
            });
            Glide.with(itemView.getContext())
                    .load(products.get(position).getPicture_url())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(img);
        }
    }

}
