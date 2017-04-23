package com.example.kuaijizhang;

import java.util.ArrayList;

import org.itst.cumstomview.XCFlowLayout;
import org.itst.kuaijizhang.adapter.MyPagerAdapter;
import org.itst.sqlhelper.MySqlHelper;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AddFlagsActivity extends Activity {
	private int currentItem,offSet,bmWidth;
	private Bitmap cursor;
	private Matrix matrix = new Matrix();
	private Animation animation;
	private ImageView imageView;
	private TextView txAddExpenseFlagsTitle,txAddIncomeFlagsTitle;
	private ViewPager viewPager;
	private View addIncomeFlagView,addExpenseFlagView;
	private EditText etExpenseFlag,etIncomeFlag;
	 private TextView addExpenseFlagButton,addIncomeFlagButton;
    private XCFlowLayout expenseFlowLayout,incomeFlowLayout;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_flags);
		imageView = (ImageView) findViewById(R.id.add_flag_cursor);
		ArrayList<View> viewList = new ArrayList<View>();
		addIncomeFlagView = getLayoutInflater().inflate(
				R.layout.add_income_flags, null);
		addExpenseFlagView = getLayoutInflater().inflate(
				R.layout.add_expense_flags, null);
		txAddExpenseFlagsTitle = (TextView) this.findViewById(R.id.tx_add_expense_flags);
		txAddIncomeFlagsTitle = (TextView) this.findViewById(R.id.tx_add_income_flags);
		
		addExpenseFlagButton = (TextView) addExpenseFlagView.findViewById(R.id.btn_add_expense_flag);
		addIncomeFlagButton = (TextView) addIncomeFlagView.findViewById(R.id.btn_add_income_flag);
		
		etExpenseFlag = (EditText) addExpenseFlagView.findViewById(R.id.et_add_expense_flag);
		addExpenseFlagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String flagName=etExpenseFlag.getText().toString();
				addExpenseFlag(flagName);
				setFlagListener();
				etExpenseFlag.setText("");
			}
		});
		etIncomeFlag = (EditText) addIncomeFlagView.findViewById(R.id.et_add_income_flag);
		addIncomeFlagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String flagName=etIncomeFlag.getText().toString();
				addIncomeFlag(flagName);
				setFlagListener();
				etIncomeFlag.setText("");
			}
		});
		
		viewList.add(addExpenseFlagView);
		viewList.add(addIncomeFlagView);
		MyPagerAdapter mpa = new MyPagerAdapter(viewList);
		viewPager = (ViewPager) this
				.findViewById(R.id.add_flag_viewpager);
		viewPager.setAdapter(mpa);
		 viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					 switch (arg0) {      
		                case 0:      
		                    if (currentItem == 1) {      
		                        animation = new TranslateAnimation(offSet * 2 + bmWidth, 0, 0, 0);      
		                    } else if (currentItem == 2) {      
		                        animation = new TranslateAnimation(offSet * 4 + 2 * bmWidth, 0, 0, 0);      
		                    }      
		                    break;      
		                case 1:     
		                    if (currentItem == 0) {      
		                        animation = new TranslateAnimation(0, offSet * 2 + bmWidth, 0, 0);      
		                    } else if (currentItem == 2) {      
		                        animation = new TranslateAnimation(4 * offSet + 2 * bmWidth, offSet * 2 + bmWidth, 0, 0);      
		                    }      
		                    break;      
		                }      
		                currentItem = arg0;      
		                animation.setDuration(40); // 光标滑动速度      
		                animation.setFillAfter(true);    
		                imageView.startAnimation(animation);    
				}
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		 txAddExpenseFlagsTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(1);
				viewPager.setCurrentItem(0);
			}
		});
		 txAddIncomeFlagsTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(1);
			}
		});
		 initeCursor();
		 initChildViews(); 
		 setFlagListener();
		 
	}
	//计算滑动的图片的位置
		private void initeCursor() {
			cursor = BitmapFactory
					.decodeResource(getResources(), R.drawable.cursor);
			bmWidth = cursor.getWidth();

			DisplayMetrics dm = getResources().getDisplayMetrics();
			// 设置图标的起始位置(3:3个textview。)
			offSet = (dm.widthPixels - 2 * bmWidth) / 6;
			matrix.setTranslate(offSet, 0);
			imageView.setImageMatrix(matrix); // 需要imageView的scaleType为matrix
			currentItem = 0;
		}
		
		private ArrayList getCategory(String type){
			ArrayList<String> list=new ArrayList<String>();
			MySqlHelper mySqlHelper = new MySqlHelper(this);
			SQLiteDatabase database = mySqlHelper.getReadableDatabase();
			Cursor c = database.query("categories", null, "type=?", new String[]{type}, null, null, null);
			while(c.moveToNext()){
				list.add(c.getString(c.getColumnIndex("category_name")));
			}
			database.close();
			return list;
		}
		
		 private void initChildViews() {  
		        // TODO Auto-generated method stub  
		        expenseFlowLayout = (XCFlowLayout) addExpenseFlagView.findViewById(R.id.expense_flowlayout);  
		        incomeFlowLayout = (XCFlowLayout) addIncomeFlagView.findViewById(R.id.income_flowlayout);
		        MarginLayoutParams lp = new MarginLayoutParams(  
		                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
		        lp.leftMargin = 5;   
		        lp.rightMargin = 5;  
		        lp.topMargin = 5;  
		        lp.bottomMargin = 5; 
		        ArrayList<String> list=getCategory("expense");
		        for(int i = 0; i < list.size(); i ++){  
		            TextView view = new TextView(this);  
		            view.setText(list.get(i));
		            view.setTextColor(Color.WHITE);   
		            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_shape));  
		            expenseFlowLayout.addView(view,lp);  
		        }  
		        
		        list = getCategory("income");
		        for (int i = 0; i < list.size(); i++) {
					TextView view = new TextView(this);
					view.setText(list.get(i));
					view.setTextColor(Color.WHITE);
					view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_shape));
					incomeFlowLayout.addView(view,lp);
				}
		 } 
		 
		 private void addExpenseFlag(String name){
			 	MySqlHelper helper=new MySqlHelper(this);
			 	SQLiteDatabase database=helper.getReadableDatabase();
			 	ContentValues cv= new ContentValues();
			 	cv.put("category_name", name);
			 	cv.put("type", "expense");
			 	if(database.insert("categories", null, cv)==-1){
			 		AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 		builder.setTitle("警告！");
			 		builder.setMessage("不能添加重复标签！");
			 		builder.setPositiveButton("确定", null);
			 		builder.create().show();
			 	}else{
			 		expenseFlowLayout = (XCFlowLayout) addExpenseFlagView.findViewById(R.id.expense_flowlayout);  
			        MarginLayoutParams lp = new MarginLayoutParams(
			                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
			        lp.leftMargin = 5;  
			        lp.rightMargin = 5;  
			        lp.topMargin = 5;  
			        lp.bottomMargin = 5;
			        TextView view = new TextView(this);  
			        view.setGravity(Gravity.CENTER);
		            view.setText(name);  
		            view.setTextColor(Color.WHITE);
		            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_shape));  
		            expenseFlowLayout.addView(view,lp);
			 	}
			 	database.close();
			 	 
		 }
		 private void addIncomeFlag(String name){
			 MySqlHelper helper=new MySqlHelper(this);
			 SQLiteDatabase database=helper.getReadableDatabase();
			 ContentValues cv= new ContentValues();
			 cv.put("category_name", name);
			 cv.put("type", "income");
			 if(database.insert("categories", null, cv)==-1){
				 AlertDialog.Builder builder = new AlertDialog.Builder(this);
				 builder.setTitle("警告！");
				 builder.setMessage("不能添加重复标签！");
				 builder.setPositiveButton("确定", null);
				 builder.create().show();
			 }else{
				 incomeFlowLayout = (XCFlowLayout) addIncomeFlagView.findViewById(R.id.income_flowlayout);  
				 MarginLayoutParams lp = new MarginLayoutParams(
						 LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
				 lp.leftMargin = 5;  
				 lp.rightMargin = 5;  
				 lp.topMargin = 5;  
				 lp.bottomMargin = 5;
				 TextView view = new TextView(this);  
				 view.setText(name);  
				 view.setTextColor(Color.WHITE);
				 view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_shape));  
				 incomeFlowLayout.addView(view,lp);
			 }
			 database.close();
			 
		 }
		 private void deleteFlagsByName(String name){
			 MySqlHelper helper = new MySqlHelper(this);
			 SQLiteDatabase database = helper.getReadableDatabase();
			 database.delete("categories", "category_name=?", new String[]{name+""});
			 database.close();
			 
		 }
		 private void setFlagListener(){
			 for (int i = 0; i < expenseFlowLayout.getChildCount(); i++) {
				 expenseFlowLayout.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
						@Override 
						public boolean onLongClick(View v) {
							final TextView sview=(TextView) v;
							AlertDialog.Builder builder=new AlertDialog.Builder(AddFlagsActivity.this);
							builder.setTitle("提示！");
							builder.setMessage("你确定要删除["+((TextView)v).getText().toString()+"]吗？");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(expenseFlowLayout.getChildCount()<3){
										 Toast.makeText(AddFlagsActivity.this, "标签数太少不利于记账哦~", Toast.LENGTH_SHORT).show();
									 }else{
										 deleteFlagsByName(sview.getText().toString());
										 expenseFlowLayout.removeView(sview);
									 }
									
									dialog.dismiss();
								}
							});
							builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
							return true;
						}
					});
			}
			 for (int i = 0; i < incomeFlowLayout.getChildCount(); i++) {
				 incomeFlowLayout.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
					 @Override 
					 public boolean onLongClick(View v) {
						 final TextView sview=(TextView) v;
						 AlertDialog.Builder builder=new AlertDialog.Builder(AddFlagsActivity.this);
						 builder.setTitle("提示！");
						 builder.setMessage("你确定要删除["+((TextView)v).getText().toString()+"]吗？");
						 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog, int which) {
								 if(incomeFlowLayout.getChildCount()<3){
									 Toast.makeText(AddFlagsActivity.this, "标签数太少不利于记账哦~", Toast.LENGTH_SHORT).show();
								 }else{
									 deleteFlagsByName(sview.getText().toString());
									 incomeFlowLayout.removeView(sview);
								 }
								 
								 dialog.dismiss();
							 }
						 });
						 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							 
							 @Override
							 public void onClick(DialogInterface dialog, int which) {
								 dialog.dismiss();
							 }
						 });
						 builder.create().show();
						 return true;
					 }
				 });
			 }
		 }
}
