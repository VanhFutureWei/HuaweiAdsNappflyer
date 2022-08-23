/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurewei.hmsadappfly

import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.futurewei.hmsadappfly.ui.navigation.HmsNewsDestinations
import com.futurewei.hmsadappfly.ui.navigation.HmsNewsNav
import com.futurewei.hmsadappfly.ui.theme.HmsAdAppflyTheme
import com.futurewei.hmsadappfly.utils.AppContainer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.DrawerValue


import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.futurewei.hmsadappfly.ui.AppDrawer
import kotlinx.coroutines.launch

@Composable
fun HmsNewsApp(appContainer: AppContainer, widthSizeClass: WindowWidthSizeClass){
    HmsAdAppflyTheme {
        val sysUiCtrl = rememberSystemUiController()
        val darkIcons = MaterialTheme.colors.isLight
        SideEffect { sysUiCtrl.setSystemBarsColor(Color.Transparent,darkIcons=darkIcons) }
        val navCtrl = rememberNavController()
        val navAction = remember(navCtrl) { HmsNewsNav(navCtrl) }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navCtrl.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: HmsNewsDestinations.HOME_ROUTE

        val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

        ModalDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = navAction.navigateToHome,
                    navigateToInterests = navAction.navigateToInterests,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                )
            },
            drawerState = sizeAwareDrawerState,
            // Only enable opening the drawer via gestures if the screen is not expanded
            gesturesEnabled = !isExpandedScreen
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .windowInsetsPadding(
                        WindowInsets
                            .navigationBars
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    )
            ) {
                if (isExpandedScreen) {
                    AppNavRail(
                        currentRoute = currentRoute,
                        navigateToHome = navAction.navigateToHome,
                        navigateToInterests = navAction.navigateToInterests,
                    )
                }
                HmsNewsNavGraph(
                    appContainer = appContainer,
                    isExpandedScreen = isExpandedScreen,
                    navController = navCtrl,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }
}


/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}

/**
 * Determine the content padding to apply to the different screens of the app
 */
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp) =
    WindowInsets.systemBars
        .only(WindowInsetsSides.Bottom)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()