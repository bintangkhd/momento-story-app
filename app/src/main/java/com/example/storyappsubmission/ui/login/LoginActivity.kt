package com.example.storyappsubmission.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.R
import com.example.storyappsubmission.customview.CustomPopUpAlert
import com.example.storyappsubmission.data.model.LoginModel
import com.example.storyappsubmission.data.model.LoginResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.preferences.UserLoginPreference
import com.example.storyappsubmission.databinding.ActivityLoginBinding
import com.example.storyappsubmission.ui.GeneralViewModelFactory
import com.example.storyappsubmission.ui.main.MainActivity
import com.example.storyappsubmission.ui.register.RegisterActivity
import com.example.storyappsubmission.utils.validateEmail
import com.example.storyappsubmission.utils.validatePassword

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { factory }
    private lateinit var factory: GeneralViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = GeneralViewModelFactory.getInstance(binding.root.context)

        setupView()
        setUpAnimation()

        enableLoginButton()
        applyEnableLoginButton()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                loginStep(email, password)
            } else {
                CustomPopUpAlert(this, R.string.wrong_email_password).show()
            }
        }

        binding.tvDoNotHaveAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            System.exit(0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loginStep(email: String, password: String) {
        viewModel.userPostLogin(email, password).observe(this@LoginActivity) { result ->
            if (result != null) {
                when(result) {
                    is ResultCondition.LoadingState -> {
                        progressLoading(true)
                    }
                    is ResultCondition.ErrorState -> {
                        progressLoading(false)
                        errorHandler()
                    }
                    is ResultCondition.SuccessState -> {
                        loginSuccess(result.data)
                    }
                }
            }
        }
    }

    private fun loginSuccess(loginResponse: LoginResponseModel) {
        saveUserDataLogin(loginResponse)
        homeRedirect()
    }

    private fun homeRedirect() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserDataLogin(loginResponse: LoginResponseModel) {
        val loginPreference = UserLoginPreference(this)
        val loginResult = loginResponse.loginResult
        val loginModel = LoginModel(
            name = loginResult?.name, userId = loginResult?.userId, token = loginResult?.token
        )

        loginPreference.setUserLogin(loginModel)
    }

    private fun errorHandler() {
        CustomPopUpAlert(this, R.string.wrong_email_password).show()
    }

    private fun enableLoginButton() {
        val email = binding.emailEdit.text
        val password = binding.passwordEdit.text

        binding.loginButton.isEnabled = validateEmail(email.toString()) && validatePassword(password.toString())
    }

    private fun applyEnableLoginButton() {
        binding.passwordEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableLoginButton()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.emailEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableLoginButton()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
        }
    }

    private fun setUpAnimation() {
        val logoAnimation = ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_Y, -600f, 0f).setDuration(500)
        val FormAnimation = ObjectAnimator.ofFloat(binding.formLogin, View.TRANSLATION_Y, 600f, 0f).setDuration(500)

        val logoAndFormAnimation = AnimatorSet().apply {
            playTogether(logoAnimation, FormAnimation)
        }

        val appNameAnimation = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 0f, 1f).setDuration(500)
        val appTaglineAnimation = ObjectAnimator.ofFloat(binding.tvAppTagline, View.ALPHA, 0f, 1f).setDuration(500)
        val doNotAnimation = ObjectAnimator.ofFloat(binding.tvDoNotHaveAccount, View.ALPHA, 0f, 1f).setDuration(500)

        val textAnimation = AnimatorSet().apply {
            playTogether(appNameAnimation, appTaglineAnimation, doNotAnimation)
        }

        val allAnimation = AnimatorSet().apply {
            playSequentially(logoAndFormAnimation, textAnimation)
        }

        allAnimation.start()

    }

}