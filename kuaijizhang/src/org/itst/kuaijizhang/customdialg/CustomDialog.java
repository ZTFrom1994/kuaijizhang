package org.itst.kuaijizhang.customdialg;

import java.util.Calendar;







import com.example.kuaijizhang.MainActivity;
import com.example.kuaijizhang.R;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		private Calendar c;
		private TextView time;
		private Spinner categorySpinner;
		private ArrayAdapter<String> incomeCategoryAdapter;
		private String[] category;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}
//		public String getTime(){
//			
//			String time=((TextView)this.findViewById(R.id.tx_dialog_time)).getText().toString();
//			return time;
//		}
		public void setCategory(String[] category){
			this.category=category;
		}
		
		
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
			
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			
			((TextView) layout.findViewById(R.id.title)).setText(title);
			
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									
									positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
			}
			
			
			//设置时间
			c=Calendar.getInstance();
			time = (TextView) layout.findViewById(R.id.tx_dialog_time);
			time.setText(String.format("%s:%s", timeFormat(c.get(Calendar.HOUR_OF_DAY)),timeFormat(c.get(Calendar.MINUTE))));
			time.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							time.setText(String.format("%s:%s", timeFormat(hourOfDay),timeFormat(minute)));
						}
					}, 00, 00, true).show();					
				}
			});
			//添加类别
	        incomeCategoryAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_style);
	        for (int i = 0; i < category.length; i++) {
	        	incomeCategoryAdapter.add(category[i]);
			}
			categorySpinner = (Spinner) layout.findViewById(R.id.spinner_income_category);
			categorySpinner.setAdapter(incomeCategoryAdapter);
			
			// set the content message
			if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
	public static String timeFormat(int value){
		return value<10?"0"+value:value+"";
	}
}
