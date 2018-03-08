package ink.moming.travelnote;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ink.moming.travelnote.data.GuidePerference;
import ink.moming.travelnote.unit.BitmapUnit;
import ink.moming.travelnote.unit.NetUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoteUploadActivity extends AppCompatActivity {
    public static final String TAG = NoteUploadActivity.class.getSimpleName();
    AlertDialog loading_dialog;



    private ImageView mUploadImage;
    private String photoPath;
    private Bitmap mBitmap;
    private int defaultImageRes;

    private Button upLoadButton;
    private EditText mUploadText;

    private UploadHandler handler = new UploadHandler(NoteUploadActivity.this);



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int UPDATE_SUCCESS = 34;
    private static final int UPDATE_DEFEATED = 35;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static class UploadHandler extends Handler{

        private Activity mActivity;

        public UploadHandler(Activity activity){
            mActivity=activity;
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_DEFEATED:
                    Toast.makeText(mActivity, "上传失败", Toast.LENGTH_SHORT).show();

                    break;
                case UPDATE_SUCCESS:
                    Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                default:break;

            }
        }

    }

    private static void startIntent(Activity activity, boolean value){
        Intent intent = new Intent();
        intent.putExtra("status",value);
        activity.setResult(80,intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_upload);
        defaultImageRes = R.drawable.ic_addpic;

        mUploadImage = findViewById(R.id.uploadimage);
        mUploadText = findViewById(R.id.content_et);

        mUploadImage.setImageResource(defaultImageRes);
        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(NoteUploadActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                // 选择图片
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 0x1);

            }
        });

        mUploadImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog();
                return true;
            }
        });

        upLoadButton = findViewById(R.id.upload_button);

        upLoadButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (mBitmap!=null){
                    String text=mUploadText.getText().toString();
                    showLoading_dialog();
                    UploadTask upload = new UploadTask(photoPath,text);
                    upload.execute();

                }else {
                    Toast.makeText(NoteUploadActivity.this, "请添加图片", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }




    /*
         * Dialog对话框提示用户删除操作 position为删除图片位置
         */
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteUploadActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mBitmap=null;
                mUploadImage.setImageResource(defaultImageRes);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    protected void showLoading_dialog(){

        final View loading_view = getLayoutInflater().inflate(R.layout.dialog_loading,null);
        AlertDialog.Builder builder= new  AlertDialog.Builder(NoteUploadActivity.this);
        builder.setTitle("上传中...");
        builder.setView(loading_view);
        loading_dialog = builder.create();
        loading_dialog.show();
    }

    private class UploadTask extends AsyncTask<Void,Void,Call>{

        String mImage;
        String mText;

        public UploadTask(String image,String text){
            mImage = image;
            mText = text;
        }

        @Override
        protected Call doInBackground(Void... voids) {
            Context context = NoteUploadActivity.this;

            return NetUnit.uploadNote(context,mText,mImage, GuidePerference.getUserId(context));


        }

        @Override
        protected void onPostExecute(Call call) {
            super.onPostExecute(call);
            loading_dialog.dismiss();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, e.getMessage());
                    Message messageerr = new Message();
                    messageerr.what = UPDATE_DEFEATED;
                    handler.sendMessage(messageerr);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resBody = response.body().string();
                    Message message = new Message();
                    Log.i(TAG, resBody);
                    try {
                        JSONObject jsonObject = new JSONObject(resBody);
                        Log.i(TAG, Integer.toString(jsonObject.getInt("status")));
                        if (Integer.toString(jsonObject.getInt("status")).equals("300") ){

                            message.what = UPDATE_SUCCESS;


                        }else {

                            message.what = UPDATE_DEFEATED;

                        }
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



            finish();
        }
    }


    // 响应startActivityForResult，获取图片路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {

                ContentResolver resolver = getContentResolver();
                try {
                    Uri uri = data.getData();
                    // 这里开始的第二部分，获取图片的路径：
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor cursor = resolver.query(uri, proj, null, null, null);
                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    photoPath = cursor.getString(column_index);
                    Log.d(TAG,photoPath);
                    mBitmap = BitmapUnit.decodeSampledBitmapFromFd(photoPath, 300, 300);
                    mUploadImage.setImageBitmap(mBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }




}
