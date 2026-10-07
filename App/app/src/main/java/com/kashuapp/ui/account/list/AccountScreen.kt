package com.kashuapp.ui.account.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.ui.account.form.AccountForm

import com.kashuapp.ui.theme.KashuTheme

@Composable
fun AccountScreen(
    onBackToHome: () -> Unit,
    viewModel: AccountVM = viewModel(),

    ) {


    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = KashuTheme.colors.mainBackground, topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = KashuTheme.colors.title
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Accounts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                }

                IconButton(
                    onClick = { viewModel.openForm()},
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(KashuTheme.colors.mainColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva cuenta",
                        tint = Color.Black
                    )
                }
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            if (uiState.isLoading && uiState.accounts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KashuTheme.colors.mainColor)
                }
            } else if (uiState.accounts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "There's no accounts created",
                        color = KashuTheme.colors.subtitle,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
//                    items(uiState.accounts, key = { it.id?: it.name }) { category ->
//                        AccountItemCard(
//                            account= Account,
//                            onEditClick = { viewModel.openEdit(category) },
//                            onDeleteClick = { category.id?.let { viewModel.deleteAccount(it) } }
//                        )
//                    }
                }
            }
        }

        if (uiState.isFormOpen) {
            AccountForm(

                accountToEdit = uiState.accountToEdit,


                onDismiss = { viewModel.closeForm() },

                onDelete = {
//                    id ->
//                    viewModel.deleteCategory(id)
                })
        }
    }
}
