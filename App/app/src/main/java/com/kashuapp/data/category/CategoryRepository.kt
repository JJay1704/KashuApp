package com.kashuapp.data.category


import com.kashuapp.data.KashuSupaBase
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class CategoryRepository : ICategoryRepository {
    override suspend fun getAllCateg(userId: String): Result<List<Category>> {
        try {
            val cateTable = KashuSupaBase.db.from("category").select().decodeList<Category>()
            val filtered = cateTable.filter { category ->
                category.isDefault || category.userId == null || category.userId == userId
            }


            return Result.success(filtered)

        } catch (e: Exception) {
            return Result.failure(e)
        }

    }



    override suspend fun createCategory(newCat: Category): Result<Category> {
        return try {
            val json = kotlinx.serialization.json.buildJsonObject {
                newCat.userId?.let { put("user_id", it) }
                put("name", newCat.name)
                put("type", newCat.type)
                put("icon", if (newCat.icon.isBlank()) "category" else newCat.icon)
                put("color", if (newCat.color.isBlank()) "#34D399" else newCat.color)
                put("is_default", false)
            }
            val response = KashuSupaBase.db.from("category")
                .insert(json) {
                    select()
                }
                .decodeSingle<Category>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun updateCategory(cat: Category): Result<Unit> {
        return try {
            val json = kotlinx.serialization.json.buildJsonObject {
                put("name", cat.name)
                put("type", cat.type)
                put("icon", if (cat.icon.isBlank()) "category" else cat.icon)
                put("color", if (cat.color.isBlank()) "#34D399" else cat.color)
            }
            KashuSupaBase.db.from("category").update(json) {
                filter {
                    eq("id", cat.id ?: "")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            KashuSupaBase.db.from("category").delete {
                filter {
                    eq("id", categoryId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }








}