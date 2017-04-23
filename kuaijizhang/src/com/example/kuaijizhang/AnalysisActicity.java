package com.example.kuaijizhang;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.itst.scrollview.ObservableScrollView;
import org.itst.scrollview.ScrollViewListener;
import org.itst.sqlhelper.MySqlHelper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ScrollView;
import android.widget.TextView;

public class AnalysisActicity extends Activity implements ScrollViewListener{
	private BarChart incomeBarChart,expenseBarChart; 
	private static final int INCOME_BAR_DATA=0;
	private static final int EXPENSE_BAR_DATA=1;
	
    private BarData mBarData; 
	private Calendar c;
	private int nowMonth,nowYear,nowDay;
	private ObservableScrollView scrollView1 = null;  
	private boolean runDelay=true;
	private float incomePie=0,expensePie=0;
	private ArrayList<Integer> colors = new ArrayList<Integer>();  
	private MySqlHelper mySqlHelper;
	private SQLiteDatabase database;
	private float total,expense,income;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis);
		//初始化日期
		Calendar calendar = Calendar.getInstance();//首先要获取日历对象
		nowMonth=calendar.get(Calendar.MONTH)+1;
		nowYear=calendar.get(Calendar.YEAR); 
		nowDay=calendar.get(Calendar.DATE);
		
		// 颜色    
        colors.add(Color.rgb(217, 59, 101));    
        colors.add(Color.rgb(41, 167, 217));    
        colors.add(Color.rgb(162, 217, 43));    
        colors.add(Color.rgb(242, 217, 19)); 
        colors.add(Color.rgb(217, 26, 26)); 
        colors.add(Color.rgb(5, 184, 145)); 
        colors.add(Color.rgb(240, 129, 50));
        colors.add(Color.rgb(239, 94, 89));
        colors.add(Color.rgb(198, 224, 112));
        
		incomeBarChart = (BarChart) this.findViewById(R.id.income_bar_chart);  
		expenseBarChart = (BarChart) this.findViewById(R.id.expense_bar_chart);  
		
		init();
		TextView txTotal=(TextView) this.findViewById(R.id.tx_analysis_total);
		TextView txIncome=(TextView) this.findViewById(R.id.tx_analysis_income);
		TextView txExpense=(TextView) this.findViewById(R.id.tx_analysis_outcome);
		txTotal.setText(total+"");
		txIncome.setText(income+"");
		txExpense.setText((expense)+"");
		incomePie=income;
		expensePie=expense;
		database.close();
		scrollView1 = (ObservableScrollView) findViewById(R.id.scrollView); 
		scrollView1.setScrollViewListener(this);
		//显示 支出条形图
		mBarData = getBarData(EXPENSE_BAR_DATA);  
        showBarChart(expenseBarChart, mBarData); 
        incomeBarChart.animateXY(0, 2000); 
	} 
	public void init() {
		//初始化 结余，总收入，总支出
		mySqlHelper=new MySqlHelper(this);
		database=mySqlHelper.getReadableDatabase();
		String sql="select sum(number) as sum from records where year=? and flag=?";
		Cursor c=database.rawQuery(sql, new String[]{nowYear+""});
		c.moveToNext();
		total=c.getFloat(c.getColumnIndex("sum"));
		c=database.rawQuery(sql, new String[]{nowYear+"","支"});
		c.moveToNext();
		expense=c.getFloat(c.getColumnIndex("sum"));
		c=database.rawQuery(sql, new String[]{nowYear+"","收"});
		c.moveToNext();
		income=c.getFloat(c.getColumnIndex("sum"));
		
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		System.out.println(y);
		if(runDelay){
			if(y>=700){
				mBarData = getBarData(INCOME_BAR_DATA);  
		        showBarChart(incomeBarChart, mBarData); 
		        incomeBarChart.animateXY(0, 2000); 
		        runDelay=false;
			}
		}
		
		
	}
	private void showBarChart(BarChart barChart, BarData barData) {  
        barChart.setDrawBorders(false);  ////是否在折线图上添加边框   
        barChart.setDescription("");// 数据描述      
//        barChart.setDescriptionPosition(200, 10);
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView      
        barChart.setNoDataTextDescription("暂时没有数据哦");      
                 
        barChart.setDrawGridBackground(false); // 是否显示表格颜色      
        
        barChart.setTouchEnabled(false); // 设置是否可以触摸      
       
        barChart.setDragEnabled(false);// 是否可以拖拽      
        barChart.setScaleEnabled(false);// 是否可以缩放      
      
        barChart.setPinchZoom(false);//       
        barChart.setClickable(true);
        barChart.setBackgroundColor(Color.WHITE);// 设置背景      
          
        barChart.setDrawBarShadow(false);  
        barChart.setVerticalFadingEdgeEnabled(false);
        barChart.setData(barData); // 设置数据      
        
        barChart.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴
        barChart.getXAxis().setPosition(XAxisPosition.BOTTOM); // 让x轴在下面
        barChart.getLegend().setEnabled(false);
        
    }  
  
    private BarData getBarData(int kind) {  
    	ArrayList<String> xValues = new ArrayList<String>(); //x标签
    	ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();  //y轴数据
    	
    	MySqlHelper helper = new MySqlHelper(AnalysisActicity.this);
    	SQLiteDatabase database=helper.getReadableDatabase();
    	int i=0;
    	String sql="select category,sum(number) as sum from records where flag=? and year=? group by category";
    	Cursor c=database.rawQuery(sql,new String[]{(kind==INCOME_BAR_DATA?"收":"支"),nowYear+""});
    	while(c.moveToNext()){
    		xValues.add(c.getString(c.getColumnIndex("category")));
    		float k=c.getFloat(c.getColumnIndex("sum"));
			DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
   		   	float filesize = Float.parseFloat(df.format(k));//返回的是String类型的
    		yValues.add(new BarEntry(filesize, i));   
    		i++;
    	}
        // y轴的数据集合      
        BarDataSet barDataSet = new BarDataSet(yValues, "");   
        barDataSet.setColors(colors);  
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();      
        barDataSets.add(barDataSet); //       
        BarData barData = new BarData(xValues, barDataSets);  
        return barData;  
    }  
}
