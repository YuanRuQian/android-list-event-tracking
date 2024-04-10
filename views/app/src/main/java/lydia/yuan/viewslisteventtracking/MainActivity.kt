package lydia.yuan.viewslisteventtracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import lydia.yuan.viewslisteventtracking.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.graph = navController.createGraph(
            startDestination = precise_logging
        ) {
            fragment<PreciseLoggingFragment>(precise_logging) {
                label = resources.getString(R.string.precise_logging_title)
            }

            fragment<LooseLoggingFragment>(loose_logging) {
                label = resources.getString(R.string.loose_logging_title)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_precise_logging -> {
                    navController.navigate(precise_logging)
                    true
                }
                R.id.navigation_loose_logging -> {
                    navController.navigate(loose_logging)
                    true
                }
                else -> false
            }
        }

    }

    companion object nav_routes {
        const val precise_logging = "precise_logging"
        const val loose_logging = "loose_logging"
    }
}

