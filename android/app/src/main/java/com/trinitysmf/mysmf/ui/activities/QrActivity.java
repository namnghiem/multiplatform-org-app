package com.trinitysmf.mysmf.ui.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.trinitysmf.mysmf.R;

public class QrActivity extends AppCompatActivity {

    private static final int WHITE = 0xFFFFFF;
    private static final int BLACK = 0X000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView =  findViewById(R.id.iv_qr);
        try {
            Bitmap bitmap = encodeAsBitmap("hgfgdfewedrytfugihoj");
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        Bitmap bitmap = null;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 1000, 1000);//256, 256
       /* int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();*/
            bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.RGB_565);
            for (int x = 0; x < 1000; x++) {
                for (int y = 0; y < 1000; y++) {
                    //bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);//guest_pass_background_color
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);//Color.WHITE
                }
            }
        }catch (WriterException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
