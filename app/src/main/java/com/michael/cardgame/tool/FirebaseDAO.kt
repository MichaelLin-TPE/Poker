package com.michael.cardgame.tool

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.michael.cardgame.bean.AcceptData
import com.michael.cardgame.bean.MessageData
import com.michael.cardgame.bean.RoomData
import com.michael.cardgame.bean.UserData
import java.util.UUID

class FirebaseDAO {

    private var db : FirebaseFirestore? = null

    private var liveUserDataListenerRegistration : ListenerRegistration? = null
    private var onlineUserDataListenerRegistration : ListenerRegistration? = null
    private var onCatchRoomListListenerRegistration : ListenerRegistration? = null
    private var onCatchRoomListenerRegistration : ListenerRegistration? = null
    private var onCatchMessageListRegistration : ListenerRegistration? = null
    
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
                if (onCatchRoomListListenerRegistration != null){
                    onCatchRoomListListenerRegistration?.remove()
                }
                if (onCatchRoomListenerRegistration != null){
                    onCatchRoomListenerRegistration?.remove()
                }
                Firebase.auth.signOut()
                UserDataTool.saveCustomerLogin(false)
                UserDataTool.saveEmail("")
                UserDataTool.saveUserPassword("")
                onLogoutFinishListener.onFinish()
            }
            ?.addOnFailureListener { Log.i("Poker","updateCashAmountFail") }


    }

    fun createGame(gameName: String, bettingValue: Int ,callback:(key:String)->Unit) {
        if (Firebase.auth.currentUser == null){
            return
        }
        val key = UUID.randomUUID().toString()
        val map = hashMapOf(
            "gameName" to gameName,
            "bettingValue" to bettingValue,
            "gameRoomKey" to key,
            "creator" to UserDataTool.getEmail(),
            "usersList" to Gson().toJson(getCurrentUserList()),
            "roomBoss" to UserDataTool.getEmail(),
            "acceptList" to Gson().toJson(getAcceptList())
        )
        db?.collection("gameRooms")
            ?.document(key)
            ?.set(map)
            ?.addOnSuccessListener {
                callback(key)
            }
    }

    private fun getAcceptList(): MutableList<AcceptData> {
        val list = mutableListOf<AcceptData>()
        list.add(AcceptData(UserDataTool.getEmail(),false))
        return list
    }

    private fun getCurrentUserList(): MutableList<UserData> {
        val list = mutableListOf<UserData>()
        list.add(UserData(UserDataTool.getEmail(),UserDataTool.getUserName(),UserDataTool.getUserPhoto(),false))
        return list
    }

    fun onCatchRoomList(callback: (roomList: MutableList<RoomData>) -> Unit) {
        if (Firebase.auth.currentUser == null){
            return
        }
        onCatchRoomListListenerRegistration =  db?.collection("gameRooms")
            ?.addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty){
                    callback(mutableListOf())
                    return@addSnapshotListener
                }
                val list = mutableListOf<RoomData>()
                for (doc in value){
                    if (doc.getString("usersList").isNullOrEmpty() ||
                        doc.getString("gameName").isNullOrEmpty() ||
                        doc.getLong("bettingValue") == null ||
                        doc.getString("gameRoomKey").isNullOrEmpty()){
                        callback(mutableListOf())
                        continue
                    }
                    val userList = Gson().fromJson<MutableList<UserData>>(doc.getString("usersList")!!,object : TypeToken<MutableList<UserData>>(){}.type)
                    val data = RoomData(doc.getString("gameName")!!,
                        doc.getLong("bettingValue")!!.toInt(),
                        doc.getString("gameRoomKey")!!,
                        userList.size,
                        userList)
                    list.add(data)
                }
                callback(list)
            }
    }

    fun onCatchRoomData(key: String , callback: (roomData: Map<String,Any>) -> Unit) {
        if (Firebase.auth.currentUser == null){
            return
        }
        onCatchRoomListenerRegistration =  db?.collection("gameRooms")
            ?.document(key)
            ?.addSnapshotListener { value, error ->
                if (error != null){
                    Log.i("Poker","error : $error")
                    return@addSnapshotListener
                }
                if (value == null){
                    Log.i("Poker","沒資料")
                    return@addSnapshotListener
                }
                if (value.getString("gameName").isNullOrEmpty()){
                    Log.i("Poker","抓不到遊戲名稱")
                    return@addSnapshotListener
                }
                val map : Map<String,Any> = value.data!!
                callback(map)
            }
    }

    fun removeLiveCatch() {
        if (onCatchRoomListenerRegistration != null){
            onCatchRoomListenerRegistration?.remove()
        }
        if (onCatchMessageListRegistration != null){
            onCatchMessageListRegistration?.remove()
        }
    }

    fun removeRoom(key: String,callback: () -> Unit) {
        db?.collection("gameRooms")
            ?.document(key)
            ?.delete()
            ?.addOnSuccessListener {

            }
        val chatRoom = db?.collection("chatRooms")
        val doc = chatRoom?.document(key)
        val collection = doc?.collection(key)
        collection?.get()?.addOnCompleteListener {
            if (it.isSuccessful){
                val batch = db?.batch()
                for (document in it.result){
                    batch?.delete(document.reference)
                }
                batch?.commit()?.addOnCompleteListener {
                    doc.delete()
                }
                callback()
            }
        }
    }

    fun onCatchChatList(key:String,callback: (msgList: MutableList<MessageData>) -> Unit){
        if (Firebase.auth.currentUser == null){
            return
        }
        onCatchMessageListRegistration = db?.collection("chatRooms")
            ?.document(key)
            ?.collection(key)
            ?.addSnapshotListener { value, error ->
                if (error != null){
                    Log.i("Poker","error : $error")
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty){
                    Log.i("Poker","沒資料")
                    return@addSnapshotListener
                }
                val list = mutableListOf<MessageData>()
                for (doc in value){
                    if (doc.getString("name").isNullOrEmpty() ||
                        doc.getString("msg").isNullOrEmpty()){
                        continue
                    }
                    list.add(MessageData(doc.getString("msg")!!,doc.getString("name")!!,doc.getLong("time")!!))
                }
                callback(list)
            }

    }

    fun sendMsg(msg: String, key: String) {
        val map = hashMapOf(
            "name" to UserDataTool.getUserName(),
            "msg" to msg,
            "time" to System.currentTimeMillis()
        )
        db?.collection("chatRooms")
            ?.document(key)
            ?.collection(key)
            ?.document()
            ?.set(map, SetOptions.merge())
    }

    fun updateRoomUserList(key: String, userList: MutableList<UserData>,callback: () -> Unit) {
        Log.i("Poker","key $key , userList : ${Gson().toJson(userList)}")
        val json = Gson().toJson(userList)
        db?.collection("gameRooms")
            ?.document(key)
            ?.update("usersList",json)
            ?.addOnSuccessListener {
                Log.i("Poker","updateRoomSuccessful")
                callback()
            }
            ?.addOnFailureListener { Log.i("Poker","updateRoomFail") }
    }

    fun updateAcceptList(key: String, acceptList: MutableList<AcceptData>) {
        val json = Gson().toJson(acceptList)
        db?.collection("gameRooms")
            ?.document(key)
            ?.update("acceptList",json)
            ?.addOnSuccessListener {
                Log.i("Poker","updateRoomSuccessful")

            }
            ?.addOnFailureListener { Log.i("Poker","updateRoomFail") }
    }

    fun updateRoomBoss(key: String, email: String) {
        db?.collection("gameRooms")
            ?.document(key)
            ?.update("roomBoss",email)
            ?.addOnSuccessListener {
                Log.i("Poker","updateRoomSuccessful")
            }
            ?.addOnFailureListener { Log.i("Poker","updateRoomFail") }
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