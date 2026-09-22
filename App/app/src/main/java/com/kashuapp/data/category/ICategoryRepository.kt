package com.kashuapp.data.category



interface ICategoryRepository {


    suspend fun getAllCateg(userId: String): Result<List<Category>>

    suspend fun createCategory(newCat: Category): Result<Category>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    suspend fun updateCategory(cat: Category): Result<Unit>


}


