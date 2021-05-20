package edu.uwindsor.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
//import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var apiTextView: TextView
    private lateinit var cheatButton: Button

    

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val marks = savedInstanceState?.getIntegerArrayList("SAVED_MARKS") ?: mutableListOf(2, 2, 2, 2, 2, 2)
        quizViewModel.marks = marks

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)
        apiTextView = findViewById(R.id.textView)

        apiTextView.text = getString(R.string.apiLevel, Build.VERSION.RELEASE)
        Toast.makeText(this, Build.VERSION.RELEASE, Toast.LENGTH_LONG).show()

        trueButton.setOnClickListener{
            checkAnswer(true)
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }

        falseButton.setOnClickListener{
            checkAnswer(false)
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        questionTextView.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }

        cheatButton.setOnClickListener {view ->
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
        }

            updateQuestion()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")

        if (quizViewModel.marks[quizViewModel.currentIndex] == 2){
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }else{
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX,  quizViewModel.currentIndex)
        savedInstanceState.putIntegerArrayList("SAVED_MARKS", quizViewModel.marks.toCollection(ArrayList()))
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion(){
        quizViewModel.isCheater = false
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        Log.d(TAG, quizViewModel.marks.toString())

        if (quizViewModel.marks[quizViewModel.currentIndex] == 2){
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }else{
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }

        if (!quizViewModel.marks.contains(2)){//if you are done the quiz,
            val finalMarks = "%.2f".format((quizViewModel.marks.sum().toDouble()/quizViewModel.questionBankSize)*100)
            val toaster = Toast.makeText(this, "Completed with a final mark of $finalMarks%", Toast.LENGTH_LONG)
            toaster.show()
        }
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer

        var messageResId = when {
            quizViewModel.isCheater -> {
                quizViewModel.marks[quizViewModel.currentIndex] = 0

                R.string.judgment_toast
            }
            userAnswer == correctAnswer -> {
                quizViewModel.marks[quizViewModel.currentIndex] = 1
                R.string.correct_toast
            }
            else -> {
                quizViewModel.marks[quizViewModel.currentIndex] = 0
                R.string.incorrect_toast
            }
        }

        val toaster = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toaster.show()

    }
}
