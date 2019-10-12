package com.example.giftshop.Adapter;

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
import com.example.giftshop.EditProductActivity;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;
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

    public EditProductAdapter(Activity activity, Context context, List<Product> dataset) {
        mContect = context;
        products = dataset;
        mactivity = activity;
    }

    public EditProductAdapter(Context context, List<Product> dataset) {
        mContect = context;
        products = dataset;
        ;
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
        TextView title;
        ImageView img;
        Button b_expand, b_delete, b_edit;
        LinearLayout expand_content;
        AlertDialog.Builder alertDialog;
        FirebaseFirestore db;
        AlertDialog builder;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            b_expand = itemView.findViewById(R.id.b_expand);
            expand_content = itemView.findViewById(R.id.expand_content);
            b_delete = itemView.findViewById(R.id.b_delete);
            b_edit = itemView.findViewById(R.id.b_edit);
            db = FirebaseFirestore.getInstance();
        }

        void setItem(final int position) {
            title.setText(products.get(position).getName());
            b_delete.setVisibility(View.VISIBLE);
            b_edit.setVisibility(View.VISIBLE);
            final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            final StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://pcru-giftshop.appspot.com");
            final StorageReference fileUploadPath = storageReference.child("product/" + products.get(position).getPicture());

            LayoutInflater layoutInflater = mactivity.getLayoutInflater();

            builder = new AlertDialog.Builder(mactivity)
                    .setTitle("Loading...")
                    .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                    .setCancelable(false)
                    .create();

            b_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContect, "click", Toast.LENGTH_LONG).show();
                    final Intent intent = new Intent(mactivity, EditProductActivity.class);
                    intent.putExtra(IntentStringHelper.PRUDUCT_ID, products.get(position).getProduct_id());
                    mactivity.startActivity(intent);


                }
            });

            b_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog = new AlertDialog.Builder(mContect)
                            .setTitle("Delete Product ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    builder.setTitle("Get Picture info...");
                                    builder.show();
                                    builder.setTitle("Delete Database...");
                                    builder.show();
                                    db.collection("product").document(products.get(position).getProduct_id())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContect, "Delete : " + products.get(position).getProduct_id() + " Complete", Toast.LENGTH_LONG).show();
                                                    builder.dismiss();
                                                    builder.setTitle("Delete Picture temp...");
                                                    builder.show();
                                                    fileUploadPath.child("product/" + products.get(position).getPicture());
                                                    fileUploadPath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(mContect, "Delete Picture Complete", Toast.LENGTH_LONG).show();
                                                            builder.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(mContect, "Delete Picture Error", Toast.LENGTH_LONG).show();
                                                            builder.dismiss();
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
                            }).setNegativeButton("Cancel", null);
                    alertDialog.show();
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

            fileUploadPath.getDownloadUrl().

                    addOnSuccessListener(new OnSuccessListener<Uri>() {
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
