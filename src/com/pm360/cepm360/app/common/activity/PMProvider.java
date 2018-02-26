
package com.pm360.cepm360.app.common.activity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public final class PMProvider extends ContentProvider {

    private static final String TAG = "PMProvider";

    private static final String DB_NAME = "cepm.db";

    private static final int DB_VERSION = 1;

    private static final int DB_VERSION_NOP_UPGRADE_FROM = 0;

    private static final int DB_VERSION_NOP_UPGRADE_TO = 1;

    private static final String AUTHORITY = "com.pm360.cepm360.provider";

    private static final String PROJECT_LIST_TYPE = "vnd.android.cursor.dir/vnd.android.project";
    private static final String PROJECT_TYPE = "vnd.android.cursor.item/vnd.android.project";

    private static final String MESSAGE_LIST_TYPE = "vnd.android.cursor.dir/vnd.android.message";
    private static final String MESSAGE_TYPE = "vnd.android.cursor.item/vnd.android.message";

    private static final String TASK_LIST_TYPE = "vnd.android.cursor.dir/vnd.android.task";
    private static final String TASK_TYPE = "vnd.android.cursor.item/vnd.android.task";

    private static final String TASK_VERSION_LIST_TYPE = "vnd.android.cursor.dir/vnd.android.task_version";
    private static final String TASK_VERSION_TYPE = "vnd.android.cursor.item/vnd.android.task_version";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String[] TABLE_NAMES = new String[] {
            "project", "message", "task", "task_version"
    };

    private static final int PROJECT = 0;
    private static final int PROJECT_ID = 1;
    private static final int MESSAGE = 2;
    private static final int MESSAGE_ID = 3;
    private static final int TASK = 4;
    private static final int TASK_ID = 5;
    private static final int TASK_VERSION = 6;
    private static final int TASK_VERSION_ID = 7;

    private static final String _ID = "_id";

    static {
        sURIMatcher.addURI(AUTHORITY, "project", PROJECT);
        sURIMatcher.addURI(AUTHORITY, "project/#", PROJECT_ID);
        sURIMatcher.addURI(AUTHORITY, "message", MESSAGE);
        sURIMatcher.addURI(AUTHORITY, "message/#", MESSAGE_ID);
        sURIMatcher.addURI(AUTHORITY, "task", TASK);
        sURIMatcher.addURI(AUTHORITY, "task/#", TASK_ID);
        sURIMatcher.addURI(AUTHORITY, "task_version", TASK_VERSION);
        sURIMatcher.addURI(AUTHORITY, "task_version/#", TASK_VERSION_ID);
    }

    private SQLiteOpenHelper mOpenHelper = null;

    private final class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            createTable(db);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
            if (oldV == DB_VERSION_NOP_UPGRADE_FROM) {
                if (newV == DB_VERSION_NOP_UPGRADE_TO) {

                    return;
                }
                oldV = DB_VERSION_NOP_UPGRADE_TO;
            }
            Log.i(TAG, "Upgrading downloads database from version " + oldV + " to "
                    + newV + ", which will destroy all old data");
            dropTable(db);
            createTable(db);
        }

    }

    private void createTable(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + "project" + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "project_id" + " TEXT, "
                    + "project_name" + " TEXT, "
                    + "start_time" + " TEXT, "
                    + "end_time" + " TEXT, "
                    + "actual_start_time" + " TEXT, "
                    + "actual_end_time" + " TEXT, "
                    + "project_status" + " TEXT, "
                    + "progress" + " TEXT, "
                    + "owner" + " TEXT, "
                    + "department" + " TEXT, "
                    + "creater" + " TEXT, "
                    + "project_compare" + " TEXT DEFAULT 'nocompare'); ");

            db.execSQL("CREATE TABLE " + "message" + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "message_id" + " INTEGER, "
                    + "type_id" + " INTEGER, "
                    + "title" + " TEXT, "
                    + "type" + " INTEGER, "
                    + "user_id" + " INTEGER, "
                    + "time" + " TEXT, "
                    + "is_read" + " INTEGER, "
                    + "is_push" + " INTEGER, "
                    + "is_process" + " INTEGER, "
                    + "tab_type" + " INTEGER);");

            db.execSQL("CREATE TABLE " + "task_version" + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "change_id" + " TEXT, "
                    + "version_number" + " TEXT,"
                    + "version_select" + " TEXT); ");

            db.execSQL("CREATE TABLE " + "task" + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "task_id" + " TEXT, "
                    + "wbs_id" + " TEXT, "
                    + "task_name" + " TEXT, "
                    + "plan_duration" + " TEXT, "
                    + "start_time" + " TEXT, "
                    + "end_time" + " TEXT, "
                    + "status" + " TEXT, "
                    + "owner" + " TEXT, "
                    + "actual_duration" + " TEXT, "
                    + "actual_start_time" + " TEXT, "
                    + "actual_end_time" + " TEXT, "
                    + "cc_user_id" + " TEXT, "
                    + "creater" + " TEXT, "
                    + "type" + " TEXT, "
                    + "progress" + " TEXT, "
                    + "department" + " TEXT, "
                    + "parents_id" + " TEXT, "
                    + "has_child" + " TEXT, "
                    + "level" + " TEXT, "
                    + "expanded" + " TEXT); ");
        } catch (SQLException ex) {
            Log.e(TAG, "couldn't create table in downloads database");
            throw ex;
        }
    }

    private void dropTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + "project");
            db.execSQL("DROP TABLE IF EXISTS " + "message");
            db.execSQL("DROP TABLE IF EXISTS " + "task");
            db.execSQL("DROP TABLE IF EXISTS " + "task_version" +
                    "");
        } catch (SQLException ex) {
            Log.e(TAG, "couldn't drop table in downloads database");
            throw ex;
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case PROJECT: {
                return PROJECT_LIST_TYPE;
            }
            case PROJECT_ID: {
                return PROJECT_TYPE;
            }
            case MESSAGE: {
                return MESSAGE_LIST_TYPE;
            }
            case MESSAGE_ID: {
                return MESSAGE_TYPE;
            }
            case TASK: {
                return TASK_LIST_TYPE;
            }
            case TASK_ID: {
                return TASK_TYPE;
            }
            case TASK_VERSION: {
                return TASK_VERSION_LIST_TYPE;
            }
            case TASK_VERSION_ID: {
                return TASK_VERSION_TYPE;
            }
            default: {
                Log.d(TAG, "calling getType on an unknown URI: " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sURIMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            Log.d(TAG, "calling insert on an unknown/invalid URI: " + uri);
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
        String tabName = TABLE_NAMES[match / 2];
        Uri ret = null;
        if (match % 2 == 0) {
            long rowID = db.insert(tabName, null, values);
            if (rowID != -1) {
                ret = ContentUris.withAppendedId(uri, rowID);
                getContext().getContentResolver().notifyChange(uri, null);
            } else {
                Log.d(TAG, "couldn't insert into btopp database");
            }
        }
        return ret;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        int match = sURIMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            Log.d(TAG, "querying unknown URI: " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        String tabName = TABLE_NAMES[match / 2];
        if (match % 2 == 0) {
            qb.setTables(tabName);
        } else {
            qb.setTables(tabName);
            qb.appendWhere(_ID + "=");
            qb.appendWhere(uri.getPathSegments().get(1));
        }

        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (ret != null) {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
            Log.v(TAG, "created cursor " + ret + " on behalf of ");// +
        } else {
            Log.d(TAG, "query failed in downloads database");
        }

        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;
        long rowId = 0;

        int match = sURIMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            Log.d(TAG, "updating unknown/invalid URI: " + uri);
            throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }
        String tabName = TABLE_NAMES[match / 2];
        String myWhere;
        if (selection != null) {
            if (match % 2 == 0) {
                myWhere = "( " + selection + " )";
            } else {
                myWhere = "( " + selection + " ) AND ";
            }
        } else {
            myWhere = "";
        }
        if (match % 2 == 1) {
            String segment = uri.getPathSegments().get(1);
            rowId = Long.parseLong(segment);
            myWhere += " ( " + _ID + " = " + rowId + " ) ";
        }

        if (values.size() > 0) {
            count = db.update(tabName, values, myWhere, selectionArgs);
        } else {
            count = 0;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        int match = sURIMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            Log.d(TAG, "deleting unknown/invalid URI: " + uri);
            throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
        String tabName = TABLE_NAMES[match / 2];
        String myWhere;
        if (selection != null) {
            if (match % 2 == 0) {
                myWhere = "( " + selection + " )";
            } else {
                myWhere = "( " + selection + " ) AND ";
            }
        } else {
            myWhere = "";
        }
        if (match % 2 == 1) {
            String segment = uri.getPathSegments().get(1);
            long rowId = Long.parseLong(segment);
            myWhere += " ( " + _ID + " = " + rowId + " ) ";
        }
        count = db.delete(tabName, myWhere, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
