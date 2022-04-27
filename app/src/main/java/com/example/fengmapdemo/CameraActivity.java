package com.example.fengmapdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraActivity extends AppCompatActivity {

    final int TAKE_PHOTO = 1;
    final int SELECT_PHOTO = 2;
    private final String[] operations = new String[]{"拍照", "相册"};
    Button buttonWelcome, buttonUpload, buttonRe;
    ImageView imageView;
    Uri imageUri;
    CameraActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //得到按钮和图片显示类
        buttonWelcome = findViewById(R.id.button_welcome);
        buttonUpload = findViewById(R.id.button_upload);
        buttonRe = findViewById(R.id.button_re);
        imageView = findViewById(R.id.imageView);

        //操作选择监听器，点击欢迎或者重拍按钮时，将显示对话框
        SelectOperationsListener selectOperationsListener = new SelectOperationsListener();
        buttonWelcome.setOnClickListener(selectOperationsListener);
        buttonRe.setOnClickListener(selectOperationsListener);
    }

    //显示对话框，根据选择的操作进入不同的函数，选择拍照则唤醒相机，选择相册则从相册中选择照片
    private class SelectOperationsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(self);
            builder.setTitle("请选择操作");
            builder.setItems(operations, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0 :
                            takePhoto();
                            break;
                        case 1:
                            selectPhoto();
                            break;
                        default:
                            break;
                    }
                }
            });
            builder.show();
        }
    }

    //拍照函数，
    public void takePhoto() {
        File output = new File(getExternalCacheDir(),"output_image.jpg");
        try {
            if (output.exists()){
                output.delete();
            }
            output.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            //图片的保存路径
            imageUri= FileProvider.getUriForFile(self,"com.example.fengmapdemo.fileprovider",output);
        }
        else { imageUri=Uri.fromFile(output);}
        //跳转界面到系统自带的拍照界面
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    public void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    private class UploadOnClickListener implements View.OnClickListener{
        Response response = null;
        @Override
        public void onClick(View v) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("", "abc");
                    String url = "http://10.176.54.14:8081/localization";
                    File file = new File(getExternalCacheDir(), "output_image.jpg");
                    String fileName = file.getAbsolutePath();
                    System.out.println(fileName);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", fileName,
                                    RequestBody.create(MediaType.parse("image/*"), file))
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization", "Client-ID " + UUID.randomUUID())
                            .url(url)
                            .post(requestBody)
                            .build();
                    try {
                        response = client.newCall(request).execute();
//                        Log.d("response", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!response.isSuccessful()) try {
                        throw new IOException("Unexpected code " + response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //跳转到 mainActivity2
            Intent intent = new Intent(self, MainActivity2.class);
            try {
                intent.putExtra("images", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
    }

    protected  void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    // 使用try让程序运行在内报错
                    try {
                        //将图片保存
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                        buttonUpload.setVisibility(Button.VISIBLE);
                        buttonUpload.setOnClickListener(new UploadOnClickListener());
                        buttonRe.setVisibility(Button.VISIBLE);
                        buttonWelcome.setVisibility(Button.INVISIBLE);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        imageView.setImageBitmap(bitmap);
                        File file = new File(getExternalCacheDir(), "output_image.jpg");
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buttonUpload.setVisibility(Button.VISIBLE);
                        buttonUpload.setOnClickListener(new UploadOnClickListener());
                        buttonRe.setVisibility(Button.VISIBLE);
                        buttonWelcome.setVisibility(Button.INVISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            default:break;
        }
    }
}