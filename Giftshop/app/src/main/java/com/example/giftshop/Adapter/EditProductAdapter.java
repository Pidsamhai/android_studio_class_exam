package com.example.giftshop.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.giftshop.EditProductActivity;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;
import com.example.giftshop.MyProductActivity;
import com.example.giftshop.ProductItemInfoActivity;
import com.example.giftshop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.Holder> {
    private Context mContect;
    private List<Product> products;
    private Activity mactivity;

    public EditProductAdapter(Activity activity, Context context ,List<Product> dataset) {
        mContect = context;
        products = dataset;
        mactivity = activity;
    }

    public EditProductAdapter(Context context, List<Product> dataset) {
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
        TextView title,product_price;
        ImageView img,profile_pic;
        Button b_expand, b_delete, b_edit;
        LinearLayout expand_content;
        AlertDialog.Builder alertDialog;
        FirebaseFirestore db;
        AlertDialog builder;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            product_price = itemView.findViewById(R.id.product_price);
            img = itemView.findViewById(R.id.img);
            b_expand = itemView.findViewById(R.id.b_expand);
            expand_content = itemView.findViewById(R.id.expand_content);
            b_delete = itemView.findViewById(R.id.b_delete);
            b_edit = itemView.findViewById(R.id.b_edit);
            db = FirebaseFirestore.getInstance();
            profile_pic = itemView.findViewById(R.id.img_profile);
        }

        @SuppressLint("DefaultLocale")
        void setItem(final int position) {
            title.setText(products.get(position).getName());
            b_delete.setVisibility(View.VISIBLE);
            b_edit.setVisibility(View.VISIBLE);
            Double _price = Double.parseDouble(products.get(position).getPrice());
            product_price.setText(String.format("ราคา ( %,.2f ) ฿",_price).replace(".00",""));
            final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            final StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
            final StorageReference fileUploadPath = storageReference.child("product/" + products.get(position).getPicture());

            LayoutInflater layoutInflater = mactivity.getLayoutInflater();

            builder = new AlertDialog.Builder(mactivity)
                    .setTitle(R.string._loading)
                    .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                    .setCancelable(false)
                    .create();

            if(!products.get(position).getU_pic().isEmpty() && products.get(position).getU_pic() != null){
                Glide.with(mContect)
                        .load(products.get(position).getU_pic())
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_pic);
            }

            b_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(mactivity, EditProductActivity.class);
                    intent.putExtra(IntentStringHelper.PRUDUCT_ID, products.get(position).getProduct_id());
                    mactivity.startActivity(intent);


                }
            });

            b_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog = new AlertDialog.Builder(mContect)
                            .setTitle(R.string._delete_product)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    builder.setTitle(R.string._get_image_info);
                                    builder.show();
                                    builder.setTitle(R.string._delete_database);
                                    builder.show();
                                    db.collection("product").document(products.get(position).getProduct_id())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContect, "Delete : " + products.get(position).getProduct_id() + " Complete", Toast.LENGTH_LONG).show();
                                                    builder.dismiss();
                                                    builder.setTitle(R.string._delete_image_temp);
                                                    builder.show();
                                                    fileUploadPath.child("product/" + products.get(position).getPicture());
                                                    fileUploadPath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(mContect, R.string._delete_image_complete, Toast.LENGTH_LONG).show();
                                                            builder.dismiss();
                                                            ((MyProductActivity)mContect).upDateData();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("DELETE IMAGE" , "IMG" + e.toString());
                                                            Toast.makeText(mContect, R.string._delete_image_error, Toast.LENGTH_LONG).show();
                                                            builder.dismiss();
                                                            ((MyProductActivity)mactivity).upDateData();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContect, "Delete : " + "Document : " + products.get(position).getProduct_id() + " Error", Toast.LENGTH_LONG).show();
                                            builder.dismiss();
                                        }
                                    });
                                }
                            }).setNegativeButton(R.string.cancel, null);
                    alertDialog.show();
                }
            });
            b_expand.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)b_delete.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            b_delete.setLayoutParams(lp);


            fileUploadPath.getDownloadUrl().

                    addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String transitionName = ViewCompat.getTransitionName(view);
                                    Log.e("IMG", "onClick: " + transitionName);
                                    assert transitionName != null;
                                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContect, view, transitionName);
                                    Intent intent = new Intent(itemView.getContext(), ProductItemInfoActivity.class);
                                    intent.putExtra(IntentStringHelper.IMAGE_URL, uri.toString());
                                    intent.putExtra(IntentStringHelper.PRODUCT_NAME, products.get(position).getName());
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
