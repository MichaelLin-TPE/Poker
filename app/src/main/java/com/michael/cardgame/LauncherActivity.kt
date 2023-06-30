package com.michael.cardgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.databinding.ActivityHomePageBinding
import com.michael.cardgame.dialog.UserPhotoAndNameConfirmDialog
import com.michael.cardgame.home.HomeActivity
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool
import java.lang.Exception

class LauncherActivity : BaseActivity() {

    private lateinit var viewModel: LauncherViewModel
    private lateinit var binding:ActivityHomePageBinding
    private lateinit var oneTapClient : SignInClient
    private lateinit var signInRequest : BeginSignInRequest
    private lateinit var auth : FirebaseAuth

    override fun onStart() {
        super.onStart()
        checkLogin()
    }

    private fun checkLogin() {
        val currentUser = auth.currentUser
        if (currentUser != null){
            Log.i("Poker","已經是登入狀態了 email : ${currentUser.email}")
            binding.signInBtn.visibility = View.GONE
            binding.customerLogin.visibility = View.GONE
            viewModel.onStartFlow(currentUser.email)
        }else{
            binding.signInBtn.visibility = View.VISIBLE
            binding.customerLogin.visibility = View.VISIBLE

            viewModel.checkIsCustomerLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home_page)
        viewModel = getViewModel(LauncherViewModel::class.java)
        auth = Firebase.auth
        handleLiveData()
        initView()
    }

    private fun initView() {
        setGooglePlusButtonText(binding.signInBtn)
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()).build()
        binding.signInBtn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener {
                    try {
                        startIntentSenderForResult(it.pendingIntent.intentSender,666,null,0,0,0,null)
                    }catch (e:Exception){
                        Log.i("Poker","signIn fail $e")
                    }
                }
                .addOnFailureListener {
                    Log.i("Poker","signIn fail $it")
                }
        }
        binding.customerLogin.setOnClickListener {
            viewModel.onShowWarningMsgDialog()
        }
    }

    private fun setGooglePlusButtonText(signInButton: SignInButton){
        for (i in 0 until signInButton.childCount){
            val view = signInButton.getChildAt(i)
            if (view is TextView) {
                view.text = getString(R.string.google)
                break
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            666->{
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    if (idToken != null){
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken,null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this){
                                if (it.isSuccessful){
                                    UserDataTool.saveCustomerLogin(false)
                                    checkLogin()
                                    Log.i("Poker","FirebaseAuth successful token $idToken")
                                }else{
                                    Log.i("Poker","FirebaseAuth fail ${it.exception}")
                                }
                            }
                    }else{
                        Log.i("Poker","get token fail")
                    }


                }catch (e : Exception){
                    Log.i("Poker","onActivityResult fail $e")
                }
            }
        }


    }


    private fun handleLiveData() {
        viewModel.goToHomePageLiveData.observe(this){
            goToPage(HomeActivity::class.java)
            finish()
        }
        viewModel.showSetUpNameAndPhotoLiveData.observe(this){
            val dialog = UserPhotoAndNameConfirmDialog.newInstance()
            dialog.show(supportFragmentManager,"dialog")
            dialog.setOnSetUpUserDataListener{ name,photoId->
                viewModel.onCatchUserData(name,photoId)
            }
        }

        viewModel.showWarningDialogLiveData.observe(this){
            showMessageDialog(getString(R.string.warming),getString(R.string.customer_login_info),getString(R.string.login)){
                viewModel.onMessageConfirmClickListener()
            }
        }

        viewModel.startToDoCustomerCreateLiveData.observe(this){
            if (UserDataTool.getUserPassword().isNotEmpty()){
                UserDataTool.saveCustomerLogin(true)
                checkLogin()
                return@observe
            }
            val email = Tool.getRandomEmail()
            val pwd = Tool.getRandomPassword()
            auth.createUserWithEmailAndPassword(email,pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.i("Poker","訪客登入成功")
                        UserDataTool.saveUserPassword(pwd)
                        UserDataTool.saveCustomerLogin(true)
                        checkLogin()
                    }else{
                        Toast.makeText(
                            this,
                            "LoginFail ${it.exception}",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Log.i("Poker","CreateUser ${it.exception}")
                    }
                }
        }

        viewModel.startToDoCustomerLoginLiveData.observe(this){
            Log.i("Poker","acct : ${it.first} , password : ${it.second}")
            auth.signInWithEmailAndPassword(it.first,it.second)
                .addOnCompleteListener {task->
                    if (task.isSuccessful){
                        checkLogin()
                    }else{
                        Toast.makeText(
                            this,
                            "LoginFail ${task.exception}",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Log.i("Poker","Login ${task.exception}")
                    }
                }
        }
    }
}