package com.pens.kamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    Button btnCamera;
    Button btnSave;
    ImageView ivCapturedImage;
    Bitmap bitmap;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btn_open);
        btnSave = findViewById(R.id.btn_save);
        ivCapturedImage = findViewById(R.id.iv_capturedimage);
        btnCamera.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            Glide.with(this).load(bitmap).into(ivCapturedImage);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveImage() {
        if (bitmap != null) {
            try {
                photoFile = createImageFile();
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                bitmap.recycle();
                bitmap = null;
                Glide.with(this).clear(ivCapturedImage);
                Toast.makeText(MainActivity.this, "Berhasil menyimpan gambar!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error accessing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MainActivity.this, "Tidak ada gambar!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                openCamera();
                break;
            case R.id.btn_save:
                saveImage();
                break;
        }
    }
}
