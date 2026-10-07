package com.kashuapp.data.currency


interface ICurrencyRepository {


    suspend fun getAllCurrency(): Result<List<Currency>>

}


