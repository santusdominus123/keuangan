package com.dataapk.keuangan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private PieChart pieChart;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            dbHelper = new DatabaseHelper(this);

            // Initialize views
            btnLogout = findViewById(R.id.btnLogout);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            pieChart = findViewById(R.id.pieChart);
            FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

            // Setup logout button
            btnLogout.setOnClickListener(v -> showLogoutDialog());

            // Setup RecyclerView
            adapter = new TransactionAdapter(this, transactionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            // Setup FAB
            fabAdd.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Error membuka form tambah transaksi", Toast.LENGTH_SHORT).show();
                }
            });

            // Setup adapter click listener
            adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    try {
                        Transaction transaction = transactionList.get(position);
                        Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                        intent.putExtra("transaction", transaction);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error membuka transaksi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDeleteClick(int position) {
                    try {
                        Transaction transaction = transactionList.get(position);
                        dbHelper.deleteTransaction(transaction.getId());
                        refreshData();
                        Toast.makeText(MainActivity.this, "Transaksi dihapus", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error menghapus transaksi", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Configure pie chart
            setupPieChart();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error memuat aplikasi", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Ya", (dialog, which) -> performLogout())
                    .setNegativeButton("Batal", null)
                    .show();
        } catch (Exception e) {
            performLogout(); // Fallback jika dialog gagal
        }
    }

    private void performLogout() {
        try {
            // Clear login state
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();

            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            // Force close app if logout fails
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error memuat data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPieChart() {
        try {
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5, 10, 5, 5);
            pieChart.setDragDecelerationFrictionCoef(0.95f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(getColor(R.color.white));
            pieChart.setTransparentCircleRadius(61f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshData() {
        try {
            // Get data from database
            transactionList.clear();
            transactionList.addAll(dbHelper.getAllTransactions());
            adapter.notifyDataSetChanged();

            // Update summary
            double income = dbHelper.getTotalIncome();
            double expense = dbHelper.getTotalExpense();
            double balance = income - expense;

            // Update text views
            TextView balanceTextView = findViewById(R.id.balanceTextView);
            TextView incomeTextView = findViewById(R.id.incomeTextView);
            TextView expenseTextView = findViewById(R.id.expenseTextView);

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            format.setMaximumFractionDigits(0);

            balanceTextView.setText(format.format(balance));
            incomeTextView.setText(format.format(income));
            expenseTextView.setText(format.format(expense));

            // Update pie chart
            updatePieChart(income, expense);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error memuat data transaksi", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePieChart(double income, double expense) {
        try {
            List<PieEntry> entries = new ArrayList<>();
            if (income > 0) entries.add(new PieEntry((float) income, "Income"));
            if (expense > 0) entries.add(new PieEntry((float) expense, "Expense"));

            if (!entries.isEmpty()) {
                PieDataSet dataSet = new PieDataSet(entries, "Financial Summary");
                dataSet.setColors(new int[] {getColor(R.color.green_500), getColor(R.color.red_500)});
                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(getColor(R.color.white));

                PieData data = new PieData(dataSet);
                pieChart.setData(data);
                pieChart.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}