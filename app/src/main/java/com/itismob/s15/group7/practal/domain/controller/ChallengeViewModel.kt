package com.itismob.s15.group7.practal.domain.controller

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.itismob.s15.group7.practal.domain.model.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChallengeViewModel : ViewModel() {
    private var _ChallengeList = MutableStateFlow<List<Challenge>>(emptyList())
    var challengeList = _ChallengeList.asStateFlow()


    private val db = Firebase.firestore

    init {
        getChallengeList()
    }


    fun getChallengeList() {
        db.collection("challenges")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                if (value != null) { _ChallengeList.value = value.toObjects(Challenge::class.java) }
            }
    }

}
