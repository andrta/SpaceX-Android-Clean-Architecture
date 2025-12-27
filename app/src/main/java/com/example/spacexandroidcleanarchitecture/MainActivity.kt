package com.example.spacexandroidcleanarchitecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.commit
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.launches.presentation.androidview.LaunchDetailsFragment
import com.example.launches.presentation.androidview.LaunchesFragment
import com.example.launches.presentation.compose.LaunchesScreen
import com.example.spacexandroidcleanarchitecture.ui.theme.SpaceXAndroidCleanArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LaunchesFragment.NavigationListener {
    @Inject
    lateinit var featureFlags: FeatureFlagProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (featureFlags.isComposeEnabled) {
            setupComposeUi()
        } else {
            setupXmlUi(savedInstanceState)
        }
    }

    private fun setupComposeUi() {
        setContent {
            SpaceXAndroidCleanArchitectureTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "launches") {
                    composable("launches") {
                        LaunchesScreen(
                            onNavigateToDetail = { launchId ->
                                navController.navigate("launch_details/$launchId")
                            }
                        )
                    }

                    composable(
                        route = "launch_details/{launchId}",
                        arguments = listOf(navArgument("launchId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val launchId = backStackEntry.arguments?.getString("launchId") ?: ""
                        LaunchDetailsScreen(launchId = launchId)
                    }
                }
            }
        }
    }

    private fun setupXmlUi(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main_legacy)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, LaunchesFragment())
            }
        }
    }

    override fun onNavigateToDetail(id: String) {
        val detailFragment = LaunchDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("LAUNCH_ID", id)
            }
        }

        supportFragmentManager.commit {
            replace(R.id.fragment_container, detailFragment)
            addToBackStack(null)
        }
    }
}

@Composable
fun LaunchDetailsScreen(launchId: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Details for Launch ID: $launchId")
    }
}
