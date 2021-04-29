package com.example.helth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import javax.xml.parsers.DocumentBuilder;

public class UpdateProfile extends AppCompatActivity {

    EditText etname, etage, etcontact, etblood, etheight, etweight;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        documentReference = db.collection("user").document(currentuid);

        etname = findViewById(R.id.et_name_cp);
        etage = findViewById(R.id.et_age_cp);
        etcontact = findViewById(R.id.et_contact_cp);
        etblood = findViewById(R.id.et_blood_cp);
        etweight = findViewById(R.id.et_weight_cp);
        etheight = findViewById(R.id.et_Height_cp);
        button = findViewById(R.id.btn_up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()) {
                            String nameResult = task.getResult().getString("name");
                            String ageResult = task.getResult().getString("age");
                            String contactResult = task.getResult().getString("contact");
                            String bloodResult = task.getResult().getString("blood");
                            String heightResult = task.getResult().getString("height");
                            String weightResult = task.getResult().getString("weight");
                            String url = task.getResult().getString("url");

                            etname.setText(nameResult);
                            etage.setText(ageResult);
                            etcontact.setText(contactResult);
                            etblood.setText(bloodResult);
                            etheight.setText(heightResult);
                            etweight.setText(weightResult);
                        }
                        else {
                            Toast.makeText(UpdateProfile.this, "Profile doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String name = etname.getText().toString();
        String age = etage.getText().toString();
        String contact = etcontact.getText().toString();
        String blood = etblood.getText().toString();
        String height = etheight.getText().toString();
        String weight = etweight.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentuid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                //DocumentSnapshot snapshot = transaction.get(sfDocRef);

                transaction.update(sDoc, "name", name);
                transaction.update(sDoc,"age", age);
                transaction.update(sDoc, "contact", contact);
                transaction.update(sDoc, "blood", blood);
                transaction.update(sDoc, "height", height);
                transaction.update(sDoc, "weight", weight);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}