package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NovelViewModel
import com.example.ui.workspace.ProjectWorkspaceScreen

class MainActivity : ComponentActivity() {

    private val viewModel: NovelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NovelApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NovelApp(viewModel: NovelViewModel) {
    val currentProjectId by viewModel.currentProjectId.collectAsState()

    if (currentProjectId == null) {
        HomeScreen(
            viewModel = viewModel,
            onOpenProject = { projectId ->
                viewModel.selectProject(projectId)
            }
        )
    } else {
        ProjectWorkspaceScreen(
            viewModel = viewModel,
            onBackToHome = {
                viewModel.closeProject()
            }
        )
    }
}
