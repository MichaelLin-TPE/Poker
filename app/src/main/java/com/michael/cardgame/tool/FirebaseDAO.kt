package com.michael.cardgame.tool

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirebaseDAO {

    private var db : FirebaseFirestore? = null

    fun init(db:FirebaseFirestore){
        this.db = db
    }

    fun getUserData(email: String,onFirebaseCatchUserDataListener: OnFirebaseCatchUserDataListener) {
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
        db?.collection("users")
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
        db?.collection("users")
            ?.document(UserDataTool.getEmail())
            ?.set(map, SetOptions.merge())
    }

    fun upDateMyCashAmount(userCashAmount: Int) {
        db?.collection("users")
            ?.document(UserDataTool.getEmail())
            ?.update("cashCount",userCashAmount)
            ?.addOnSuccessListener { Log.i("Poker","updateCashAmountSuccessful")}
            ?.addOnFailureListener { Log.i("Poker","updateCashAmountFail") }
    }


    interface OnFirebaseCatchUserDataListener{
        fun onCatchUserData(value: DocumentSnapshot)

        fun onError(errorMsg:String)

        fun onNeedToCreateUser()
    }




}