package com.example.storyappsubmission.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.R
import com.example.storyappsubmission.customview.CustomPopUpAlert
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.databinding.ActivityRegisterBinding
import com.example.storyappsubmission.ui.GeneralViewModelFactory
import com.example.storyappsubmission.ui.login.LoginActivity
import com.example.storyappsubmission.utils.maxName
import com.example.storyappsubmission.utils.validateEmail
import com.example.storyappsubmission.utils.validatePassword

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { factory }
    private lateinit var factory: GeneralViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = GeneralViewModelFactory.getInstance(binding.root.context)

        setupView()
        enableRegisterButton()
        applyEnableRegisterButton()
        setUpAnimation()

        binding.registerButton.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                registerStep(name, email, password)
            } else {
                CustomPopUpAlert(this, R.string.register_error_validation).show()
            }
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

    override fun onDestroy() {
        super.onDestroy()
        this.finish()
    }

    private fun registerStep(name: String, email: String, password: String) {
        viewModel.userPostRegister(name, email, password).observe(this@RegisterActivity) { result ->
            if (result != null) {
                when(result) {
                    is ResultCondition.LoadingState -> {
                        progressLoading(true)
                    }
                    is ResultCondition.ErrorState -> {
                        progressLoading(false)
                        errorHandler(true)
                    }
                    is ResultCondition.SuccessState -> {
                        progressLoading(false)
                        registerSuccess(true)
                    }
                }
            }
        }
    }

    private fun registerSuccess(success: Boolean) {
        if (success) {
            val alert = CustomPopUpAlert(this, R.string.register_success)
            alert.show()
            alert.setOnDismissListener {
                loginRedirect()
            }

        }
    }

    private fun loginRedirect() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun errorHandler(error: Boolean) {
        if(error) {
            CustomPopUpAlert(this, R.string.register_error).show()
        }
    }

    private fun setUpAnimation() {
        val textInputnName = ObjectAnimator.ofFloat(binding.nameTextField, View.TRANSLATION_X, -600f, 0f).setDuration(500)
        val textInputPassword = ObjectAnimator.ofFloat(binding.passwordTextField, View.TRANSLATION_X, -600f, 0f).setDuration(500)

        val emailTextField = ObjectAnimator.ofFloat(binding.emailTextField, View.TRANSLATION_X, 600f, 0f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.registerButton, View.TRANSLATION_X, 600f, 0f).setDuration(500)


        val tranlateAnimation = AnimatorSet().apply {
            playTogether(textInputnName, textInputPassword, emailTextField, btnRegister)
        }

        val tvRegisterAnim = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 0f, 1f).setDuration(500)
        val tvAlreadyHaveAnim = ObjectAnimator.ofFloat(binding.tvAlreadyHaveAccount, View.ALPHA, 0f, 1f).setDuration(500)

        val fadeAnimation = AnimatorSet().apply {
            playTogether(tvRegisterAnim, tvAlreadyHaveAnim)
        }

        val allAnimation = AnimatorSet().apply {
            playSequentially(tranlateAnimation, fadeAnimation)
        }

        allAnimation.start()

    }

    private fun enableRegisterButton() {
        val name = binding.nameEdit.text
        val email = binding.emailEdit.text
        val password = binding.passwordEdit.text

        binding.registerButton.isEnabled = maxName(name.toString()) && validateEmail(email.toString()) && validatePassword(password.toString())
    }

    private fun applyEnableRegisterButton() {
        binding.passwordEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableRegisterButton()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.emailEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableRegisterButton()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.registerLayout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.registerLayout.visibility = View.VISIBLE
        }
    }



}