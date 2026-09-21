package com.kashuapp.data.category

interface ICategoryRepository {



    suspend fun getAllCateg (): Result<List<Category>>
//    suspend fun postCategory ()






}


