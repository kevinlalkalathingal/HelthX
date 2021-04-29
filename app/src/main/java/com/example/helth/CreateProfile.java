package com.example.helth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class CreateProfile extends AppCompatActivity {

    EditText etname, etage, etcontact, etblood, etheight, etweight;
    Button button;
    ImageView imageView;
    Uri imageuri;
    ProgressBar progressBar;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    private static final int PICK_IMAGE = 1;
    AllUserMember member;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new AllUserMember();
        imageView = findViewById(R.id.iv_cp);
        etname = findViewById(R.id.et_name_cp);
        etage = findViewById(R.id.et_age_cp);
        etblood = findViewById(R.id.et_blood_cp);
        etcontact = findViewById(R.id.et_contact_cp);
        etheight = findViewById(R.id.et_Height_cp);
        etweight = findViewById(R.id.et_weight_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");
         databaseReference = database.getReference("All Users");

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 uploadData();
             }
         });

         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(intent, PICK_IMAGE);
             }
         });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null
                    || data.getData() != null){
                imageuri = data.getData();

                Picasso.get().load(imageuri).into(imageView);
            }
        }catch (Exception e) {
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {
        String name = etname.getText().toString();
        String blood = etblood.getText().toString();
        String contact = etcontact.getText().toString();
        String age = etage.getText().toString();
        String height = etheight.getText().toString();
        String weight = etweight.getText().toString();

        if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(blood)
            || !TextUtils.isEmpty(contact) || imageuri != null
            || !TextUtils.isEmpty(age) || TextUtils.isEmpty(height)
            || !TextUtils.isEmpty(weight)){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(imageuri));
            uploadTask = reference.putFile(imageuri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        Map<String, String> profile = new HashMap<>();
                        profile.put("name", name);
                        profile.put("url", downloadUri.toString());
                        profile.put("blood", blood);
                        profile.put("contact", contact);
                        profile.put("age", age);
                        profile.put("uid", currentUserId);
                        profile.put("height", height);
                        profile.put("weight", weight);
                        profile.put("privacy", "public");

                        member.setName(name);
                        member.setUid(currentUserId);
                        member.setBlood(blood);
                        member.setContact(contact);
                        member.setAge(age);
                        member.setHeight(height);
                        member.setWeight(weight);
                        member.setUid(currentUserId);
                        member.setUrl(downloadUri.toString());

                        databaseReference.child(currentUserId).setValue(member);

                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(CreateProfile.this, Fragment2.class);
                                        startActivity(intent);
                                    }
                                }, 2000);
                            }
                        });

                    }
                }
            });
        }else{
            Toast.makeText(this, "please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }
}