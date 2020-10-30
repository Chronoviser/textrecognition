package com.tas_productions.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
ImageView imageView;
Button capture,detect;
TextView textView;
Bitmap imageBitmap;
static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageviewID);
        capture = findViewById(R.id.capture_buttonID);
        detect=  findViewById(R.id.detect_buttonID);
        textView = findViewById(R.id.textviewID);
        capture.setOnClickListener(this);
        detect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.capture_buttonID:
                dispatchTakePictureIntent();
                textView.setText("");
                break;
            case R.id.detect_buttonID:
                detectText();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
    private void detectText() {
    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
            displayText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Make sure image is not congested!", Toast.LENGTH_SHORT).show();
                Log.d("Error : ",e.getMessage());
            }
        });
    }

    private void displayText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList  = firebaseVisionText.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(this, "Image doesnot contain detectable text!", Toast.LENGTH_SHORT).show();
        }
        else{
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String text =  block.getText();
                textView.setText(text);
            }
        }
    }
}
