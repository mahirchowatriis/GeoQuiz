package edu.uwindsor.geoquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
//import android.view.View
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

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

        cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

            updateQuestion()

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
            toaster.setGravity(Gravity.TOP, 0, 200)
            toaster.show()
        }
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if(userAnswer == correctAnswer){
            quizViewModel.marks[quizViewModel.currentIndex] = 1
            R.string.correct_toast
        }else{
            quizViewModel.marks[quizViewModel.currentIndex] = 0
            R.string.incorrect_toast
        }

        val toaster = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toaster.setGravity(Gravity.TOP, 0, 200)
        toaster.show()
    }
}
