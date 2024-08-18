package com.example.calculatorapp;

import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView inputDisplay;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnAdd, btnSub, btnMul, btnDiv, btnClear, btnEqual, btnHistory, btnNotes;
    private String currentInput = "";
    private String operation = "";
    private double result = 0;

    private List<String> historyList = new ArrayList<>();
    private static final String PREFS_NAME = "CalculatorAppPrefs";
    private static final String NOTES_KEY = "notes";

    private SoundPool soundPool;
    private int buttonClickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputDisplay = findViewById(R.id.inputDisplay);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);

        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnMul = findViewById(R.id.btnMul);
        btnDiv = findViewById(R.id.btnDiv);
        btnClear = findViewById(R.id.btnClear);
        btnEqual = findViewById(R.id.btnEqual);

        btnHistory = findViewById(R.id.btnHistory);
        btnNotes = findViewById(R.id.btnNotes);

        // Initialize SoundPool and load sound
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        buttonClickSound = soundPool.load(this, R.raw.button_click, 1);

        setButtonListenersWithAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the SoundPool resources
        soundPool.release();
    }

    private void setButtonListenersWithAnimation() {
        Button[] buttons = {
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                btnAdd, btnSub, btnMul, btnDiv, btnClear, btnEqual, btnHistory, btnNotes
        };

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                animateButton(button);
                handleButtonClick(button);
            });
        }
    }

    private void handleButtonClick(Button button) {
        int id = button.getId();

        if (id == R.id.btn0) {
            appendNumber("0");
        } else if (id == R.id.btn1) {
            appendNumber("1");
        } else if (id == R.id.btn2) {
            appendNumber("2");
        } else if (id == R.id.btn3) {
            appendNumber("3");
        } else if (id == R.id.btn4) {
            appendNumber("4");
        } else if (id == R.id.btn5) {
            appendNumber("5");
        } else if (id == R.id.btn6) {
            appendNumber("6");
        } else if (id == R.id.btn7) {
            appendNumber("7");
        } else if (id == R.id.btn8) {
            appendNumber("8");
        } else if (id == R.id.btn9) {
            appendNumber("9");
        } else if (id == R.id.btnAdd) {
            setOperation("+");
        } else if (id == R.id.btnSub) {
            setOperation("-");
        } else if (id == R.id.btnMul) {
            setOperation("*");
        } else if (id == R.id.btnDiv) {
            setOperation("/");
        } else if (id == R.id.btnEqual) {
            evaluate();
        } else if (id == R.id.btnClear) {
            clear();
        } else if (id == R.id.btnHistory) {
            showHistory();
        } else if (id == R.id.btnNotes) {
            saveNote();
        }
    }

    private void appendNumber(String number) {
        currentInput += number;
        inputDisplay.setText(currentInput);
    }

    private void setOperation(String op) {
        if (!currentInput.isEmpty()) {
            operation = op;
            result = Double.parseDouble(currentInput);
            currentInput = "";
        } else {
            inputDisplay.setText("Error: Enter a number first");
        }
    }

    private void evaluate() {
        if (currentInput.isEmpty()) {
            inputDisplay.setText("Error: Complete the expression");
            return;
        }

        double secondOperand;
        try {
            secondOperand = Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            inputDisplay.setText("Error: Invalid input");
            return;
        }

        switch (operation) {
            case "+":
                result += secondOperand;
                break;
            case "-":
                result -= secondOperand;
                break;
            case "*":
                result *= secondOperand;
                break;
            case "/":
                if (secondOperand != 0) {
                    result /= secondOperand;
                } else {
                    inputDisplay.setText("Error: Division by zero");
                    return;
                }
                break;
            default:
                inputDisplay.setText("Error: No operation selected");
                return;
        }

        String expression = result + " " + operation + " " + secondOperand + " = " + result;
        historyList.add(expression);
        inputDisplay.setText(String.valueOf(result));
        currentInput = String.valueOf(result);
    }

    private void clear() {
        currentInput = "";
        operation = "";
        result = 0;
        inputDisplay.setText("0");
    }

    private void showHistory() {
        if (historyList.isEmpty()) {
            inputDisplay.setText("No history");
        } else {
            StringBuilder historyText = new StringBuilder();
            for (String entry : historyList) {
                historyText.append(entry).append("\n");
            }
            inputDisplay.setText(historyText.toString());
        }
    }

    private void saveNote() {
        if (currentInput.isEmpty()) {
            Toast.makeText(this, "Error: No note to save", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String noteContent = currentInput;
        editor.putString(NOTES_KEY, noteContent);
        editor.apply();

        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();

        clear();
    }

    private void animateButton(Button button) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 0.9f, // Start and end values for the X axis scaling
                1f, 0.9f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        scaleAnimation.setDuration(100); // Duration for the animation
        scaleAnimation.setFillAfter(true); // If fillAfter is true, the transformation that this animation performed will persist when it is finished.

        // Start scaling up after the button press
        ScaleAnimation scaleBackAnimation = new ScaleAnimation(
                0.9f, 1f,
                0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleBackAnimation.setDuration(100);
        scaleBackAnimation.setStartOffset(100);
        scaleBackAnimation.setFillAfter(true);

        button.startAnimation(scaleAnimation);
        button.postDelayed(() -> {
            button.startAnimation(scaleBackAnimation);
            soundPool.play(buttonClickSound, 1f, 1f, 0, 0, 1f); // Play sound effect
        }, 100); // Ensure animation sequence
    }
}
