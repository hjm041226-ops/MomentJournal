package com.momentjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.momentjournal.ui.navigation.NavGraph
import com.momentjournal.ui.theme.MomentJournalTheme
import com.momentjournal.ui.theme.rememberAppThemeType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeType = rememberAppThemeType()
            MomentJournalTheme(themeType = themeType.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        themeType = themeType.value,
                        onThemeChange = { themeType.value = it }
                    )
                }
            }
        }
    }
}
