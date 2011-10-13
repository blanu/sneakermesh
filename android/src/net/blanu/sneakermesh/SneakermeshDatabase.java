package net.blanu.sneakermesh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SneakermeshDatabase extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    private static final String NAME = "sneakermesh";

    SneakermeshDatabase(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE hashes(id INTEGER PRIMARY KEY ASC, hash STRING, parent INTEGER)");
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}