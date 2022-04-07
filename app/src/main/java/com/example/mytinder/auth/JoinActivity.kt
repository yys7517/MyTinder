package com.example.mytinder.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.mytinder.MainActivity
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityJoinBinding
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinBinding
    private lateinit var auth : FirebaseAuth

    private val TAG = JoinActivity::class.java.simpleName

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    // FCM 토큰 => token 정보도 유저 정보에 추가하자.
    private var token = ""

    // 사용가능한 닉네임을 저장할 nickTemp => 최종 회원가입 시, 사용가능한 닉네임인지 확인하기 위해 변수에 담아놓자.
    private var nickTemp = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_join )

        // FCM 토큰은 유저를 식별하기 위한 것이 아닌, 특정 디바이스를 식별하기 위한 것. 로그아웃 후에도 토큰이 남아있다.
        // 토큰을 언제 삭제해야 할까 ?

        // 기기마다 회원가입을 한 번만 하게끔 제약을 걸어야할까 ?

        // 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result.toString()
            }
            else {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Log.d("사용자 Token", token.toString() )
            // Toast.makeText(baseContext, token.toString() , Toast.LENGTH_SHORT).show()
        })

        val rgGender = binding.rgGender
        val rbMale = binding.rbMale
        val rbFemale = binding.rbFemale

        auth = Firebase.auth

        var isNickChecked = false
        var isProfileImageUploaded = false

        // 갤러리에서 이미지 가져와서 이미지 뷰에 설정하기.
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.profileImageArea.setImageURI( uri )
            }
        )
        // 프로필 이미지 설정하기
        binding.profileImageArea.setOnClickListener {
            // 핸드폰 이미지 가져오기
            getAction.launch("image/*")
            isProfileImageUploaded = true
        }

        // 닉네임 중복 확인
        binding.btnNickCheck.setOnClickListener {

            nickname = binding.nickArea.text.toString()

            if ( nickname.isEmpty() ) {
                Toast.makeText(baseContext, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            else {
                FirebaseRef.userInfoRef.orderByChild("nickname").equalTo(nickname)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                // 닉네임 중복 없음
                                isNickChecked = true

                                Toast.makeText(
                                    applicationContext,
                                    "사용할 수 있는 닉네임입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 중복확인 후, 닉네임이 변경됬을 때
                                // 중복이 안되는 닉네임인지 확인하기 위함.
                                nickTemp = nickname


                            } else {
                                isNickChecked = false
                                Toast.makeText(baseContext, "중복된 닉네임 입니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(
                                applicationContext,
                                databaseError.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // 회원가입 완료 버튼을 눌렀을 때
        binding.btnJoin.setOnClickListener {

            val email = binding.emaliArea.text.toString()
            val password = binding.passArea.text.toString()
            val passCheck = binding.passCheckArea.text.toString()

            nickname = binding.nickArea.text.toString()
            city = binding.cityArea.text.toString()
            age = binding.ageArea.text.toString()
            uid = auth.currentUser?.uid.toString()

            var isGoToJoin  = true


            // 유효성 검사 진행..
            // 비밀번호 확인, 닉네임 중복확인 등.
            when {
                email.isEmpty() -> { // 이메일이 비어있다면
                    Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                password.isEmpty() -> { // 비밀번호가 비어있다면
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                password.length < 6 -> { // 비밀번호의 길이가 6보다 짧다면
                    Toast.makeText(this, "비밀번호를 더 길게 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                    binding.passArea.text?.clear()
                }

                passCheck.isEmpty() -> { // 비밀번호 확인 값이 비어있다면
                    Toast.makeText(this, "비밀번호를 한 번 더 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                ! password.equals( passCheck ) -> { // 비밀번호가 일치하지 않는다면
                    isGoToJoin = false
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    binding.passCheckArea.text?.clear()
                }

                nickname.isEmpty() -> {
                    Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                age.isEmpty() -> {
                    Toast.makeText(this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                city.isEmpty() -> {
                    Toast.makeText(this, "지역을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                ! isNickChecked -> {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }

                // 중복확인 된 닉네임이 또 다시 변경됬을 때
                nickTemp != nickname -> {
                    isNickChecked = false
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }

                ! isProfileImageUploaded -> {
                    Toast.makeText(this, "프로필 사진을 선택하세요.", Toast.LENGTH_SHORT).show()
                }


                isGoToJoin && isNickChecked && isProfileImageUploaded-> {
                    // 회원가입 진행
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                uid = auth.currentUser?.uid.toString()

                                // 회원정보 DB에 저장.
                                FirebaseRef.userInfoRef
                                    .child(uid)
                                    .setValue( UserInfoModel( uid, nickname, gender, city, age, token )  )

                                // 회원 프로필 사진 업로드
                                uploadImage( uid )

                                Toast.makeText(this, "회원가입 되었습니다.",Toast.LENGTH_SHORT).show()

                                val intent = Intent( this, MainActivity::class.java )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)

                            }
                        }
                }
            }
        }
    }

    // 이미지 Storage 에 업로드
    private fun uploadImage( uid : String ) {
        val imageView = binding.profileImageArea

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = FirebaseRef.storageRef.child("${uid}.png").putBytes(data)
        uploadTask.
        addOnFailureListener {

        }.
        addOnSuccessListener { taskSnapshot ->

        }

    }

    // 성별 라디오 버튼 리스너
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbMale ->
                    if (checked) {
                        gender = "남"
                    }
                R.id.rbFemale ->
                    if (checked) {
                        gender = "여"
                    }
            }
        }
    }
}