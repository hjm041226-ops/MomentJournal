package com.momentjournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.momentjournal.MomentJournalApp
import com.momentjournal.data.repository.RecordRepository
import com.momentjournal.data.repository.TagRepository
import com.momentjournal.ui.detail.DetailScreen
import com.momentjournal.ui.detail.DetailViewModel
import com.momentjournal.ui.editor.EditorScreen
import com.momentjournal.ui.editor.EditorViewModel
import com.momentjournal.ui.home.HomeScreen
import com.momentjournal.ui.tag.TagManageScreen
import com.momentjournal.ui.tag.TagViewModel
import com.momentjournal.ui.theme.AppThemeType
import com.momentjournal.ui.theme.ThemePickerScreen

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{recordId}"
    const val DETAIL = "detail/{recordId}"
    const val TAG_MANAGE = "tag_manage"
    const val THEME_PICKER = "theme_picker"

    fun editor(recordId: Long = -1) = "editor/$recordId"
    fun detail(recordId: Long) = "detail/$recordId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    themeType: AppThemeType,
    onThemeChange: (AppThemeType) -> Unit
) {
    val app = LocalContext.current.applicationContext as MomentJournalApp

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(
            Routes.EDITOR,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
            val repository = RecordRepository(
                app.database.recordDao(),
                app.database.blockDao(),
                app.database.recordTagDao()
            )
            EditorScreen(
                navController = navController,
                viewModel = viewModel(
                    factory = EditorViewModel.Factory(repository, recordId)
                )
            )
        }

        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: return@composable
            val repository = RecordRepository(
                app.database.recordDao(),
                app.database.blockDao(),
                app.database.recordTagDao()
            )
            DetailScreen(
                recordId = recordId,
                navController = navController,
                viewModel = viewModel(
                    factory = DetailViewModel.Factory(recordId, repository)
                )
            )
        }

        composable(Routes.TAG_MANAGE) {
            val repository = TagRepository(app.database.tagDao())
            TagManageScreen(
                navController = navController,
                viewModel = viewModel(factory = TagViewModel.Factory(repository))
            )
        }

        composable(Routes.THEME_PICKER) {
            ThemePickerScreen(
                currentTheme = themeType,
                onThemeSelected = { onThemeChange(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
