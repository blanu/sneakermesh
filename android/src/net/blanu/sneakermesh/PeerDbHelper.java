package net.blanu.sneakermesh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PeerDbHelper extends SQLiteOpenHelper
{
	PeerDbHelper(Context context) {
		super(context, "sneakermesh", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE peers (peer VARCHAR(15), lastSeenPeer DATE);";
		db.execSQL(createStatement);

		createStatement = "CREATE TABLE have (peer VARCHAR(15), hash VARCHAR(20));";
		db.execSQL(createStatement);
		
		createStatement = "CREATE TABLE want (peer VARCHAR(15), hash VARCHAR(20));";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}