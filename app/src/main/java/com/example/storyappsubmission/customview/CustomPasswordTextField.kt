package com.example.storyappsubmission.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.storyappsubmission.R
import com.example.storyappsubmission.utils.validatePassword
import com.google.android.material.textfield.TextInputEditText

class CustomPasswordTextField: TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                checkPassword(p0.toString())
            }

        })
    }

    private fun checkPassword(password: String) {
        if (validatePassword(password)) {
            this@CustomPasswordTextField.error = null
        } else {
            this@CustomPasswordTextField.error = context.getString(R.string.password_error)
        }
    }
}