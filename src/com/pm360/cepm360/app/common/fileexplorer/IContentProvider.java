package com.pm360.cepm360.app.common.fileexplorer;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface IContentProvider extends IInterface {
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) throws RemoteException;

	public String getType(Uri url) throws RemoteException;

	public Uri insert(Uri url, ContentValues initialValues)
			throws RemoteException;

	public int bulkInsert(Uri url, ContentValues[] initialValues)
			throws RemoteException;

	public int delete(Uri url, String selection, String[] selectionArgs)
			throws RemoteException;

	public int update(Uri url, ContentValues values, String selection,
			String[] selectionArgs) throws RemoteException;

	public ParcelFileDescriptor openFile(Uri url, String mode)
			throws RemoteException, FileNotFoundException;

	public AssetFileDescriptor openAssetFile(Uri url, String mode)
			throws RemoteException, FileNotFoundException;

	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws RemoteException, OperationApplicationException;

	public Bundle call(String method, String arg, Bundle extras)
			throws RemoteException;

	// Data interchange.
	public String[] getStreamTypes(Uri url, String mimeTypeFilter)
			throws RemoteException;

	public AssetFileDescriptor openTypedAssetFile(Uri url, String mimeType,
			Bundle opts) throws RemoteException, FileNotFoundException;

	/* IPC constants */
	static final String descriptor = "android.content.IContentProvider";

	static final int QUERY_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION;
	static final int GET_TYPE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 1;
	static final int INSERT_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 2;
	static final int DELETE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 3;
	static final int UPDATE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 9;
	static final int BULK_INSERT_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 12;
	static final int OPEN_FILE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 13;
	static final int OPEN_ASSET_FILE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 14;
	static final int APPLY_BATCH_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 19;
	static final int CALL_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 20;
	static final int GET_STREAM_TYPES_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 21;
	static final int OPEN_TYPED_ASSET_FILE_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION + 22;
}