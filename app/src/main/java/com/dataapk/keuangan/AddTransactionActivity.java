package com.dataapk.keuangan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private EditText titleEditText, amountEditText;
    private RadioGroup typeRadioGroup;
    private Spinner categorySpinner;
    private Transaction transaction;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        amountEditText = findViewById(R.id.amountEditText);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        categorySpinner = findViewById(R.id.categorySpinner);
        Button saveButton = findViewById(R.id.saveButton);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Check if editing existing transaction
        if (getIntent().hasExtra("transaction")) {
            transaction = (Transaction) getIntent().getSerializableExtra("transaction");
            populateFields();
        }

        saveButton.setOnClickListener(v -> saveTransaction());
    }

    private void populateFields() {
        if (transaction != null) {
            titleEditText.setText(transaction.getTitle());
            amountEditText.setText(String.valueOf(transaction.getAmount()));

            // Select radio button
            int radioId = transaction.getType().equals("Income") ?
                    R.id.incomeRadio : R.id.expenseRadio;
            typeRadioGroup.check(radioId);

            // Select category in spinner
            ArrayAdapter adapter = (ArrayAdapter) categorySpinner.getAdapter();
            int position = adapter.getPosition(transaction.getCategory());
            if (position >= 0) {
                categorySpinner.setSelection(position);
            }
        }
    }

    private void saveTransaction() {
        String title = titleEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();

        // Validation
        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate radio button selection
        int checkedRadioButtonId = typeRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select transaction type", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String type = ((RadioButton) findViewById(checkedRadioButtonId)).getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            if (transaction == null) {
                // Add new transaction
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        .format(new Date());
                Transaction newTransaction = new Transaction(title, amount, type, category, date);
                long result = dbHelper.addTransaction(newTransaction);

                if (result != -1) {
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Update existing transaction
                transaction.setTitle(title);
                transaction.setAmount(amount);
                transaction.setType(type);
                transaction.setCategory(category);
                // Keep original date for existing transactions

                boolean result = dbHelper.updateTransaction(transaction);

                if (result) {
                    Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            finish(); // Close activity and return to MainActivity

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}