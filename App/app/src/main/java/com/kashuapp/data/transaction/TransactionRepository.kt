package com.kashuapp.data.transaction

import android.annotation.SuppressLint
import com.kashuapp.data.KashuSupaBase
import com.kashuapp.data.category.Category
import io.github.jan.supabase.postgrest.result.PostgrestResult
import java.sql.Timestamp


class TransactionRepository : ITransactionRepository{


    @SuppressLint("SuspiciousIndentation")
    override suspend fun getAll(): Result<List<Transaction>> {

         try {


          val tableTrans =   KashuSupaBase.db.from("transaction")
              .select ()
              .decodeList<Transaction>()



             return  Result.success(tableTrans)

        } catch (e: Exception) {

             return Result.failure(e)

        }


    }


    override suspend fun postTrans(

        trans: Transaction

    ): Result<PostgrestResult> {

        try {

            val insertTrans =
                KashuSupaBase.db.from("transaction")
                                .insert((trans)
                                     )
            return Result.success(insertTrans)


        }catch (e : Exception){

            return   Result.failure(e)
        }



    }




}


