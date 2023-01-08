package ru.netology.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {
    companion object {
        const val EDIT = "EditPost"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editText.requestFocus()

        intent?.let {
            binding.editText.setText(intent.getStringExtra(EditPostActivity.EDIT))
        }

        binding.okEditButton.setOnClickListener {
            val intent = Intent()
            if (binding.editText.text.isBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.editText.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
        binding.canselEditButton.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}