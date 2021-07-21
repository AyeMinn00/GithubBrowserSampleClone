package com.amo.githubbrowsersamplev2.ui.test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amo.githubbrowsersamplev2.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    private val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        viewModel.result.observe(this, {
            Timber.e("result is %s", it)
        })

    }

}