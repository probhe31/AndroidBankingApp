package com.probe31.probe.bankingappv2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {

        dniText.error = null
        passwordText.error = null

        val dniStr = dniText.text.toString()
        val passwordStr = passwordText.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            passwordText.error = getString(R.string.error_invalid_password)
            focusView = passwordText
            cancel = true
        }

        if (TextUtils.isEmpty(dniStr)) {
            dniText.error = getString(R.string.error_field_required)
            focusView = dniText
            cancel = true

        } else if (!isDniValid(dniStr)) {
            dniText.error = getString(R.string.error_invalid_dni)
            focusView = dniText
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)

            val jsonObject = JSONObject()
            jsonObject.put("dni", dniStr)
            jsonObject.put("password", passwordStr)

            val url = "http://192.168.1.6/api/client"

            val que = Volley.newRequestQueue(this@LoginActivity)
            val req = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    Response.Listener {

                        //response -> Log.INFO(response["status"].toString());
                        response -> println("success " + response["status"].toString())
                        showProgress(false)
                        goToListAccountActivity()

                    },
                    Response.ErrorListener {
                        error -> println("error bherring " + error)
                        showProgress(false)
                        passwordText.error = getString(R.string.error_incorrect_password)
                        passwordText.requestFocus()
                    })

            que.add(req)
        }
    }

    private fun isDniValid(dni: String): Boolean {
        //TODO: Replace this with your own logic
        //return email.contains("@")
        return dni.length == 8
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length == 4
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    private fun goToListAccountActivity()
    {
        val intent = Intent(this, ListAccountsActivity::class.java).apply {
            //putExtra(EXTRA_MESSAGE, message)
        }

        startActivity(intent)
    }


}
