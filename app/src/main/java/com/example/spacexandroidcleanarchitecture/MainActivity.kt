package com.example.spacexandroidcleanarchitecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.commit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.launches.presentation.androidview.LaunchDetailsFragment
import com.example.launches.presentation.androidview.LaunchesListFragment
import com.example.launches.presentation.compose.LaunchesScreen
import com.example.spacexandroidcleanarchitecture.ui.theme.SpaceXAndroidCleanArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LaunchesListFragment.OnLaunchClickedListener {

    @Inject
    lateinit var featureFlags: FeatureFlagProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (featureFlags.isComposeEnabled) {
            setContent {
                SpaceXAndroidCleanArchitectureTheme {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "launches") {
                        composable("launches") {
                            LaunchesScreen(onLaunchClick = {
                                navController.navigate("launch_details")
                            })
                        }
                        composable("launch_details") {
                            LaunchDetailsScreen()
                        }
                    }
                }
            }
        } else {
            setContentView(R.layout.activity_main_legacy)

            if (savedInstanceState == null) {
                supportFragmentManager.commit {
                    replace(R.id.fragment_container, LaunchesListFragment())
                }
            }
        }
    }

    override fun onLaunchClicked() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, LaunchDetailsFragment())
            addToBackStack(null)
        }
    }
}

@Composable
fun LaunchDetailsScreen() {
    Text("Launch Details Screen")
}
