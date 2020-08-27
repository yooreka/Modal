package com.example.broadcastreceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.InputStream;

public class BasicAppShareActivity extends AppCompatActivity implements AutoPermissionsListener {
     TextView lblContacts;
     ImageView imgGallery;

     Button btnContacts, btnGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_app_share);

        //뷰찾아오기
        lblContacts = (TextView)findViewById(R.id.lblContacts);
        imgGallery = (ImageView)findViewById(R.id.imgGallery);
        btnContacts = (Button)findViewById(R.id.btnContacts);
        btnGallery = (Button)findViewById(R.id.btnGallery);
        //연락처 버튼을 눌럿을 때 수행할 코드
        btnContacts.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //연락처 인텐트 생성
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                //연락처 출력
                startActivityForResult(intent, 10);
            }
        });
        //이미지 버튼을 눌렀을 때 수행할 내용을 작성
        btnGallery.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //사진 앱 전부 호출
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 20);
            }
        });
        //시작하자마자 필요한 권한 요청
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //이미지를 선택했을 때 수행할 내용
        if(requestCode == 20 && resultCode == RESULT_OK){
            Uri fileUri = data.getData();
            ContentResolver resolver = getContentResolver();
            try{
                InputStream inputStream = resolver.openInputStream(fileUri);
                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgGallery.setImageBitmap(imgBitmap);
                inputStream.close();
            }catch(Exception e){
                Log.e("이미지 가져오기 예외", e.getMessage());
            }
        }
        //연락처가 없어졌을 때 수행할 내용
        if(requestCode == 10 && resultCode == RESULT_OK){
            try{
                //선택한 연락처 가져오기
                String id = Uri.parse(data.getDataString()).getLastPathSegment();
                //id를 가지고 연락처 가져오기
                Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.Data._ID + "=" + id, null, null);
                cursor.moveToNext();
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                lblContacts.setText(name + ":" + phone);
                lblContacts.setTextSize(20);
            }catch(Exception e){
                Log.e("연락처 예외", e.getMessage());
            }
        }
    }
    //권한을 거부할 때 호출되는 메소드 : AutoPermissionListener의 메소드
    @Override
    public void onDenied(int i, String[] strings) {
        Toast.makeText(this, "권한 사용을 하지 않으면 기능을 사용 못함", Toast.LENGTH_LONG).show();
    }
   //권한을 허용했을 대 호출되는 메소드
    @Override
    public void onGranted(int i, String[] strings) {
     Toast.makeText(this, "권한 사용을 허용 하셨습니다.", Toast.LENGTH_LONG).show();
    }
    //권한 요청을 하고 권한에 대한 응답을 했을 때 호출되는 메소드
    //Activity의 메소드
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int [] grantResults){
        //상위 클래스의 메소드 호출
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //권한 요청 결과를 AutoPermission에 전송해서 메소드를 호출하도록 해줌
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
}