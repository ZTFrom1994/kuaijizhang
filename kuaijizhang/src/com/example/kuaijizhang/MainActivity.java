package com.example.kuaijizhang;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

import org.itst.excel.ExcelUtils;
import org.itst.kuaijizhang.adapter.MyPagerAdapter;
import org.itst.kuaijizhang.customdialg.CustomDialog;
import org.itst.service.MyService;
import org.itst.sqlhelper.MySqlHelper;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

public class MainActivity extends Activity {
	private SQLiteDatabase database;
	private SimpleCursorAdapter cursorAdapter;
	private MySqlHelper mySqlHelper;
	private Button btnInCome, btnexpense;
	private ImageView imageView;
	private Bitmap cursor;
	private Matrix matrix = new Matrix();
	private Animation animation;
	private ListView listView;
	private TextView txBilling, txCount, txSettings, txMonth, txYear, txChooseDateType,txTodayIncome,txTodayExpense,txTotal,txChange,txDetails,txAnalysis;
	private TextView txAddFlags,txAppRecommend,txNotificationSettings,txExportData,txSponsor;
	private List<View> viewList = new ArrayList<View>();
	private ViewPager viewPager;
	private MyPagerAdapter mpa;
	private View billingView, countView, settingsView;
	private Calendar c;
	private int offSet;
	private int currentItem;
	private int bmWidth;
	private String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sept", "Oct", "Nov", "Dec" };
	private String[] title = { "序号", "日期", "时间", "金额", "类别", "类型"};
	private int nowMonth,nowYear,nowDay;
	private int currentYear,currentMonth,nextMonth,lastMonth;
	private int iPosition;
	private PieChart mChart;
	private boolean flag=true;
	private TextView txLastMonth,txNextMonth,txCurrentMonth;
	private float currentMonthIncome=0.0f,currentMonthExpense=0.0f;
	private File file;
	private ArrayList<ArrayList<String>>bill2List;
	private static final int TODAY_CHOOSE=0;
	private static final int RECENT_TWO_DAYS_CHOOSE=1;
	private static final int RECENT_THREE_DAYS_CHOOSE=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.cursor);      
		txBilling=(TextView) this.findViewById(R.id.tx_billing);
		txCount=(TextView) this.findViewById(R.id.tx_count);
		txSettings=(TextView) this.findViewById(R.id.tx_settings);
		mySqlHelper=new MySqlHelper(this);
		viewList=new ArrayList<View>();
		billingView=getLayoutInflater().inflate(R.layout.billing,null);
	    countView=getLayoutInflater().inflate(R.layout.count,null);
		settingsView=getLayoutInflater().inflate(R.layout.settings,null);
		
		mChart = (PieChart) countView.findViewById(R.id.spread_pie_chart);    
		
		txTodayIncome=(TextView)billingView.findViewById(R.id.tx_TodayIncome);
		txTodayExpense=(TextView)billingView.findViewById(R.id.tx_TodayExpense);
		txTotal=(TextView)billingView.findViewById(R.id.tx_Total);
		listView=(ListView) billingView.findViewById(R.id.listView);
		btnInCome=(Button) billingView.findViewById(R.id.btn_income);
		btnexpense=(Button) billingView.findViewById(R.id.btn_expense); 
		
		//viewpager中添加三个页面
		viewList.add(billingView);
        viewList.add(countView);
        viewList.add(settingsView);
		txMonth=(TextView) billingView.findViewById(R.id.tx_month);
		txYear=(TextView) billingView.findViewById(R.id.tx_year);
		txChooseDateType=(TextView) billingView.findViewById(R.id.tx_choose_date_type);
		
		//初始化日期
		c = Calendar.getInstance();//首先要获取日历对象
		nowMonth=c.get(Calendar.MONTH)+1;
		nowYear=c.get(Calendar.YEAR); 
		nowDay=c.get(Calendar.DATE);
		currentMonth=nowMonth; 
		currentYear=nowYear;
		nextMonth=(currentMonth!=12?currentMonth+1:1);
		lastMonth=(currentMonth!=1?currentMonth-1:12);
		
        txMonth.setText(month[nowMonth-1]);   
        txYear.setText(nowYear+""); 
        txChooseDateType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tx = (TextView)v;
				String nowChoose = tx.getText().toString();
				if(nowChoose.equals("今天")){
					tx.setText("最近两天");
					refreshListView(RECENT_TWO_DAYS_CHOOSE);
				}else if(nowChoose.equals("最近两天")){
					tx.setText("最近三天");
					refreshListView(RECENT_THREE_DAYS_CHOOSE);
				}else{
					tx.setText("今天");
					refreshListView(TODAY_CHOOSE);
				}
				
			}
		});
        txAddFlags=(TextView) settingsView.findViewById(R.id.tx_settings_add_flags);
        txAddFlags.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MainActivity.this,AddFlagsActivity.class);
				startActivity(i);
			}
		});
        txSponsor = (TextView)settingsView.findViewById(R.id.tx_settings_pay_me);
        txSponsor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,SponsorActivity.class);
				startActivity(i);
			}
		});
//        txAppRecommend = (TextView)settingsView.findViewById(R.id.tx_settings_application_recommend);
//        txAppRecommend.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				OffersManager.getInstance(MainActivity.this).showOffersWall(); 
//			}
//		});
//        txNotificationSettings = (TextView) settingsView.findViewById(R.id.tx_settings_notification_setting);
//        txNotificationSettings.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(MainActivity.this,NotificationSettingsActivity.class);
//				startActivity(i);
//			}
//		});
         
        txExportData = (TextView) settingsView.findViewById(R.id.tx_settings_data_output);
        txExportData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					initData();
			}
		});
        txChange=(TextView) countView.findViewById(R.id.tx_change);
        txLastMonth=(TextView)countView.findViewById(R.id.tx_lastmonth);
        txNextMonth=(TextView) countView.findViewById(R.id.tx_nextmonth);
        txCurrentMonth=(TextView) countView.findViewById(R.id.tx_currentmonth);
        txDetails=(TextView) countView.findViewById(R.id.tx_currentmonth_details);
        
        txLastMonth.setText(lastMonth+"月");
        txNextMonth.setText(nextMonth+"月");
        txCurrentMonth.setText(currentYear+"年"+currentMonth+"月");
        
		txChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag=!flag;
				PieData mPieData = getPieDataByDate(currentYear, currentMonth); 
				showChart(mChart, mPieData);
			}
		});
		
		txDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MainActivity.this, ShowDetailsActivity.class);
				startActivity(i);
				
			}
		});
		txLastMonth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(lastMonth==1){
					lastMonth=12;
					currentMonth=1;
					nextMonth=2;
				}else if(nextMonth==1){
					nextMonth=12;
					currentMonth=11;
					lastMonth=10;
				}else if(currentMonth==1){
					currentMonth=12;
					lastMonth=11;
					nextMonth=1;
					currentYear--;
				}else{
					lastMonth--;
					nextMonth--;
					currentMonth--;
				}
				txLastMonth.setText(lastMonth+"月");
		        txNextMonth.setText(nextMonth+"月");
		        txCurrentMonth.setText(currentYear+"年"+currentMonth+"月");
		        setcurrentMonthIncomeAndExpend();
		        PieData mPieData = getPieDataByDate(currentYear, currentMonth); 
				showChart(mChart, mPieData);
		        
			}
		});
		txNextMonth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				if(lastMonth==12){
					lastMonth=1;
					currentMonth=2;
					nextMonth=3;
				}else if(nextMonth==12){
					nextMonth=1;
					currentMonth=12;
					lastMonth=11;
				}else if(currentMonth==12){
					currentMonth=1;
					lastMonth=12;
					nextMonth=2;
					currentYear++;
				}else{
					lastMonth++;
					nextMonth++;
					currentMonth++;
				}
				txLastMonth.setText(lastMonth+"月");
		        txNextMonth.setText(nextMonth+"月");
		        txCurrentMonth.setText(currentYear+"年"+currentMonth+"月");
		        
		    	setcurrentMonthIncomeAndExpend();
		        PieData mPieData = getPieDataByDate(currentYear, currentMonth); 
				showChart(mChart, mPieData);
			}
		});
		txAnalysis=(TextView) countView.findViewById(R.id.tx_analysis);
		txAnalysis.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,AnalysisActicity.class);
				startActivity(i);
			}
		});
		 // 初始化滑动图片位置     
        initeCursor(); 
        mpa = new MyPagerAdapter(viewList);
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
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
	                    refreshThreeNumber();
	                    refreshListView(-1);
	                    break;      
	                case 1:     
	                	//设置图表 界面
	                	setcurrentMonthIncomeAndExpend();
	    		        PieData mPieData = getPieDataByDate(nowYear, nowMonth);    
	    		        showChart(mChart, mPieData);
	                    if (currentItem == 0) {      
	                        animation = new TranslateAnimation(0, offSet * 2 + bmWidth, 0, 0);      
	                    } else if (currentItem == 2) {      
	                        animation = new TranslateAnimation(4 * offSet + 2 * bmWidth, offSet * 2 + bmWidth, 0, 0);      
	                    }      
	                    break;      
	                case 2:      
	                    if (currentItem == 0) {      
	                        animation = new TranslateAnimation(0, 4 * offSet + 2 * bmWidth, 0, 0);      
	                    } else if (currentItem == 1) {      
	                        animation = new TranslateAnimation(offSet * 2 + bmWidth, 4 * offSet + 2 * bmWidth, 0, 0);      
	                    }      
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
        
        //设置页面滑动事件（viewpager）
        txBilling.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(0);
				refreshListView(-1);
			}
		});
        
        txCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setcurrentMonthIncomeAndExpend();
				viewPager.setCurrentItem(1);
			}
		});
        
        txSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(2);
			}
		});
        
        //设置两个按钮的动作
        btnInCome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//设置dialog
			    CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);   
	            builder.setTitle("新收入");  
	            builder.setCategory(getCategory("income"));
	            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                }  
	            });  
	            builder.setNegativeButton("确定",  
	                    new android.content.DialogInterface.OnClickListener() {  
	                        public void onClick(DialogInterface dialog, int which) {  
	                            EditText editText=(EditText) ((CustomDialog)dialog).findViewById(R.id.et_dialog_number);
	    	                	String s_number=editText.getText().toString();
	    	                	if(!s_number.equals("")){
	    	                		String time= ((TextView)((CustomDialog)dialog).findViewById(R.id.tx_dialog_time)).getText().toString();
	    		                	float number=Float.parseFloat(s_number);
	    	            			Spinner spinner=(Spinner) ((CustomDialog)dialog).findViewById(R.id.spinner_income_category);
	    	            			String category=spinner.getSelectedItem().toString();
	    		                	insert(time, category, number,"收");
	    		                	refreshListView(-1);
	    		                	refreshThreeNumber();
	    	                	}
	    	                	dialog.dismiss();
	                        }  
	                    });  
	            builder.create().show();
	            
			}
		});
        btnexpense.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final View dialogView = getLayoutInflater().inflate(R.layout.dialog_normal_layout, null);
				CustomDialog.Builder builder = new CustomDialog.Builder(dialogView.getContext());   
	            builder.setTitle("新支出");
	            builder.setCategory(getCategory("expense"));
	            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                	dialog.dismiss();  
	                }  
	            });  
	            builder.setNegativeButton("确定",  
	                    new android.content.DialogInterface.OnClickListener() {  
	                        public void onClick(DialogInterface dialog, int which) {  
	                        	EditText editText=(EditText) ((CustomDialog)dialog).findViewById(R.id.et_dialog_number);
	    	                	String s_number=editText.getText().toString();
	    	                	if(!s_number.equals("")){
	    	                		String time= ((TextView)((CustomDialog)dialog).findViewById(R.id.tx_dialog_time)).getText().toString();
	    		                	float number=Float.parseFloat(s_number);
	    	            			Spinner spinner=(Spinner) ((CustomDialog)dialog).findViewById(R.id.spinner_income_category);
	    	            			String category=spinner.getSelectedItem().toString();
	    		                	insert(time, category, number,"支");
	    		                	refreshListView(-1);
	    		                	refreshThreeNumber();
	    	                	}
	    	                	dialog.dismiss();  
	                        }  
	                    }); 
	            builder.create().show(); 
			}
		});
        SQLiteDatabase sqLiteDatabase=mySqlHelper.getReadableDatabase();
        Cursor c=sqLiteDatabase.query("records", new String[]{"_id","time","category","round(number,1) as number"}, "year=? and month=? and day=?", new String[]{nowYear+"",nowMonth+"",""+nowDay}, null, null, null);
        cursorAdapter=new SimpleCursorAdapter(this, R.layout.record_cell, c, new String[]{"time","category","number"}, new int[]{R.id.tx_record_time,R.id.tx_record_category,R.id.tx_record_number});
        listView.setAdapter(cursorAdapter);
        sqLiteDatabase.close();
        refreshThreeNumber();
        
        
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iPosition=position;
				AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("提示");
				builder.setMessage("确定要删除这条记录吗？");
				builder.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Cursor c = cursorAdapter.getCursor();
						c.moveToPosition(iPosition);
						int ItemId = c.getInt(c.getColumnIndex("_id"));
						deleteById(ItemId);
						refreshThreeNumber();
						refreshListView(-1);
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {
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
	
	private void insert(String time, String category, float number,String flag) {
		database = mySqlHelper.getReadableDatabase();
		ContentValues cValues = new ContentValues(); 
		cValues.put("category", category);
		DecimalFormat decimalFormat=new DecimalFormat("0.0");
		float f=Float.parseFloat(decimalFormat.format(number));
		cValues.put("flag", flag);
		cValues.put("number",f);
		cValues.put("time", time);
		cValues.put("month",nowMonth);
		cValues.put("year",nowYear);
		cValues.put("day",nowDay);
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		cValues.put("date",dateFormater.format(new Date()));
		database.insert("records", null, cValues);
		database.close();
	}

	private void deleteById(int id) {
		database = mySqlHelper.getReadableDatabase();
		database.delete("records", "_id=?", new String[]{id+""});
		database.close();
	}

	private void refreshListView(int  flag) {
		database = mySqlHelper.getReadableDatabase();
		String sql = "select _id,time,category,round(number,1) as number from records where year >= ? and month >= ? and day >=? ";
		Cursor c=null;
		Calendar cal = Calendar.getInstance();
		switch (flag) { 
		case TODAY_CHOOSE:
			c=database.rawQuery(sql, new String[]{nowYear+"",nowMonth+"",nowDay+""});
			break;
		case RECENT_TWO_DAYS_CHOOSE:
			cal.add(Calendar.DATE, -1);
			c=database.rawQuery(sql, new String[]{cal.getTime().getYear()+"",cal.getTime().getMonth()+"",cal.getTime().getDay()+""});
			System.out.println(cal.getTime().getDay());
			break;
		case RECENT_THREE_DAYS_CHOOSE:
			cal.add(Calendar.DATE, -2);
			c = database.rawQuery(sql, new String[]{cal.getTime().getYear()+"",cal.getTime().getMonth()+"",cal.getTime().getDay()+""});
			break;
		default:
			c=database.query("records",new String[]{"_id","time","category","round(number,1) as number"}, "year=? and month=? and day=?", new String[]{nowYear+"",nowMonth+"",""+nowDay}, null, null, null);
			break;
		}
		cursorAdapter.changeCursor(c);
		database.close();
	}
	
    private String[] getCategory(String type){
    	MySqlHelper mySqlHelper = new MySqlHelper(this);
    	database = mySqlHelper.getReadableDatabase();
    	Cursor c = database.query("categories", null, "type=?", new String[]{type}, null,null, null);
    	ArrayList<String> list = new ArrayList<String>();
    	while(c.moveToNext()){
    		list.add(c.getString(c.getColumnIndex("category_name")));
    	}
    	database.close();
		return list.size()==0?new String[]{"空"}:list.toArray(new String[list.size()]);
	}
    
	private void refreshThreeNumber(){
		database=mySqlHelper.getReadableDatabase();
		Cursor c=database.rawQuery("select round(sum(number),1) as sum from records where flag=? and year=? and month=? and day=?",new String[]{"支",nowYear+"",nowMonth+"",nowDay+""});
		float todayExpense=0f,todayIncome=0f,total=0f;
		if(c.moveToNext()){
			todayExpense = c.getFloat(c.getColumnIndex("sum"));
		}
		c=database.rawQuery("select round(sum(number),1) as sum from records where flag=? and year=? and month=? and day=?",new String[]{"收",nowYear+"",nowMonth+"",nowDay+""});
		if(c.moveToNext()){
			todayIncome = c.getFloat(c.getColumnIndex("sum"));
		}
		c=database.rawQuery("select round(sum(number),1) as income from records where flag = ? ",new String[]{"收"});
		
		if(c.moveToNext()){
			float a = c.getFloat(c.getColumnIndex("income"));
			c=database.rawQuery("select round(sum(number),1) as expense from records where flag = ? ",new String[]{"支"});
			if(c.moveToNext()){
				float b = c.getFloat(c.getColumnIndex("expense"));
				total = a - b ;
			}
			
		}
		txTodayExpense.setText(todayExpense+"");
		txTodayIncome.setText(todayIncome+"");
		txTotal.setText(total+"");
		database.close();
	}
	public boolean isEmpty(){
		database = mySqlHelper.getReadableDatabase();
		Cursor c = database.rawQuery("select * from records ", null);
		if(c.moveToNext())
			return false;
		else 
			return true;
	}
	
	//计算滑动的图片的位置
	private void initeCursor() {
		cursor = BitmapFactory
				.decodeResource(getResources(), R.drawable.cursor);
		bmWidth = cursor.getWidth();

		DisplayMetrics dm = getResources().getDisplayMetrics();
		// 设置图标的起始位置(3:3个textview。)
		offSet = (dm.widthPixels - 3 * bmWidth) / 6;
		// offSet = dm.widthPixels / 6 - bmWidth / 3;
		matrix.setTranslate(offSet, 0);
		imageView.setImageMatrix(matrix); // 需要imageView的scaleType为matrix
		currentItem = 0;
	}
	
	private void showChart(final PieChart pieChart, PieData pieData) {    
        pieChart.setHoleColorTransparent(true);    
        pieChart.setHoleRadius(60f);  //半径    
        pieChart.setTransparentCircleRadius(64f); // 半透明圈    
        pieChart.setDescription("");;    
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字    
        pieChart.setDrawHoleEnabled(true);    
        pieChart.setRotationAngle(90); // 初始旋转角度    
        pieChart.setRotationEnabled(true); // 可以手动旋转    
        pieChart.setCenterTextSize(30);
        //中间圆盘百分比
        pieChart.setHoleRadius(40f);
        //透明区域百分比
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setUsePercentValues(true);  //显示成百分比   
        pieChart.setCenterText(flag?"支出\n"+currentMonthExpense+"元":"收入\n"+currentMonthIncome+"元");  //饼状图中间的文字
        pieChart.setCenterTextSize(23);
        //设置数据    
        pieChart.setData(pieData);   
        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(1000, 1000);  //设置动画    
    }    
    
    private PieData getPieDataByDate(int toYear,int toMonth) {
    	ArrayList<String> tags = new ArrayList<String>();  //用来显示每个块儿的标签
        ArrayList<String> values = new ArrayList<String>();  //用来显示每个块儿的值
        ArrayList<Entry> items = new ArrayList<Entry>();  //显示类别名称
        ArrayList<Integer> colors = new ArrayList<Integer>();
        
        // 饼图颜色    
        colors.add(Color.rgb(217, 59, 101));    
        colors.add(Color.rgb(41, 167, 217));    
        colors.add(Color.rgb(162, 217, 43));    
        colors.add(Color.rgb(242, 217, 19)); 
        colors.add(Color.rgb(217, 26, 26)); 
        colors.add(Color.rgb(5, 184, 145)); 
        colors.add(Color.rgb(240, 129, 50));
        colors.add(Color.rgb(239, 94, 89));
        colors.add(Color.rgb(198, 224, 112));
        
        database=mySqlHelper.getReadableDatabase();
        float monthTotal=0f;
        Cursor c=null;
        c=database.rawQuery("select round(sum(number),1) as sum from records where year=? and month=? and flag=?", new String[]{currentYear+"",currentMonth+"",(flag?"支":"收")});
        if(c.moveToNext()){
        	monthTotal=c.getFloat(c.getColumnIndex("sum"));
        	if(monthTotal!=0f){
		    	String sql="select category,round(sum(number),1) as sum from records where flag=? and year=? and month=?  group by category having sum!=0";
		        if(flag){
		    		//支出
		        	c=database.rawQuery(sql, new String[]{"支",toYear+"",toMonth+""});
		    	}else {
		    		//收入
		    		c=database.rawQuery(sql, new String[]{"收",toYear+"",toMonth+""});
				}
		        while(c.moveToNext()){
		        	values.add(""+c.getFloat(c.getColumnIndex("sum")));
		        	tags.add(""+c.getString(c.getColumnIndex("category")));
		        }
		        //设置百分比
		        	for (int i = 0; i < tags.size(); i++) {
		        		float f = Float.parseFloat(values.get(i))/monthTotal;
		    			items.add(new Entry(f*100,i)); 
		    		}
            }else{
            	tags.add("暂无数据");
            	items.add(new Entry(100, 0));
            	
            }
        }
       
        //y轴的集合    
        PieDataSet pieDataSet = new PieDataSet(items, null);    
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离    
        pieDataSet.setValueTextSize(16);
        pieDataSet.setValueTextColor(Color.WHITE);
        
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float arg0) {
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
      		   float filesize = Float.parseFloat(df.format(arg0));//返回的是String类型的
				return filesize+"%";
			}
		});
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();    
        float px = 5 * (metrics.densityDpi / 160f);    
        pieDataSet.setSelectionShift(px); // 选中态多出的长度   
        PieData pieData = new PieData(tags, pieDataSet);
        return pieData;    
	}
    private void setcurrentMonthIncomeAndExpend(){
    	MySqlHelper helper=new MySqlHelper(MainActivity.this);
    	SQLiteDatabase database=helper.getReadableDatabase();
    	Cursor c=database.rawQuery("select round(sum(number),1) as sum from records where year=? and month=? and flag=?", new String[]{currentYear+"",currentMonth+"","收"});
    	if(c.moveToNext()){
    		currentMonthIncome=c.getFloat(c.getColumnIndex("sum"));
    		c=database.rawQuery("select round(sum(number),1) as sum from records where year=? and month=? and flag =? ", new String[]{currentYear+"",currentMonth+"","支"});
    		if(c.moveToNext()){
    			currentMonthExpense = c.getFloat(c.getColumnIndex("sum"));
    		}
    	}
    }
    private long nowTime=0,currentTime=0;
    @Override
	public void onBackPressed() {
		//super.onBackPressed();
		if(nowTime<=0){
			Toast.makeText(this, "再次按下返回键退出", Toast.LENGTH_SHORT).show();
			nowTime=System.currentTimeMillis();
		}else{
			currentTime=System.currentTimeMillis();
			if(currentTime-nowTime<=1300)
			{
				finish();
			}else {
				Toast.makeText(this, "再次按下返回键退出", Toast.LENGTH_SHORT).show();
				nowTime=System.currentTimeMillis();
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void initData() {
		System.out.println(1);
		file = new File(getSDPath() + "/kjz");
		makeDir(file);
		ExcelUtils.initExcel(file.toString() + "/bill.xls", title);
		ExcelUtils.writeObjListToExcel(getBillData(), getSDPath() + "/kjz/bill.xls", this);
	}

	private ArrayList<ArrayList<String>> getBillData() {
		bill2List=new ArrayList<ArrayList<String>>();
		MySqlHelper helper=new MySqlHelper(this);
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor c = database.rawQuery("select * from records",null);
		while (c.moveToNext()) {
			ArrayList<String> beanList=new ArrayList<String>();
			beanList.add(""+c.getInt(c.getColumnIndex("_id")));
			beanList.add(c.getString(c.getColumnIndex("date")));
			beanList.add(c.getString(c.getColumnIndex("time")));
			beanList.add(""+c.getInt(c.getColumnIndex("number")));
			beanList.add(c.getString(c.getColumnIndex("category")));
			beanList.add(c.getInt(c.getColumnIndex("number"))>0?"收入":"支出");
			bill2List.add(beanList);
		}
		c.close();
		return bill2List; 
	}

	public static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		String dir = sdDir.toString();
		return dir;

	}

	private boolean canSave(String[] data) {
		boolean isOk = false;
		for (int i = 0; i < data.length; i++) {
			if (i > 0 && i < data.length) {
				if (!TextUtils.isEmpty(data[i])) {
					isOk = true;
				}
			}
		}
		return isOk;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		OffersManager.getInstance(this).onAppExit();
	}
}
