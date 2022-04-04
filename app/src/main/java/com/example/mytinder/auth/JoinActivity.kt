package com.example.mytinder.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.mytinder.MainActivity
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityJoinBinding
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinBinding
    private lateinit var auth : FirebaseAuth

    private val TAG = JoinActivity::class.java.simpleName

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    private var nickTemp = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_join )

        auth = Firebase.auth
        var isNickChecked = false

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
        }

        binding.btnNickCheck.setOnClickListener {
            // 닉네임 중복 확인
            nickname = binding.nickArea.text.toString()

            FirebaseRef.userInfoRef.orderByChild("nickname").equalTo( nickname ).addListenerForSingleValueEvent( object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // 닉네임 중복 없음
                        isNickChecked = true

                        Toast.makeText(applicationContext,
                            "사용할 수 있는 닉네임입니다.",
                            Toast.LENGTH_SHORT).show()

                        // 중복확인 후, 닉네임이 변경됬을 때
                        // 중복이 안되는 닉네임인지 확인하기 위함.
                        nickTemp = nickname


                    } else {
                        isNickChecked = false
                        Toast.makeText( baseContext, "중복된 닉네임 입니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(applicationContext,
                        databaseError.message,
                        Toast.LENGTH_SHORT).show()
                }
            })
        }


        // 회원가입 완료 버튼을 눌렀을 때
        binding.btnJoin.setOnClickListener {

            val email = binding.emaliArea.text.toString()
            val password = binding.passArea.text.toString()
            val passCheck = binding.passCheckArea.text.toString()

            nickname = binding.nickArea.text.toString()
            gender = binding.genderArea.text.toString()
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

                ! isNickChecked -> {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }

                // 중복확인 된 닉네임이 또 다시 변경됬을 때
                nickTemp != nickname -> {
                    isNickChecked = false
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }


                isGoToJoin && isNickChecked -> {
                    // 회원가입 진행
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                uid = auth.currentUser?.uid.toString()

                                // 회원정보 DB에 저장.
                                FirebaseRef.userInfoRef
                                    .child(uid)
                                    .setValue( UserInfoModel( uid, nickname, gender, city, age)  )

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
}