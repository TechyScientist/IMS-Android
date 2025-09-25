package com.johnnyconsole.android.ims

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.johnnyconsole.android.ims.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import android.view.View.VISIBLE
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.inputmethod.EditorInfo
import androidx.core.text.HtmlCompat
import com.johnnyconsole.android.ims.session.UserSession
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var pressed = false

    private inner class SignInTask: AsyncTask<String, Unit, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            binding.pbIndicator.visibility = VISIBLE
        }

        override fun doInBackground(vararg params: String): String {
            val conn = (URL("https://wildfly.johnnyconsole.com:8443/ims-restful/api/user/signin").openConnection()) as HttpsURLConnection
            conn.requestMethod = "POST"
            conn.hostnameVerifier = HostnameVerifier { _, _ -> true }
            conn.doOutput = true
            with(conn.outputStream) {
                write("username=${params[0]}&password=${params[1]}".toByteArray())
                flush()
                close()
            }
            conn.connect()
            val response = StringBuffer()

            if(conn.responseCode == HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                for (line in reader.readLines()) {
                    response.append(line)
                }
            }
            return response.toString()
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            binding.pbIndicator.visibility = INVISIBLE
            parseResponseString(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        with(binding) {
            setContentView(root)
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }

            etUsername.setOnEditorActionListener { _, action, event ->
               return@setOnEditorActionListener if(action == EditorInfo.IME_ACTION_DONE ||
                   (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                   signin()
                   true
               } else false
            }

            etPassword.setOnEditorActionListener { _, action, event ->
                return@setOnEditorActionListener if(action == EditorInfo.IME_ACTION_DONE ||
                    (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    signin()
                    true
                } else false
            }

            btSignIn.setOnClickListener {_ -> signin()}
        }
    }

    private fun signin() {
        with(binding) {
            if(pressed || etUsername.text.isNullOrBlank() || etPassword.text.isNullOrBlank())
                return@signin
            pressed = true
            SignInTask().execute(etUsername.text.toString().lowercase(), etPassword.text.toString())
        }
    }

    private fun parseResponseString(response: String) {
        Log.d("SignInResponse", response)
        val json = JSONObject(response)
        val status = json.getInt("status")
        if(status == 200) {
            binding.tvErrorMessage.visibility = GONE
            val user = json.getJSONObject("user")
            UserSession.construct(
                user.getString("username"),
                user.getString("name"),
                user.getInt("access")
            )
            binding.etUsername.text.clear()
            binding.etPassword.text.clear()
            binding.etUsername.requestFocus()
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        else {
            binding.tvErrorMessage.text = HtmlCompat.fromHtml(
                getString(R.string.error,
                    status,
                    json.getString("category"),
                    json.getString("message")
                ), HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvErrorMessage.visibility = VISIBLE
        }
        pressed = false
    }
}