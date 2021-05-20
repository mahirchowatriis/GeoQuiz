package edu.uwindsor.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE = "edu.uwindsor.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "edu.uwindsor.geoquiz.answer_shown"
private const val KEY_HAS_CHEATED = "has_cheated"

class CheatActivity : AppCompatActivity() {

    private var answerIsTrue = false
    private var hasCheated = false
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        hasCheated = savedInstanceState?.getBoolean(KEY_HAS_CHEATED, false) ?: false
        setAnswerShownResult(hasCheated)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            hasCheated = true
            setAnswerShownResult(true)
            displayCheatText()
        }
        displayCheatText()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_HAS_CHEATED, hasCheated)
    }

    private fun displayCheatText() {
        if (hasCheated) {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}