package com.udacity

import androidx.annotation.StringRes


sealed class ButtonState(@StringRes val text: Int) {
    object Loading : ButtonState(R.string.button_loading)
    object Completed : ButtonState(R.string.button_name)
}