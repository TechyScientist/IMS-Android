package com.johnnyconsole.android.ims

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.johnnyconsole.android.ims.databinding.ActivityDashboardBinding
import com.johnnyconsole.android.ims.session.UserSession
import android.view.View.INVISIBLE

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        with(binding) {
            setContentView(root)
            ViewCompat.setOnApplyWindowInsetsListener(root, { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            })
            appBar.activityTitle.text = getString(R.string.activity_title, "Dashboard")

            tvHeader.text = getString(R.string.dashboard_header, UserSession.name!!)

            if(UserSession.access != 1) llAdmin.visibility = INVISIBLE

            btSignOut.setOnClickListener { _ ->
                UserSession.destroy()
                finish()
            }
        }
    }
}