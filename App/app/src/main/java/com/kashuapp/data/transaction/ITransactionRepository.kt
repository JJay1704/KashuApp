package com.kashuapp.data.transaction



interface ITransactionRepository {


    suspend fun getAll(userId: String, accountId: String?): Result<List<Transaction>>
    suspend fun postTrans(trans: Transaction): Result<Unit>

    suspend fun updateTrans(trans : Transaction): Result<Unit>



    suspend fun deleteTrans (transId : String):Result <Unit>

}


