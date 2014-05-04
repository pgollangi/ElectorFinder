package com.vimukti.electoralroll;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ElectoralView extends LinearLayout {

	public ElectoralView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private TextView row1;
	private TextView row2;

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.row1 = (TextView) findViewById(R.id.row1);
		this.row2 = (TextView) findViewById(R.id.row2);
	}

	public void setElectrol(Electoral e) {
		String cp = String
				.format("%03d-%d", e.getConstituency(), e.getPartNo());
		StringBuffer buff = new StringBuffer(cp);
		buff.append("  ");
		buff.append(String.valueOf(e.getSerial()));
		buff.append("  ");
		buff.append(e.getVoterId());
		buff.append("  ");
		buff.append(e.getName());
		row1.setText(buff.toString());
		row2.setText(e.getRelative() + "  " + e.getHouseNo() + "  "
				+ e.getGender());
	}
}
