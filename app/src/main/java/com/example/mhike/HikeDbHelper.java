package com.example.mhike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class HikeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mhike_database.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_HIKES = "hikes";
    private static final String H_COL_ID = "_id";
    private static final String H_COL_NAME = "name";
    private static final String H_COL_LOCATION = "location";
    private static final String H_COL_DATE = "date";
    private static final String H_COL_PARKING = "parking";
    private static final String H_COL_LENGTH = "length";
    private static final String H_COL_DIFFICULTY = "difficulty";
    private static final String H_COL_DESCRIPTION = "description";
    private static final String H_COL_WEATHER = "weather";
    private static final String H_COL_EQUIPMENT = "equipment";
    // Thêm cột user_id vào bảng hikes
    private static final String H_COL_USER_ID = "user_id";

    public static final String TABLE_OBSERVATIONS = "observations";
    private static final String O_COL_ID = "_id";
    private static final String O_COL_HIKE_ID = "hike_id";
    private static final String O_COL_OBSERVATION = "observation";
    private static final String O_COL_TIME = "time";
    private static final String O_COL_COMMENTS = "comments";
    private static final String O_COL_IMAGE_URI = "image_uri";

    private static final String TABLE_USERS = "users";
    private static final String USER_COL_ID = "u_id";
    private static final String USER_COL_USERNAME = "username";
    private static final String USER_COL_PASSWORD = "password";


    public HikeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HIKES_TABLE = "CREATE TABLE " + TABLE_HIKES + " (" +
                H_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                H_COL_NAME + " TEXT NOT NULL, " +
                H_COL_LOCATION + " TEXT NOT NULL, " +
                H_COL_DATE + " TEXT NOT NULL, " +
                H_COL_PARKING + " TEXT NOT NULL, " +
                H_COL_LENGTH + " TEXT NOT NULL, " +
                H_COL_DIFFICULTY + " TEXT NOT NULL, " +
                H_COL_DESCRIPTION + " TEXT, " +
                H_COL_WEATHER + " TEXT, " +
                H_COL_EQUIPMENT + " TEXT, " +
                H_COL_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + H_COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_COL_ID + "))";
        db.execSQL(CREATE_HIKES_TABLE);

        String CREATE_OBSERVATIONS_TABLE = "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                O_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                O_COL_HIKE_ID + " INTEGER NOT NULL, " +
                O_COL_OBSERVATION + " TEXT NOT NULL, " +
                O_COL_TIME + " TEXT NOT NULL, " +
                O_COL_COMMENTS + " TEXT, " +
                O_COL_IMAGE_URI + " TEXT, " +
                "FOREIGN KEY(" + O_COL_HIKE_ID + ") REFERENCES " +
                TABLE_HIKES + "(" + H_COL_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_OBSERVATIONS_TABLE);

        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COL_USERNAME + " TEXT UNIQUE, " +
                USER_COL_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_COL_USERNAME, username);
        cv.put(USER_COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { USER_COL_ID };
        String selection = USER_COL_USERNAME + " = ?" + " AND " + USER_COL_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { USER_COL_ID };
        String selection = USER_COL_USERNAME + " = ?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { USER_COL_ID };
        String selection = USER_COL_USERNAME + " = ?";
        String[] selectionArgs = { username };
        long userId = -1;

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(USER_COL_ID));
            cursor.close();
        }
        db.close();
        return userId;
    }

    public long addHike(Hike hike, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(H_COL_NAME, hike.getName());
        cv.put(H_COL_LOCATION, hike.getLocation());
        cv.put(H_COL_DATE, hike.getDate());
        cv.put(H_COL_PARKING, hike.getParking());
        cv.put(H_COL_LENGTH, hike.getLengthKm());
        cv.put(H_COL_DIFFICULTY, hike.getDifficulty());
        cv.put(H_COL_DESCRIPTION, hike.getDescription());
        cv.put(H_COL_WEATHER, hike.getWeather());
        cv.put(H_COL_EQUIPMENT, hike.getEquipment());
        cv.put(H_COL_USER_ID, userId);

        long id = db.insert(TABLE_HIKES, null, cv);
        db.close();
        return id;
    }

    public int updateHike(Hike hike) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(H_COL_NAME, hike.getName());
        cv.put(H_COL_LOCATION, hike.getLocation());
        cv.put(H_COL_DATE, hike.getDate());
        cv.put(H_COL_PARKING, hike.getParking());
        cv.put(H_COL_LENGTH, hike.getLengthKm());
        cv.put(H_COL_DIFFICULTY, hike.getDifficulty());
        cv.put(H_COL_DESCRIPTION, hike.getDescription());
        cv.put(H_COL_WEATHER, hike.getWeather());
        cv.put(H_COL_EQUIPMENT, hike.getEquipment());

        int rows = db.update(TABLE_HIKES, cv, H_COL_ID + "=?", new String[]{String.valueOf(hike.getId())});
        db.close();
        return rows;
    }

    public List<Hike> getAllHikes(long userId) {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = H_COL_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor c = db.query(TABLE_HIKES, null, selection, selectionArgs, null, null, H_COL_DATE + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                Hike h = cursorToHike(c);
                list.add(h);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public Hike getHike(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HIKES, null, H_COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Hike h = null;
        if (c != null && c.moveToFirst()) {
            h = cursorToHike(c);
            c.close();
        }
        db.close();
        return h;
    }

    public int deleteHike(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_HIKES, H_COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteAllHikes(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HIKES, H_COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public List<Hike> advancedSearch(String name, String location, String length, String date, long userId) {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder whereClause = new StringBuilder(H_COL_USER_ID + " = ?");
        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(userId));

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND ").append(H_COL_NAME).append(" LIKE ?");
            whereArgs.add("%" + name + "%");
        }
        if (location != null && !location.isEmpty()) {
            whereClause.append(" AND ").append(H_COL_LOCATION).append(" LIKE ?");
            whereArgs.add("%" + location + "%");
        }
        if (length != null && !length.isEmpty()) {
            whereClause.append(" AND ").append(H_COL_LENGTH).append(" = ?");
            whereArgs.add(length);
        }
        if (date != null && !date.isEmpty()) {
            whereClause.append(" AND ").append(H_COL_DATE).append(" = ?");
            whereArgs.add(date);
        }

        Cursor c = db.query(TABLE_HIKES, null, whereClause.toString(),
                whereArgs.toArray(new String[0]), null, null, H_COL_DATE + " DESC");

        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToHike(c));
            }
            c.close();
        }
        db.close();
        return list;
    }

    private Hike cursorToHike(Cursor c) {
        Hike h = new Hike();
        h.setId(c.getLong(c.getColumnIndexOrThrow(H_COL_ID)));
        h.setName(c.getString(c.getColumnIndexOrThrow(H_COL_NAME)));
        h.setLocation(c.getString(c.getColumnIndexOrThrow(H_COL_LOCATION)));
        h.setDate(c.getString(c.getColumnIndexOrThrow(H_COL_DATE)));
        h.setParking(c.getString(c.getColumnIndexOrThrow(H_COL_PARKING)));
        h.setLengthKm(c.getString(c.getColumnIndexOrThrow(H_COL_LENGTH)));
        h.setDifficulty(c.getString(c.getColumnIndexOrThrow(H_COL_DIFFICULTY)));
        h.setDescription(c.getString(c.getColumnIndexOrThrow(H_COL_DESCRIPTION)));
        h.setWeather(c.getString(c.getColumnIndexOrThrow(H_COL_WEATHER)));
        h.setEquipment(c.getString(c.getColumnIndexOrThrow(H_COL_EQUIPMENT)));
        return h;
    }

    public Observation getObservation(long observationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Observation observation = null;

        Cursor cursor = db.query(
                TABLE_OBSERVATIONS,
                null,
                O_COL_ID + "=?",
                new String[]{String.valueOf(observationId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            observation = new Observation(
                    cursor.getLong(cursor.getColumnIndexOrThrow(O_COL_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(O_COL_HIKE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(O_COL_OBSERVATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(O_COL_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(O_COL_COMMENTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(O_COL_IMAGE_URI))
            );
            cursor.close();
        }

        db.close();
        return observation;
    }

    public int updateObservation(Observation o) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(O_COL_TIME, o.getTime());
        values.put(O_COL_COMMENTS, o.getComments());
        values.put(O_COL_IMAGE_URI, o.getImageUri());

        String selection = O_COL_ID + " = ?";
        String[] selectionArgs = { String.valueOf(o.getId()) };

        int rowsAffected = db.update(
                TABLE_OBSERVATIONS,
                values,
                selection,
                selectionArgs
        );

        db.close();
        return rowsAffected;
    }

    public long addObservation(long hikeId, String observation, String time, String comments, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(O_COL_HIKE_ID, hikeId);
        cv.put(O_COL_OBSERVATION, observation);
        cv.put(O_COL_TIME, time);
        cv.put(O_COL_COMMENTS, comments);
        cv.put(O_COL_IMAGE_URI, imageUri);

        long id = db.insert(TABLE_OBSERVATIONS, null, cv);
        db.close();
        return id;
    }


    public List<Observation> getObservationsByHike(long hikeId) {
        List<Observation> observationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = O_COL_HIKE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(hikeId) };

        Cursor cursor = db.query(
                TABLE_OBSERVATIONS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                O_COL_TIME + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Observation observation = new Observation(
                        cursor.getLong(cursor.getColumnIndexOrThrow(O_COL_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(O_COL_HIKE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(O_COL_OBSERVATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(O_COL_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(O_COL_COMMENTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(O_COL_IMAGE_URI))
                );
                observationList.add(observation);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return observationList;
    }
    public void deleteObservation(long observationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_OBSERVATIONS,O_COL_ID + " = ?",
                new String[]{String.valueOf(observationId)}
        );
        db.close();
    }
}
