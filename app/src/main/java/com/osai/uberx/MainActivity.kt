package com.osai.uberx

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var navigationView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var userImageView: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        UberXApp.appComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        displayUserHear()
    }

    private fun displayUserHear() {
        val hView = navigationView.getHeaderView(0)
        val userName = hView.findViewById(R.id.userName) as (TextView)
        userImageView = hView.findViewById(R.id.userImage) as (CircleImageView)

        val userNameText = sharedPreferences.getString(getString(R.string.username), "")
        if (!userNameText.isNullOrEmpty()) userName.text = userNameText

        val userImage = sharedPreferences.getString(getString(R.string.userimage), "")
        val imageUri = Uri.parse(userImage)
        if (imageUri != null) displaySelectedImage(imageUri)
    }

    private fun displaySelectedImage(photoPathUri: Uri) {
        Glide
            .with(this)
            .load(photoPathUri.toString())
            .into(userImageView)
    }
}
