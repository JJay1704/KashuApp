package com.kashuapp.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object KashuSupaBase {

    val client = createSupabaseClient(
        supabaseUrl = "https://ehvmswrklikwgvkyiskk.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVodm1zd3JrbGlrd2d2a3lpc2trIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk3Nzc3MDgsImV4cCI6MjEwNTM1MzcwOH0.hKNQsIu24KMqyNY8aVyI72U7aby3OyQ4wVIVFoLdRpE"
    ){

        install(Auth)
        install(Postgrest)
    }

    val auth get() = client.auth
    val db get() = client.postgrest


}