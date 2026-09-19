package com.kashuapp.core.navBar

sealed class HomeTab {

    object home : HomeTab()
    object goals : HomeTab()
    object transaction : HomeTab()

}