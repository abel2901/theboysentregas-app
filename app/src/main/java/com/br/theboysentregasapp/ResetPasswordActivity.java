package com.br.theboysentregasapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private Button mBtnReset;
    private TextView mTxtResetInfo;
    private Button mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mEditEmail = findViewById(R.id.edit_reset_email_password);
        mBtnReset = findViewById(R.id.btn_reset);
        mTxtResetInfo = findViewById(R.id.txt_reset_password_info);
        mBtnBack = findViewById(R.id.btn_back);

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            @Override
            public void onClick(View view) {
                if (mEditEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Insira um email valido", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(mEditEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mEditEmail.setVisibility(View.GONE);
                                        mBtnReset.setVisibility(View.GONE);
                                        mTxtResetInfo.setVisibility(View.VISIBLE);
                                        mBtnBack.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, "Email não cadastrado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
