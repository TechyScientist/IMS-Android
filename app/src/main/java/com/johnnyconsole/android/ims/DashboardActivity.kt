package com.johnnyconsole.android.ims

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.johnnyconsole.android.ims.databinding.ActivityDashboardBinding
import com.johnnyconsole.android.ims.session.UserSession
import android.view.View.INVISIBLE
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog

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
            btSearchBy.text = getString(R.string.search_by, "IMS Barcode")

            etSearch.setOnEditorActionListener { _, action, event ->
                return@setOnEditorActionListener if(action == EditorInfo.IME_ACTION_DONE ||
                    (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    search()
                    true
                } else false
            }

            if(UserSession.access != 1) llAdmin.visibility = INVISIBLE

            btSearchBy.setOnClickListener {_ ->
                val dialog = AlertDialog.Builder(this@DashboardActivity)
                    .setTitle(R.string.search_title)
                    .setItems(R.array.search_fields) { _, i ->
                        btSearchBy.text = getString(R.string.search_by, resources.getStringArray(R.array.search_fields)[i])
                    }.setNegativeButton(R.string.cancel) {dialog, _ ->
                        dialog.dismiss()
                    }.create()

                dialog.show()
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.error))
            }

            btSignOut.setOnClickListener { _ ->
                UserSession.destroy()
                finish()
            }
        }
    }

    private fun search() {
        //TODO: Implement searching functionality
    }
}