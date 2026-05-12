package com.ptithcm.lottemart.features.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.lottemart.R;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_search);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        
        etSearch.requestFocus();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;
        
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("QUERY", query);
        startActivity(intent);
    }
}
