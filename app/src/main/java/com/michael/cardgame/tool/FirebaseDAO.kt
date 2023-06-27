package com.michael.cardgame.tool

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDAO {

    private var db : FirebaseFirestore? = null

    fun init(db:FirebaseFirestore){
        this.db = db
    }

    fun getUserData(email: String,onFirebaseCatchUserDataListener: OnFirebaseCatchUserDataListener) {
        db?.collection("users")
            ?.document(email)
            ?.addSnapshotListener { value, error ->

                if (error != null){
                    Log.i("Poker","getUsers fail : $error")
                    onFirebaseCatchUserDataListener.onError(error.toString())
                    return@addSnapshotListener
                }
                if (value == null || !value.exists()){
                    Log.i("Poker","getUsers is empty")
                    onFirebaseCatchUserDataListener.onNeedToCreateUser()
                    return@addSnapshotListener
                }
                Log.i("Poker","getUsers has data")
                onFirebaseCatchUserDataListener.onCatchUserData(value)

            }
    }




    interface OnFirebaseCatchUserDataListener{
        fun onCatchUserData(value: DocumentSnapshot?)

        fun onError(errorMsg:String)

        fun onNeedToCreateUser()
    }




}