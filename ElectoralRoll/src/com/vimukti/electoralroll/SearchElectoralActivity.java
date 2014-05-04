package com.vimukti.electoralroll;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class SearchElectoralActivity extends ListActivity {

	private EditText searchBox;
	private ElectoralDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		this.datasource = new ElectoralDataSource(this);
		datasource.open(true);

		this.searchBox = (EditText) findViewById(R.id.search_box);
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// findElectorals();
			}
		});

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Electoral> adapter = new ArrayAdapter<Electoral>(this,
				android.R.layout.simple_list_item_1) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ElectoralView eView = null;
				if (convertView == null) {
					eView = (ElectoralView) getLayoutInflater().inflate(
							R.layout.layout_electrol, parent, false);
				} else {
					eView = (ElectoralView) convertView;
				}
				Electoral e = getItem(position);
				eView.setElectrol(e);
				return eView;
			}
		};
		setListAdapter(adapter);

	}

	protected void findElectorals() {
		String text = searchBox.getText().toString();
		List<Electoral> result = searchElectoral(text);
		showElectorals(result);
	}

	@SuppressWarnings("unchecked")
	private void showElectorals(List<Electoral> result) {
		ArrayAdapter<Electoral> adapter = (ArrayAdapter<Electoral>) getListAdapter();
		adapter.clear();

		if (result.isEmpty()) {
			adapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "No result found.",
					Toast.LENGTH_LONG).show();
			return;
		}

		for (Electoral e : result) {
			adapter.add(e);
		}
		adapter.notifyDataSetChanged();
	}

	private List<Electoral> searchElectoral(String text) {
		List<Electoral> result = datasource.findElectorals(text);
		return result;
	}

	public void onSearch(View button) {
		findElectorals();
	}

	@Override
	protected void onResume() {
		datasource.open(true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
