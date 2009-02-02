/**
 * Copyright (C) 2009 Android OS Community Inc (http://androidos.cc/bbs).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.itcreator.android.reader.util;

import java.util.ArrayList;
import java.util.List;

import cn.itcreator.android.reader.domain.Book;
import cn.itcreator.android.reader.domain.BookMark;
import cn.itcreator.android.reader.paramter.Constant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * CoolReader Database operator
 * 
 * @author Wang XinFeng
 * @version  1.0
 * 
 */
public class CRDBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase sql = null;
	private boolean isopen = false;

	public CRDBHelper(Context c) {
		super(c, Constant.DB_NAME, null, Constant.DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String tag = "onCreate";
		Log.d(tag, "start create table");
		db.execSQL(Constant.CREATE_TABLE_BOOK);
		db.execSQL(Constant.CREATE_TABLE_BOOK_MARK);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/** save the book info to database */
	public int saveBook(Book b) {
		String tag = "saveBook";
		Log.d(tag, "query the book form database");
		Log.d(tag, "query the book path:" + b.getBookPath());
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
		}
		String[] col = new String[] { Constant.BOOK_ID, Constant.BOOK_PATH };
		Cursor cur = sql.query(Constant.BOOK, col, Constant.BOOK_PATH + "=\""
				+ b.getBookPath() + "\"", null, null, null, null);
		int c = cur.getCount();
		if (c == 0) {
			ContentValues values = new ContentValues();
			values.put(Constant.BOOK_PATH, b.getBookPath());
			cur.close();
			return (int) sql.insert(Constant.BOOK, null, values);
		} else {
			cur.moveToLast();
			int i = cur.getInt(0);
			cur.close();
			return i;
		}

	}

	/**
	 * ������ǩ�����ݿ���
	 * 
	 * @param bm
	 *            Ҫ�������ǩ
	 * @return ����ǩ�����ݿ��е�ID
	 */
	public boolean addBookMark(BookMark bm) {
		String tag = "addBookMark";
		Log.d(tag, "insert the book mark into database");
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
			Log.d(tag, "open the database...");
		}
		Log.d(tag, "constructor the new content values...");
		ContentValues values = new ContentValues();
		values.put(Constant.BOOK_ID, bm.getBookId());
		values.put(Constant.BOOK_MARK_NAME, bm.getMarkName());

		values.put(Constant.BOOK_MARK_OFFSET, bm.getCurrentOffset());
		values.put(Constant.Book_MARK_SAVETIME, bm.getSaveTime());
		Log.d(tag, "insert ...");
		long x = sql.insert(Constant.BOOK_MARK_TABLE_NAME, null, values);
		if (x > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ɾ����ǩ
	 * 
	 * @param bmId
	 *            ��ǩ��ID
	 * @return ɾ���Ƿ�ɹ�
	 */
	public boolean deleteBookMark(int bmId) {
		String tag = "deleteBookMark";
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
			Log.d(tag, "open the database...");
		}
		
		String s = "delete from " + Constant.BOOK_MARK_TABLE_NAME + " where "
				+ Constant.BOOK_MARK_ID + " =" + bmId;
		
		Log.d(tag, s);
		sql.execSQL(s);
		return true;
	}

	/**
	 * ��ѯĳ���鼮��������ǩ
	 * 
	 * @param bookId
	 * @return
	 */
	public List<BookMark> queryAllBookMark(int bookId) {
		String tag = "queryAllBookMark";
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
			Log.d(tag, "open the database...");
		}
		String[] columns = new String[] { Constant.BOOK_MARK_ID,
				Constant.BOOK_MARK_NAME, Constant.BOOK_MARK_OFFSET ,Constant.Book_MARK_SAVETIME};
		Log.d(tag, "query the book mark from database...");
		Cursor cur = sql.query(Constant.BOOK_MARK_TABLE_NAME, columns,
				Constant.BOOK_ID + "=\"" + bookId + "\"", null, null, null,
				Constant.BOOK_MARK_ID+" desc");
		Log.d(tag, "wrapper the book mark to the list...");
		List<BookMark> list = new ArrayList<BookMark>();
		while (cur.moveToNext()) {
			BookMark b = new BookMark();
			b.setBookMarkId(cur.getInt(0));
			b.setMarkName(cur.getString(1));
			b.setCurrentOffset(cur.getInt(2));
			b.setSaveTime(cur.getString(3));
			list.add(b);
		}

		Log.d(tag, "prepare return the book mark list");
		Log.d(tag, "book mark list size = "+list.size());
		cur.close();
		System.gc();
		return list;
	}

	/**
	 * ������鼮�����ݿ��е�����
	 * 
	 * @param bookName
	 * @return
	 */
	public boolean deleteBook(String bookName) {
		String tag = "deleteBook";
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
		}
		String[] col = new String[] { Constant.BOOK_ID, Constant.BOOK_PATH };
		Cursor cur = sql.query(Constant.BOOK, col, Constant.BOOK_PATH + "=\""
				+ bookName + "\"", null, null, null, null);

		Log.d(tag, "query the book info from the database....");

		int bookid = 0;
		while (cur.moveToNext()) {
			bookid = cur.getInt(0);
		}

		Log.d(tag, "delete all book mark of this book...");
		boolean result = true;
		result = clearAllBookMarkForBook(bookid);// �����ǩ
		if (result) {
			//����鼮
			String s = "delete from " + Constant.BOOK_TABLE_NAME + " where "
					+ Constant.BOOK_ID + "=" + bookid;
			Log.d(tag, s);
			sql.execSQL(s);
		}
		cur.close();
		return result;
	}

	/**
	 * ���ĳ�����ȫ����ǩ
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean clearAllBookMarkForBook(int bookId) {
		String tag = "clearAllBookMarkForBook";
		if (!isopen) {
			isopen = true;
			sql = getWritableDatabase();
			Log.d(tag, "open the database...");
		}

		Log.d(tag, "delete all book mark...");
		String s = "delete from " + Constant.BOOK_MARK_TABLE_NAME + " where "
				+ Constant.BOOK_ID + " =" + bookId;
		Log.d(tag, s);
		sql.execSQL(s);

		return true;
	}

	/** close the database */
	public void close() {
		String tag = "close";
		if (isopen) {
			sql.close();
			isopen = false;
		}
	}

}
