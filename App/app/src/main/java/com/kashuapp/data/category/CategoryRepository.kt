package com.kashuapp.data.category

import android.annotation.SuppressLint
import com.kashuapp.data.KashuSupaBase

class CategoryRepository : ICategoryRepository {


    @SuppressLint("SuspiciousIndentation")
    override suspend fun getAllCateg(): Result<List<Category>>{

        try {

            val cateTable = KashuSupaBase.db.from("category")
                .select().decodeList<Category>()
                return Result.success(cateTable)

        } catch (e: Exception) {
            return Result.failure(e)        }

    }

}