package com.br.theboysentregasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    public static final String PROFILE = "Perfil ";
    public static final String SELECTED = " selecionado";

    private EditText mEditUsername;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnEnter;
    private Button mBtnSelectedPhoto;
    private Uri mSelectedUri;
    private ImageView mImgPhoto;

    private RadioGroup mRadioOccupation;
    private RadioButton mRadioMotoboyButton;
    private RadioButton mRadioOperatorButton;
    private String occupation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEditEmail = findViewById(R.id.edit_email);
        mEditUsername = findViewById(R.id.edit_username);
        mEditPassword = findViewById(R.id.edit_password);
        mBtnEnter = findViewById(R.id.btn_enter);
        mBtnSelectedPhoto = findViewById(R.id.btn_selected_photo);
        mImgPhoto = findViewById(R.id.image_photo);
        mRadioOccupation = findViewById(R.id.radioGroup);
        mRadioMotoboyButton = findViewById(R.id.radio_motoboy);
        mRadioOperatorButton = findViewById(R.id.radio_operator);

        mBtnSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        getSupportActionBar().setTitle("Registrar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            mSelectedUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mImgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                mBtnSelectedPhoto.setAlpha(0);
            } catch (IOException e) {

            }

        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }

    private void createUser() {
        String name = mEditUsername.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mRadioOccupation.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Nome, senha, ocupação e email devem ser preenchidos", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Teste", task.getResult().getUser().getUid());

                            saveUserInFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste", e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void saveUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Teste", uri.toString());

                                String uid = FirebaseAuth.getInstance().getUid();
                                String username = mEditUsername.getText().toString();
                                String profileUrl = uri.toString();

                                User user = new User(uid, username, profileUrl, occupation);
                                user.setOccupation(occupation);

                                SharedPreferences sharedPreferences = getSharedPreferences("key", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("occupation", occupation);
                                editor.apply();

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Intent intent = new Intent(RegisterActivity.this, MessagesActivity.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Teste", e.getMessage());
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Teste", e.getMessage(), e);
                    }
                });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_motoboy:
                if (checked) {
                    String motoboyOptionText = mRadioMotoboyButton.getText().toString();
                    occupation = motoboyOptionText;
                    Toast.makeText(this, PROFILE + motoboyOptionText + SELECTED, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.radio_operator:
                if (checked) {
                    String operatorOptionText = mRadioOperatorButton.getText().toString();
                    occupation = operatorOptionText;
                    Toast.makeText(this, PROFILE + operatorOptionText + SELECTED, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
