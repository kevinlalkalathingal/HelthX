package com.example.helth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment2 extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt, ageEt, bloodEt, contactEt, heightEt, weightEt;
    ImageButton imageButtonEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getActivity().findViewById(R.id.iv_f2);
        nameEt = getActivity().findViewById(R.id.tv_name_f2);
        ageEt = getActivity().findViewById(R.id.tv_age_f2);
        bloodEt = getActivity().findViewById(R.id.tv_blood_f2);
        contactEt = getActivity().findViewById(R.id.tv_contact_f2);
        heightEt = getActivity().findViewById(R.id.tv_height_f2);
        weightEt = getActivity().findViewById(R.id.tv_weight_f2);
        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f2);

        imageButtonEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_edit_f2:
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){

                            String nameResult = task.getResult().getString("name");
                            String ageResult = task.getResult().getString("age");
                            String contactResult = task.getResult().getString("contact");
                            String bloodResult = task.getResult().getString("blood");
                            String heightResult = task.getResult().getString("height");
                            String weightResult = task.getResult().getString("weight");
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(imageView);
                            nameEt.setText(nameResult);
                            ageEt.setText(ageResult);
                            contactEt.setText(contactResult);
                            bloodEt.setText(bloodResult);
                            heightEt.setText(heightResult);
                            weightEt.setText(weightResult);

                        }else{
                            Intent intent = new Intent(getActivity(), CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                });

    }
}
