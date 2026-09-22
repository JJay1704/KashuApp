package com.kashuapp.data.transaction

import io.github.jan.supabase.postgrest.result.PostgrestResult

interface ITransactionRepository {


    suspend fun getAll(): Result<List<Transaction>>
    suspend fun postTrans(trans: Transaction): Result<Unit>


}


