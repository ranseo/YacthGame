package com.ranseo.yatchgame.ui.lobby

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.ActivityLobbyBinding
import com.ranseo.yatchgame.ui.login.LoginActivity
import com.ranseo.yatchgame.util.YachtSound
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 사용자가 방을 만들고
 * */
@AndroidEntryPoint
class LobbyActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding : ActivityLobbyBinding

    private lateinit var navController : NavController
    private lateinit var appBarConfiguration : AppBarConfiguration


    //firebase
    @Inject lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.lobbyToolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.lobby_nav_host) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.lobbyDrawer)
        binding.lobbyToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.lobbyNavigationView.setNavigationItemSelectedListener(this@LobbyActivity)



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                auth.signOut()
                logout()
                binding.lobbyDrawer.closeDrawers()
                true
            }
            R.id.lobby -> {
                if(navController.currentDestination?.id != R.id.lobby) {
                    navController.navigate(R.id.action_global_to_lobby)
                }
                binding.lobbyDrawer.closeDrawers()
                true
            }

            R.id.statis-> {
                if(navController.currentDestination?.id != R.id.statis) {
                    navController.navigate(R.id.action_global_to_statis)
                }
                binding.lobbyDrawer.closeDrawers()
                true
            }
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}