package com.example.kuaijizhang;


import java.util.ArrayList;
import java.util.List;

import org.itst.dropdown.DropdownButton;
import org.itst.dropdown.DropdownItemObject;
import org.itst.dropdown.DropdownListView;
import org.itst.sqlhelper.MySqlHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ShowDetailsActivity extends Activity {
	private static final int ID_TYPE_ALL = 0;
    private static final int ID_TYPE_OUT = 1;
    private static final int ID_TYPE_IN = 2;
    private static final String TYPE_ALL = "全部记录";
    private static final String TYPE_OUT = "支出记录";
    private static final String TYPE_IN = "收入记录";
    private static final int ID_LABEL_ALL = -1;
    private static final String LABEL_ALL = "全部类别";
    private static final String ORDER_REPLY_TIME = "记录时间排序";
    private static final String ORDER_NUMBER_SIZE = "金额大小排序";
    private static final int ID_ORDER_REPLY_TIME = 51;
    private static final int ID_ORDER_NUMBER_SIZE = 49;
    private int iPosition;
    private int total=0,incomeCount=0,expenseCount=0;
    private SQLiteDatabase database;
	private SimpleCursorAdapter cursorAdapter;
	private MySqlHelper mySqlHelper;
	private ListView listView;
	private String[] orderBy={"date","number"};
	private String[] inOrderBy={"date","number DESC"};
    View mask;
    DropdownButton btnType, btnCategory, btnOrder;
    DropdownListView dropdownType, dropdownCategory, dropdownOrder;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;
    
    private List<DropdownItemObject> categoryLabelsList;//记录分类标签的集合
    private List<DropdownItemObject> typeLabelsList;//记录类型标签的集合
    private List<DropdownItemObject> orderLabelsList;//排序标签的集合
    
    private DropdownButtonsController dropdownButtonsController = new DropdownButtonsController();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdetails);
		listView = (ListView) this.findViewById(R.id.ls_showdetails);

		mySqlHelper = new MySqlHelper(this);
		SQLiteDatabase sqLiteDatabase = mySqlHelper.getReadableDatabase();
		Cursor c = sqLiteDatabase.query("records", null, null, null, null,
				null, null);
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.details_cell, c,
				new String[] { "date", "category", "number" }, new int[] { R.id.tx_details_date,
						R.id.tx_details_category, R.id.tx_details_number});
		listView.setAdapter(cursorAdapter);
		
		 listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					iPosition=position;
					AlertDialog.Builder builder=new AlertDialog.Builder(ShowDetailsActivity.this);
					builder.setTitle("提示");
					builder.setMessage("确定要删除这条记录吗？");
					builder.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Cursor c = cursorAdapter.getCursor();
							c.moveToPosition(iPosition);
							int ItemId = c.getInt(c.getColumnIndex("_id"));
							deleteById(ItemId);
							refreshListView();
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
		
        mask = findViewById(R.id.mask);
        btnType = (DropdownButton) findViewById(R.id.chooseType);
        btnCategory = (DropdownButton) findViewById(R.id.chooseLabel);
        btnOrder = (DropdownButton) findViewById(R.id.chooseOrder);
        dropdownType = (DropdownListView) findViewById(R.id.dropdownType);
        dropdownCategory = (DropdownListView) findViewById(R.id.dropdownLabel);
        dropdownOrder = (DropdownListView) findViewById(R.id.dropdownOrder);

        dropdown_in = AnimationUtils.loadAnimation(this,R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_mask_out);
        flushCounts();
        dropdownButtonsController.init();
        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownButtonsController.hide();
                System.out.println("mask clicked");
            }
        });
        
        
		
	}
	
	private class DropdownButtonsController implements DropdownListView.Container {
        private DropdownListView currentDropdownList;
        
        @Override
        public void show(DropdownListView view) {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.setVisibility(View.GONE);
                currentDropdownList.button.setChecked(false);
            }
            currentDropdownList = view;
            mask.clearAnimation();
            mask.setVisibility(View.VISIBLE);
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_in);
            currentDropdownList.setVisibility(View.VISIBLE);
            currentDropdownList.button.setChecked(true);
        }

        @Override
        public void hide() {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.button.setChecked(false);
                mask.clearAnimation();
                mask.startAnimation(dropdown_mask_out);
            }
            currentDropdownList = null;
        }

        @Override
        public void onSelectionChanged(DropdownListView view) {
            System.out.println("onSelectionChanged");
            refreshListView();
        }
        
        void reset() {
            btnType.setChecked(false);
            btnCategory.setChecked(false);
            btnOrder.setChecked(false);

            dropdownType.setVisibility(View.GONE);
            dropdownCategory.setVisibility(View.GONE);
            dropdownOrder.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);
            dropdownType.clearAnimation();
            dropdownCategory.clearAnimation();
            dropdownOrder.clearAnimation();
            mask.clearAnimation();
        }

        void init() {
            reset();
            typeLabelsList=getTypeLabels();
            categoryLabelsList=getCategoryLabels();
            orderLabelsList=new ArrayList<DropdownItemObject>();
            orderLabelsList.add(new DropdownItemObject(22, ORDER_REPLY_TIME));
            orderLabelsList.add(new DropdownItemObject(23, ORDER_NUMBER_SIZE));
            
            dropdownType.bind(typeLabelsList, btnType, this, ID_TYPE_ALL);
            dropdownCategory.bind(categoryLabelsList, btnCategory, this, ID_LABEL_ALL);
            dropdownOrder.bind(orderLabelsList, btnOrder, this, ID_ORDER_REPLY_TIME);

            dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                	
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (currentDropdownList == null) {
                        reset();
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
	private ArrayList<DropdownItemObject> getTypeLabels(){
    	ArrayList<DropdownItemObject> labelsList = new ArrayList<DropdownItemObject>();
    	labelsList.add(new DropdownItemObject(0,"全部记录",total));
    	labelsList.add(new DropdownItemObject(1, "收入记录", incomeCount));
    	labelsList.add(new DropdownItemObject(2, "支出记录", expenseCount));
    	return labelsList;
    }
	
	//获得类别标签对象的集合类别标签
    private ArrayList<DropdownItemObject> getCategoryLabels() {
    	ArrayList<DropdownItemObject> labelsList = new ArrayList<DropdownItemObject>();
    	mySqlHelper = new MySqlHelper(ShowDetailsActivity.this);
		
    	SQLiteDatabase database = mySqlHelper.getReadableDatabase();
    	String sql="select count(category) as count,category from records group by category";
    	int i=1;
    	Cursor c=database.rawQuery(sql, null);
		while(c.moveToNext()){
			DropdownItemObject topicLabelObject =  new DropdownItemObject(i,c.getString(c.getColumnIndex("category")),c.getInt(c.getColumnIndex("count")));
			labelsList.add(topicLabelObject);
	        i++;
		}
		
    	DropdownItemObject labelObject =  new DropdownItemObject(i,"全部类别",total);
    	labelsList.add(0,labelObject);
    	database.close();
    	return labelsList;
    }
    private void refreshListView() {
    	String category=dropdownCategory.currentItemObject.text;
        int typeId=dropdownType.currentItemObject.id;
        int orderId=dropdownOrder.currentItemObject.id;
        
        System.out.println(category+"typeid:"+typeId+"orderId:"+orderId);
        
        MySqlHelper  helper=new MySqlHelper(ShowDetailsActivity.this);
        database = helper.getReadableDatabase();
        Cursor c;
        if(typeId==0){
        	//全部记录  按类别排序，排序方式了
        	if(category.equals("全部类别"))
        		c=database.query("records",null, null, null, null, null, orderBy[orderId==22?0:1]);
        	else
        		c=database.query("records",null, null, null, null, null, "category");
        }else if(typeId==1){
        	//收入记录
        	if(category.equals("全部类别"))
        		c=database.query("records",null, "number>0",null, null, null, inOrderBy[orderId==22?0:1]);
        	else
        		c=database.query("records",null, "category=? and number>0", new String[]{category}, null, null, inOrderBy[orderId==22?0:1]);
        }else{
        	//支出记录
        	if(category.equals("全部类别"))
        		c=database.query("records",null, " number<0", null, null, null, orderBy[orderId==22?0:1]);
        	else
        		c=database.query("records",null, "category=? and number<0", new String[]{category}, null, null, orderBy[orderId==22?0:1]);
        }
        
		cursorAdapter.changeCursor(c);
		database.close();
	}
	public void flushCounts() {
    	mySqlHelper = new MySqlHelper(ShowDetailsActivity.this);
		SQLiteDatabase database = mySqlHelper.getReadableDatabase();
    	
		String sql="select count(*) as count  from records";
        Cursor c=database.rawQuery(sql, null);
        c.moveToNext();
        total=c.getInt(c.getColumnIndex("count"));
        
        sql="select count(*) as count  from records  where number>0";
        c=database.rawQuery(sql, null);
        c.moveToNext();
        incomeCount=c.getInt(c.getColumnIndex("count"));
        
        sql="select count(*) as count  from records  where number<0";
        c=database.rawQuery(sql, null);
        c.moveToNext();
        expenseCount=c.getInt(c.getColumnIndex("count"));
        database.close();
    }
	
	private void deleteById(int id) {
		mySqlHelper=new MySqlHelper(this);
		database = mySqlHelper.getReadableDatabase();
		database.delete("records", "_id=?", new String[]{id+""});
		database.close();
	}
}
