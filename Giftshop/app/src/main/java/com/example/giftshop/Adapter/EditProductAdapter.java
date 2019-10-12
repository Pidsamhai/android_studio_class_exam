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
import android.widget.EditText;
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
import com.example.giftshop.Model.Product;
import com.example.giftshop.ProductItemInfoActivity;
import com.example.giftshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;


public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.Holder> {
    private Context mContect;
    private List<Product> products;
    private Activity mactivity;

    private String IMAGE_URL = "IMAGE_URL";
    private String PRODUCT_NAME = "PRODUCT_NAME";
    private String PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";
    private String PRODUCT_TEL = "PRODUCT_TEL";
    public String LAT = "LAT";
    public String LON = "LON";

    public EditProductAdapter(Activity activity, Context context, List<Product> dataset) {
        mContect = context;
        products = dataset;
        mactivity = activity;
    }
    public EditProductAdapter(Context context, List<Product> dataset) {
        mContect = context;
        products = dataset;;
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
        TextView title, t_tel;
        ImageView img;
        Button b_expand, b_delete, b_edit;
        LinearLayout expand_content;
        RelativeLayout call;
        AlertDialog.Builder alertDialog;
        FirebaseFirestore db;
        AlertDialog builder;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            b_expand = itemView.findViewById(R.id.b_expand);
            expand_content = itemView.findViewById(R.id.expand_content);
            call = itemView.findViewById(R.id.call);
            t_tel = itemView.findViewById(R.id.t_tel);
            b_delete = itemView.findViewById(R.id.b_delete);
            b_edit = itemView.findViewById(R.id.b_edit);
            db = FirebaseFirestore.getInstance();
        }

        void setItem(final int position) {
            title.setText(products.get(position).getName());
            t_tel.setText(products.get(position).getTel());
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

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + products.get(position).getTel()));
                    mContect.startActivity(intent);
                }
            });

            b_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContect,"click",Toast.LENGTH_LONG).show();
                    final Intent intent = new Intent(mactivity, EditProductActivity.class);
                    fileUploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           intent.putExtra(IMAGE_URL,uri.toString());
                           intent.putExtra(PRODUCT_NAME,products.get(position).getName());
                           intent.putExtra(PRODUCT_DESCRIPTION,products.get(position).getDescription());
                           intent.putExtra(PRODUCT_TEL,products.get(position).getTel());
                           mactivity.startActivity(intent);
                       }
                   });


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
                                    db.collection("product")
                                            .whereEqualTo("product_id", products.get(position).getProduct_id())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        String doc_id = null;
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                            Log.d("", document.getId() + " => " + document.getData());
                                                            doc_id = document.getId();
                                                        }
                                                        assert doc_id != null;
                                                        final String finalDoc_id = doc_id;
                                                        builder.dismiss();
                                                        builder.setTitle("Delete Database...");
                                                        builder.show();
                                                        db.collection("product").document(doc_id)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(mContect, "Delete : " + finalDoc_id + " Complete" , Toast.LENGTH_LONG).show();
                                                                        builder.dismiss();
                                                                        builder.setTitle("Delete Picture temp...");
                                                                        builder.show();
                                                                        fileUploadPath.child("product/"+products.get(position).getPicture());
                                                                        fileUploadPath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(mContect,"Delete Picture Complete",Toast.LENGTH_LONG).show();
                                                                                builder.dismiss();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(mContect,"Delete Picture Error",Toast.LENGTH_LONG).show();
                                                                                builder.dismiss();
                                                                            }
                                                                        });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(mContect, "Delete : " + "Document : " +finalDoc_id + " Error" , Toast.LENGTH_LONG).show();
                                                                builder.dismiss();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(mContect, "Cant find Id", Toast.LENGTH_LONG).show();
                                                        //mactivity.finish();
                                                        builder.dismiss();
                                                        mactivity.setResult(10);
                                                    }
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
