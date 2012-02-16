package net.blanu.sneakermesh;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AndroidPeerDb implements PeerDb
{
	SQLiteDatabase db;
		
	public AndroidPeerDb(Context context)
	{
		PeerDbHelper helper=new PeerDbHelper(context);
		db=helper.getWritableDatabase();
	}
	
	public void putHave(String peer, String hash)
	{
		put("have", peer, hash);
	}
	
	public void deleteHave(String peer)
	{
		delete("have", peer);
	}
	
	public void deletePeerWant(String peer, String hash)
	{
		deletePeerHash("want", peer, hash);
	}
	
	public void deletePeerHave(String peer, String hash)
	{
		deletePeerHash("have", peer, hash);
	}
	
	public void putHave(String peer, Set<String> hashes)
	{
		put("have", peer, hashes);		
	}
	
	public List<String> getPeersThatHave(String hash)
	{
		return getPeersForHash("have", hash);
	}
	
	public List<String> getHave(String peer)
	{
		return getHashes("have", peer);
	}
	
	public List<String> getHaveExcept(String peer1, String peer2)
	{
		return getHashesExcept("have", peer1, peer2);
	}

	public void putWant(String peer, String hash)
	{
		put("want", peer, hash);		
	}
	
	public void putWant(String peer, Set<String> hashes)
	{
		put("want", peer, hashes);				
	}
	
	public void deleteWant(String peer)
	{
		delete("want", peer);
	}
	
	public List<String> getPeersThatWant(String hash)
	{
		return getPeersForHash("want", hash);		
	}
	
	public List<String> getWant(String peer)
	{
		return getHashes("want", peer);		
	}

	public List<String> getWantExcept(String peer1, String peer2)
	{
		return getHashesExcept("want", peer1, peer2);
	}	
	
	synchronized public void insertOrUpdate(String table, String keyName, String keyValue, String updateName, String updateValue)
	{
		String[] columns=new String[] {updateName};
		String selection=keyName+"=?";
		String[] selectionArgs=new String[] {keyValue};

		ContentValues values=new ContentValues();
		values.put(keyName, keyValue);
		values.put(updateName, updateValue);		
		
		Cursor cursor=db.query(table, columns, selection, selectionArgs, null, null, null);
		if(cursor.getCount()==0)
		{
			Log.e("AndroidPeerDb", "Inserting");
			
			db.insert(table, null, values);
		}
		else
		{
			Log.e("AndroidPeerDb", "Updating");
			
			db.update(table, values, selection, selectionArgs);
		}
	}

	synchronized public void insertOrUpdate(String table, String keyName, String keyValue, String updateName, long updateValue)
	{
		String[] columns=new String[] {updateName};
		String selection=keyName+"=?";
		String[] selectionArgs=new String[] {keyValue};

		ContentValues values=new ContentValues();
		values.put(keyName, keyValue);
		values.put(updateName, updateValue);		
		
		Cursor cursor=db.query(table, columns, selection, selectionArgs, null, null, null);
		if(cursor.getCount()==0)
		{
			
			db.insert(table, null, values);
		}
		else
		{
			db.update(table, values, selection, selectionArgs);
		}
	}	
	
	synchronized public void addPeer(String peer)
	{
		insertOrUpdate("peers", "peer", peer, "lastSeen", new Date().getTime());
	}
		
	synchronized public void put(String table, String peer, String hash)
	{
		ContentValues values=new ContentValues();
		values.put("peer", peer.toString());
		values.put("hash", hash);
		
		long result=db.insert(table, null, values);
		Log.e("AndroidPeerDb", "insert result: "+table+" "+values+" "+new Long(result).toString());
	}

	synchronized public void put(String table, String peer, Set<String> hashes)
	{
		for(String hash : hashes)
		{
			put(table, peer, hash);
		}
	}
	
	synchronized public void delete(String table, String peer)
	{
		String where="peer=?";
		String[] whereArgs=new String[] {peer};
		
		db.delete(table, where, whereArgs);
	}
	
	synchronized public void deletePeerHash(String table, String peer, String hash)
	{
		String where="peer=? and hash=?";
		String[] whereArgs=new String[] {peer, hash};
		
		db.delete(table, where, whereArgs);		
	}
	
	synchronized public List<String> getPeers()
	{
		List<String> results=new ArrayList<String>();

		String[] columns=new String[] {"peer"};
		
		Cursor cursor=db.query("peers", columns, null, null, null, null, null);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			String peer=cursor.getString(0);
			results.add(peer);
		}
		cursor.close();
		
		return results;
	}
	
	synchronized public List<String> getPeersForHash(String table, String hash)
	{
		List<String> results=new ArrayList<String>();

		String[] columns=new String[] {"peer"};
		String selection="hash=?";
		String[] selectionArgs=new String[] {hash};
		
		Cursor cursor=db.query(table, columns, selection, selectionArgs, null, null, null);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			String peer=cursor.getString(0);
			results.add(peer);
		}
		cursor.close();
		
		return results;
	}

	synchronized public List<String> getHashes(String table, String peer)
	{
		List<String> results=new ArrayList<String>();
		
		String[] selectionArgs=new String[] {peer};

		Log.e("AndroidPeerDb", "getHashes: "+table+" "+peer);
		Cursor cursor=db.rawQuery("select hash from "+table+" where peer=?", selectionArgs);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			String hash=cursor.getString(0);
			results.add(hash);
		}
		cursor.close();
		
		return results;
	}
	
	synchronized public List<String> getHashesExcept(String table, String peer1, String peer2)
	{
		List<String> results=new ArrayList<String>();

		String query="select hash from "+table+" where peer=? except select hash from "+table+" where peer=?";
		String[] selectionArgs=new String[] {peer1, peer2};
		
		Cursor cursor=db.rawQuery(query, selectionArgs);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			String hash=cursor.getString(0);
			results.add(hash);
		}
		cursor.close();
		
		return results;
	}
	
	synchronized public List<String> getWantAndHave(String peer1, String peer2)
	{
		List<String> results=new ArrayList<String>();

		String query="select hash from want where peer=? intersect select hash from have where peer=?";
		String[] selectionArgs=new String[] {peer1, peer2};
		
		Cursor cursor=db.rawQuery(query, selectionArgs);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			String hash=cursor.getString(0);
			results.add(hash);
		}
		cursor.close();
		
		return results;
	}		
}
