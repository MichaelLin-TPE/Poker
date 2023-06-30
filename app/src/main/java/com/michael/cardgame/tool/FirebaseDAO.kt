package com.michael.cardgame.tool

import android.content.Intent
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.LauncherActivity

class FirebaseDAO {

    private var db : FirebaseFirestore? = null

    private var liveUserDataListenerRegistration : ListenerRegistration? = null
    private var onlineUserDataListenerRegistration : ListenerRegistration? = null
    
    
    fun init(db:FirebaseFirestore){
        this.db = db
    }

    fun getUserData(email: String,onFirebaseCatchUserDataListener: OnFirebaseCatchUserDataListener) {
        if (Firebase.auth.currentUser == null){
            return
        }
        db?.collection("users")
            ?.document(email)
            ?.get()
            ?.addOnSuccessListener {
                if (it == null || !it.exists()){
                    onFirebaseCatchUserDataListener.onNeedToCreateUser()
                    return@addOnSuccessListener
                }
                onFirebaseCatchUserDataListener.onCatchUserData(it)
            }
            ?.addOnFailureListener {
                onFirebaseCatchUserDataListener.onError(it.toString())
            }
    }

    fun getLiveUserData(onFirebaseCatchUserDataListener: OnFirebaseCatchUserDataListener){
        if (Firebase.auth.currentUser == null){
            return
        }
        liveUserDataListenerRegistration = db?.collection("users")
            ?.document(UserDataTool.getEmail())
            ?.addSnapshotListener { value, error ->
                if (error != null){
                    onFirebaseCatchUserDataListener.onError(error.toString())
                    return@addSnapshotListener
                }
                if (value == null || !value.exists()){
                    onFirebaseCatchUserDataListener.onError("發生非預期錯誤,請稍後再嘗試")
                    return@addSnapshotListener
                }
                onFirebaseCatchUserDataListener.onCatchUserData(value)
            }
    }


    fun saveUserData(map: HashMap<String, Any>) {
        if (Firebase.auth.currentUser == null){
            return
        }
        db?.collection("users")
            ?.document(UserDataTool.getEmail())
            ?.set(map, SetOptions.merge())
    }

    fun upDateMyCashAmount(userCashAmount: Int) {
        if (Firebase.auth.currentUser == null){
            return
        }
        db?.collection("users")
            ?.document(UserDataTool.getEmail())
            ?.update("cashCount",userCashAmount)
            ?.addOnSuccessListener { Log.i("Poker","updateCashAmountSuccessful")}
            ?.addOnFailureListener { Log.i("Poker","updateCashAmountFail") }
    }

    fun stopConnectFirebase() {
        if (Firebase.auth.currentUser == null){
            return
        }
        db?.collection("onlineUsers")
            ?.document(UserDataTool.getEmail())
            ?.update("isConnected",false)
            ?.addOnSuccessListener { Log.i("Poker","updateCashAmountSuccessful")}
            ?.addOnFailureListener { Log.i("Poker","updateCashAmountFail") }
    }

    fun startToConnectFirebase(){
        if (Firebase.auth.currentUser == null){
            return
        }
        val map = hashMapOf(
            "email" to UserDataTool.getEmail(),
            "isConnected" to true
        )
        db?.collection("onlineUsers")
            ?.document(UserDataTool.getEmail())
            ?.set(map,SetOptions.merge())
    }

    fun setOnCatchOnlineUsersListener(onCatchOnlineUsersListener: OnCatchOnlineUsersListener){
        if (Firebase.auth.currentUser == null){
            return
        }
        onlineUserDataListenerRegistration =  db?.collection("onlineUsers")
            ?.addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty){
                    return@addSnapshotListener
                }
                var userCount = 0
                for (doc in value){
                    Log.i("Poker","email : ${doc.getString("email")} , isConnected : ${doc.getBoolean("isConnected")}")
                    if (doc.getBoolean("isConnected")!!){
                        userCount ++
                    }
                }
                onCatchOnlineUsersListener.onCatchOnlineUsersCount(userCount)
            }
    }

    fun logout(onLogoutFinishListener: OnLogoutFinishListener) {
        db?.collection("onlineUsers")
            ?.document(UserDataTool.getEmail())
            ?.update("isConnected",false)
            ?.addOnSuccessListener {
                Log.i("Poker","updateCashAmountSuccessful")
                if (liveUserDataListenerRegistration != null){
                    liveUserDataListenerRegistration?.remove()
                }
                if (onlineUserDataListenerRegistration != null){
                    onlineUserDataListenerRegistration?.remove()
                }
                Firebase.auth.signOut()
                UserDataTool.saveCustomerLogin(false)
                UserDataTool.saveEmail("")
                UserDataTool.saveUserPassword("")
                onLogoutFinishListener.onFinish()
            }
            ?.addOnFailureListener { Log.i("Poker","updateCashAmountFail") }


    }

    fun interface OnLogoutFinishListener{
        fun onFinish()
    }

    fun interface OnCatchOnlineUsersListener{
        fun onCatchOnlineUsersCount(usersCount:Int)
    }

    interface OnFirebaseCatchUserDataListener{
        fun onCatchUserData(value: DocumentSnapshot)

        fun onError(errorMsg:String)

        fun onNeedToCreateUser()
    }




}