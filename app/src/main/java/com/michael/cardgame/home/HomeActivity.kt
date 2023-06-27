package com.michael.cardgame.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.LauncherActivity
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.big_two.BigTwoActivity
import com.michael.cardgame.databinding.ActivityHomeBinding
import com.michael.cardgame.tool.SpeechTool

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        viewModel = getViewModel(HomeViewModel::class.java)

        SpeechTool.init(this)
        handleLiveData()
        initView()

        val db = Firebase.firestore
//        val user = hashMapOf(
//            "first" to "Joyce",
//            "last" to "Lin",
//            "born" to 1988
//        )
//        db.collection("users")
//            .document("Joyce")
//            .set(user, SetOptions.merge())
//        db.collection("users")
//            .document("Joyce")
//            .set(user)
//            .addOnSuccessListener {
//                Log.i("Poker","add user successful")
//            }
//            .addOnFailureListener {
//                Log.i("Poker","Error adding document : $it")
//            }
        db.collection("users")
            .document("Joyce")
            .addSnapshotListener { snapshot, error ->
                if (error != null){
                    Log.i("Poker","snapShot is error :$error")
                    return@addSnapshotListener
                }
                if (snapshot == null || !snapshot.exists()){
                    Log.i("Poker","snapShot doesn't exists")
                    return@addSnapshotListener
                }
                Log.i("Poker","last name : ${snapshot.getString("last")}")


            }



    }

    private fun initView() {
        binding.ivBigTwo.setOnClickListener {
            goToPage(BigTwoActivity::class.java)
        }
        binding.tvLogout.setOnClickListener {
            Firebase.auth.signOut()
            goToPage(LauncherActivity::class.java)
            finish()
        }
    }

    private fun handleLiveData() {
        viewModel.showUserCashAmountLiveData.observe(this){
            binding.tvUserCash.text = it
        }
        viewModel.showUserDiamondCountLiveData.observe(this){
            binding.tvUserDiamond.text = it
        }
        viewModel.showUserNameLiveData.observe(this){
            binding.tvUserName.text = it
        }
        viewModel.showUserPhotoLiveData.observe(this){
            binding.ivUserPhoto.setImageResource(it)
        }
    }
}