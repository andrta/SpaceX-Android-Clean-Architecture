package com.example.spacexandroidcleanarchitecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.launches.presentation.details.androidview.LaunchDetailsFragment
import com.example.launches.presentation.details.compose.LaunchDetailsScreen
import com.example.launches.presentation.list.androidview.LaunchesFragment
import com.example.launches.presentation.list.compose.LaunchesScreen
import com.example.spacexandroidcleanarchitecture.ui.theme.SpaceXAndroidCleanArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LaunchesFragment.NavigationListener {
    @Inject
    lateinit var featureFlags: FeatureFlagProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
                    ) {
                        LaunchDetailsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
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
                putString("launchId", id)
            }
        }

        supportFragmentManager.commit {
            replace(R.id.fragment_container, detailFragment)
            addToBackStack(null)
        }
    }
}
