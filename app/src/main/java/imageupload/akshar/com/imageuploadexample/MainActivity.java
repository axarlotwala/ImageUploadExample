package imageupload.akshar.com.imageuploadexample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.ActionProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bikomobile.multipart.Multipart;
import com.bikomobile.multipart.MultipartRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageAdd;
    EditText name;
    Button select, upload;
    private Uri FilePath;
    Bitmap bitmap;
    private String UPLOAD_URL = "http://192.168.0.103/shopping/file_upload.php";
    Context context;
    TextView storage_path;

    private static final int STORAGE_PERMISSION_CODE = 2210;
    private static final int PICK_IMAGE_REQUEST = 2310;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageAdd = findViewById(R.id.imageAdd);
        name = findViewById(R.id.name);
        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        storage_path = findViewById(R.id.storage_path);
        select.setOnClickListener(this);
        upload.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v == upload) {
            uploadImage();
        }

        if (v == select) {
            ShowFileChooser();
        }
    }


    //Open Dialog For Choose Images

    private void ShowFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    //Showing Result in  ImageView

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePath);
                imageAdd.setImageBitmap(bitmap);
                storage_path.setText("Path:".concat(getPath(FilePath)));


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    } //over these method


    private String getPath(Uri uri) {

        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor == null){
            result = uri.getPath();
        }else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            result = cursor.getString(id);
            cursor.close();
        }

        return result;
    }


    private void uploadImage() {

        final String NAME = name.getText().toString().trim();
        String path = getPath(FilePath);

        try {

            String Upload_ID = UUID.randomUUID().toString();
            try {
                new MultipartUploadRequest(this, Upload_ID, UPLOAD_URL)
                        .addFileToUpload(path, "image")
                        .addParameter("name", NAME)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();

        }

    }

}
