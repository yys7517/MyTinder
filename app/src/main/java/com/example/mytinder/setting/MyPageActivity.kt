package com.example.mytinder.setting

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.auth.IntroActivity
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.databinding.ActivityMyPageBinding
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyPageBinding
    private lateinit var auth : FirebaseAuth

    private val TAG = MyPageActivity::class.java.simpleName

    private lateinit var pastData : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_my_page )

        auth = Firebase.auth

        // 마이페이지 정보 불러오기
        getMyUserData()

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {

            auth.signOut()
            val intent = Intent( this, IntroActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            Toast.makeText(this, "로그아웃 되었습니다.",Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 갤러리에서 이미지 가져와서 이미지 뷰에 설정하기.
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->

                binding.myImage.setImageURI( uri )

                // 프로필 사진 업로드
                val myuid = FirebaseAuthUtils.getMyUid()

                uploadImage( myuid )

                // Firebase Storage uploadTask 를 실패 상황을 만들 지 못하였으나
                // 아래와 같은 코드를 작성 시 기존 이미지 데이터로 유지가 됨을 확인하였음.
                // binding.myImage.setImageBitmap( pastData )   // 실패 시 기존 데이터로 변경.

            }
        )

        // 프로필 사진 변경하기
        binding.myImage.setOnClickListener {

            val mDialogView = LayoutInflater.from( this ).inflate( R.layout.custom_yes_no_dialog , null )
            val mBuilder = AlertDialog.Builder( this )
                .setView( mDialogView )


            val mAlertDialog = mBuilder.show()

            mAlertDialog.findViewById<TextView>(R.id.txtQuestion).text = "프로필 사진을 변경하시겠습니까?"
            // '예' 버튼 클릭 시
            mAlertDialog.findViewById<Button>(R.id.btnSubmit).setOnClickListener {

                // 기존 프로필 이미지 데이터 저장해두기.
                // 실패 시 -> 기존 이미지로 다시 세팅.
                val imageView = binding.myImage

                imageView.isDrawingCacheEnabled = true
                imageView.buildDrawingCache()
                pastData = (imageView.drawable as BitmapDrawable).bitmap

                mAlertDialog.dismiss()

                // 핸드폰 이미지 가져온 후, 이미지 파일 Firebase Storage 업로드
                getAction.launch("image/*")
            }

            // '아니오' 버튼 클릭 시
            mAlertDialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {

                mAlertDialog.dismiss()

            }
        }

    }

    // 이미지 Storage 에 업로드
    private fun uploadImage( uid : String ) {

        val imageView = binding.myImage

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val mDialogView = LayoutInflater.from( this ).inflate( R.layout.progressbar_dialog , null )
        val mBuilder = AlertDialog.Builder( this )
            .setView( mDialogView )

        val uploadTask = FirebaseRef.storageRef.child("${uid}.png").putBytes(data)

        val mAlertDialog = mBuilder.show()

        mAlertDialog.findViewById< TextView >( R.id.txtProgress ).text = "프로필 사진 변경 중.."

        mAlertDialog.setCanceledOnTouchOutside( false )     // 바깥 영역 터치 시 Cancel 되지 않도록

        uploadTask.addOnSuccessListener {
                Toast.makeText(this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT ).show()
                mAlertDialog.dismiss()
            }
            .addOnFailureListener {
                binding.myImage.setImageBitmap( pastData )   // 실패 시 기존 데이터로 변경.

                Toast.makeText(this, "프로필 사진 변경이 실패하였습니다.", Toast.LENGTH_SHORT ).show()
                mAlertDialog.dismiss()
            }
            .addOnCanceledListener {
                binding.myImage.setImageBitmap( pastData )   // 실패 시 기존 데이터로 변경.

                Toast.makeText(this, "프로필 사진 변경이 실패하였습니다.", Toast.LENGTH_SHORT ).show()
                mAlertDialog.dismiss()
            }.addOnPausedListener {
                binding.myImage.setImageBitmap( pastData )   // 실패 시 기존 데이터로 변경.

                Toast.makeText(this, "프로필 사진 변경이 실패하였습니다.", Toast.LENGTH_SHORT ).show()
                mAlertDialog.dismiss()
            }

    }

    // 내 UID를 통해 내 User 정보를 가져오자.
    private fun getMyUserData() {


        val mDialogView = LayoutInflater.from( this ).inflate( R.layout.progressbar_dialog , null )
        val mBuilder = AlertDialog.Builder( this )
            .setView( mDialogView )

        // 가져올 경로
        val myUserInfoRef = FirebaseRef.userInfoRef.child( FirebaseAuthUtils.getMyUid() )

        val mAlertDialog = mBuilder.show()

        mAlertDialog.findViewById< TextView >( R.id.txtProgress ).text = "내 정보 불러오는 중.."

        // Read from the database
        myUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d( TAG, dataSnapshot.toString() )
                val myUserInfo = dataSnapshot.getValue(UserInfoModel::class.java)

                binding.myUid.text = myUserInfo?.uid.toString()
                binding.myNickname.text = myUserInfo?.nickname.toString()
                binding.myAge.text = "${myUserInfo?.age}세"
                binding.myCity.text = myUserInfo?.city.toString()
                binding.myGender.text = "${myUserInfo?.gender}성"

                val profileImageRef = FirebaseRef.storageRef.child("${myUserInfo?.uid}.png")

                profileImageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                    if( task.isSuccessful ) {
                        Glide.with( baseContext )
                            .load(task.result)
                            .into( binding.myImage )
                    }
                })

                Handler().postDelayed( {

                    mAlertDialog.dismiss()

                },1500)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
}