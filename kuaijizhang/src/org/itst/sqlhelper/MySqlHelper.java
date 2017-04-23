package org.itst.sqlhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlHelper extends SQLiteOpenHelper {

	public MySqlHelper(Context context){
		super(context, "database", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table records(_id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT,number REAL,year INTEGER,month INTEGER,day INTEGER,time TEXT,date Text,flag TEXT)");
		db.execSQL("create table categories(_id INTEGER PRIMARY KEY AUTOINCREMENT,category_name TEXT UNIQUE,type TEXT)");
		db.execSQL("insert into categories(category_name,type) values('生活费用','income')");
		db.execSQL("insert into categories(category_name,type) values('工资收入','income')");
		db.execSQL("insert into categories(category_name,type) values('兼职收入','income')");
		db.execSQL("insert into categories(category_name,type) values('奖金收入','income')");
		db.execSQL("insert into categories(category_name,type) values('利息收入','income')");
		db.execSQL("insert into categories(category_name,type) values('其他收入','income')");
		
		db.execSQL("insert into categories(category_name,type) values('早午晚餐','expense')");
		db.execSQL("insert into categories(category_name,type) values('零食酒水','expense')");
		db.execSQL("insert into categories(category_name,type) values('休闲娱乐','expense')");
		db.execSQL("insert into categories(category_name,type) values('学习书刊','expense')");
		db.execSQL("insert into categories(category_name,type) values('美容美发','expense')");
		db.execSQL("insert into categories(category_name,type) values('服饰用品','expense')");
		db.execSQL("insert into categories(category_name,type) values('出行交通','expense')");
		db.execSQL("insert into categories(category_name,type) values('交流通讯','expense')");
		db.execSQL("insert into categories(category_name,type) values('求医买药','expense')");
		db.execSQL("insert into categories(category_name,type) values('数码百货','expense')");
		db.execSQL("insert into categories(category_name,type) values('其他支出','expense')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
