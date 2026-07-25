package com.example.myfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "genini.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String TABLE_CONTACTS = "emergency_contacts";
    private static final String TABLE_WEEKS = "pregnancy_weeks";

    // 2. الـ Constructor التقليدي (بدون Singleton)
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                "phone TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "password_salt TEXT NOT NULL, " +
                "last_period_date TEXT, " +
                "due_date TEXT, " +
                "profile_image_path TEXT)";

        String createAppointmentsTable =
                "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "doctor_name TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id) ON DELETE CASCADE)";

        String createContactsTable =
                "CREATE TABLE " + TABLE_CONTACTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id) ON DELETE CASCADE)";

        String createWeeksTable =
                "CREATE TABLE " + TABLE_WEEKS + " (" +
                "week_number INTEGER PRIMARY KEY, " +
                "size_label TEXT NOT NULL, " +
                "development TEXT NOT NULL, " +
                "tip_one TEXT NOT NULL, " +
                "tip_two TEXT NOT NULL, " +
                "tip_three TEXT NOT NULL, " +
                "source_url TEXT NOT NULL)";

        db.execSQL(createUsersTable);
        db.execSQL(createAppointmentsTable);
        db.execSQL(createContactsTable);
        db.execSQL(createWeeksTable);
        seedPregnancyWeeks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }



    public long registerUser(String name, String email, String phone, String password) {

        SQLiteDatabase db = getWritableDatabase();

        String salt = PasswordUtils.newSalt();
        String hash = PasswordUtils.hash(password, salt);

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email.trim().toLowerCase());
        values.put("phone", phone);
        values.put("password_hash", hash);
        values.put("password_salt", salt);

        long result = db.insert(TABLE_USERS, null, values);

        return result;
    }

    public boolean isEmailRegistered(String email) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"id"}, "email = ?", new String[]{email.trim().toLowerCase()}, null, null, null);

        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = true;
        }

        cursor.close();
        return exists;
    }

    public long authenticate(String email, String password) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"id", "password_hash", "password_salt"}, "email = ?", new String[]{email.trim().toLowerCase()}, null, null, null);

        long userId = -1;

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String hash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
            String salt = cursor.getString(cursor.getColumnIndexOrThrow("password_salt"));
            if (PasswordUtils.matches(password, salt, hash)) {
                userId = id;
            } else {
                userId = -2;
            }
        }

        cursor.close();
        return userId;
    }

    public User getUser(long userId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String lastPeriod = cursor.getString(cursor.getColumnIndexOrThrow("last_period_date"));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_path"));

            user = new User(id, name, email, phone, lastPeriod, dueDate, imagePath);
        }

        cursor.close();
        return user;
    }

    public boolean updateProfile(long userId, String name, String phone, String lastPeriodDate, String dueDate, String profileImagePath) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("last_period_date", lastPeriodDate);
        values.put("due_date", dueDate);
        values.put("profile_image_path", profileImagePath);
        int result = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePregnancyDates(long userId, String lastPeriodDate, String dueDate) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_period_date", lastPeriodDate);
        values.put("due_date", dueDate);
        int result = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verifyPassword(long userId, String password) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"password_hash", "password_salt"}, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        boolean verified = false;
        if (cursor.moveToFirst()) {
            String hash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
            String salt = cursor.getString(cursor.getColumnIndexOrThrow("password_salt"));
            if (PasswordUtils.matches(password, salt, hash)) {
                verified = true;
            }
        }
        cursor.close();
        return verified;
    }

    public boolean updatePassword(long userId, String newPassword) {

        SQLiteDatabase db = getWritableDatabase();
        String salt = PasswordUtils.newSalt();
        String hash = PasswordUtils.hash(newPassword, salt);
        ContentValues values = new ContentValues();
        values.put("password_hash", hash);
        values.put("password_salt", salt);
        int result = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }


    public long insertAppointment(Appointment appointment) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", appointment.getUserId());
        values.put("title", appointment.getTitle());
        values.put("doctor_name", appointment.getDoctorName());
        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("location", appointment.getLocation());
        values.put("phone", appointment.getPhone());

        long result = db.insert(TABLE_APPOINTMENTS, null, values);

        return result;
    }

    public ArrayList<Appointment> getAppointments(long userId) {

        ArrayList<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "date ASC, time ASC");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long ownerId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            Appointment app = new Appointment(id, ownerId, title, doctor, date, time, location, phone);
            appointments.add(app);
        }
        cursor.close();
        return appointments;
    }

    public Appointment getAppointment(long appointmentId, long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, "id = ? AND user_id = ?", new String[]{String.valueOf(appointmentId), String.valueOf(userId)}, null, null, null);
        Appointment appointment = null;
        if (cursor.moveToFirst()) {

            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long ownerId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));

            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            appointment = new Appointment(id, ownerId, title, doctor, date, time, location, phone);
        }
        cursor.close();
        return appointment;
    }

    public boolean updateAppointment(Appointment appointment) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", appointment.getTitle());
        values.put("doctor_name", appointment.getDoctorName());
        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("location", appointment.getLocation());
        values.put("phone", appointment.getPhone());

        int result = db.update(TABLE_APPOINTMENTS, values, "id = ? AND user_id = ?", new String[]{String.valueOf(appointment.getId()), String.valueOf(appointment.getUserId())});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteAppointment(long appointmentId, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_APPOINTMENTS, "id = ? AND user_id = ?", new String[]{String.valueOf(appointmentId), String.valueOf(userId)});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Appointment> getUpcomingAppointments(long userId, int limit) {

        ArrayList<Appointment> appointments = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date());
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, "user_id = ? AND date >= ?", new String[]{String.valueOf(userId), today}, null, null, "date ASC, time ASC", String.valueOf(limit));

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long ownerId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            Appointment app = new Appointment(id, ownerId, title, doctor, date, time, location, phone);
            appointments.add(app);
        }

        cursor.close();
        return appointments;
    }

    public long insertContact(long userId, String name, String phone) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("phone", phone);
        long result = db.insert(TABLE_CONTACTS, null, values);
        return result;
    }

    public ArrayList<EmergencyContact> getContacts(long userId) {
        ArrayList<EmergencyContact> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "id DESC");
        while (cursor.moveToNext()) {

            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long ownerId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            EmergencyContact contact = new EmergencyContact(id, ownerId, name, phone);
            contacts.add(contact);
        }

        cursor.close();
        return contacts;
    }

    public boolean deleteContact(long contactId, long userId) {

        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_CONTACTS, "id = ? AND user_id = ?", new String[]{String.valueOf(contactId), String.valueOf(userId)});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * طريقة تعليمية تقليدية لتعبئة بيانات الحمل في قاعدة البيانات.
     * في المشاريع التعليمية، نستخدم ContentValues لإضافة البيانات بشكل منظم لكل أسبوع.
     * تم تضمين الـ 40 أسبوعاً كاملة (التطور والحجم والنصائح) لضمان شمولية التطبيق.
     */
    private void seedPregnancyWeeks(SQLiteDatabase db) {
        // نستخدم حلقة تكرار تعليمية تمر على كل أسبوع (من الأسبوع 1 إلى 40)
        // هذا يضمن أن قاعدة البيانات ستحتوي على جميع سجلات تطور الجنين
        for (int i = 1; i <= 40; i++) {
            // 1. استخراج البيانات التفصيلية من كلاس المرافق (PregnancyUtils)
            String babySize = PregnancyUtils.getBabySize(i);
            String developmentInfo = PregnancyUtils.getWeekInfo(i);
            java.util.ArrayList<String> weeklyTips = PregnancyUtils.getWeeklyTips(i);
            
            // 2. تنظيم النصائح التعليمية
            String tip1 = (weeklyTips != null && weeklyTips.size() > 0) ? weeklyTips.get(0) : "استمري في المتابعة";
            String tip2 = (weeklyTips != null && weeklyTips.size() > 1) ? weeklyTips.get(1) : "اهتمي بغذائك";
            String tip3 = (weeklyTips != null && weeklyTips.size() > 2) ? weeklyTips.get(2) : "استريحي قدر الإمكان";
            
            // 3. استخدام ContentValues لإدراج البيانات بشكل آمن وتفصيلي في جدول 'pregnancy_weeks'
            ContentValues values = new ContentValues();
            values.put("week_number", i);
            values.put("size_label", babySize);
            values.put("development", developmentInfo);
            values.put("tip_one", tip1);
            values.put("tip_two", tip2);
            values.put("tip_three", tip3);
            values.put("source_url", "https://www.babycenter.com");

            // تنفيذ عملية الإدخال التقليدية
            db.insert(TABLE_WEEKS, null, values);
        }
    }

    public PregnancyWeek getPregnancyWeek(int weekNumber) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEEKS, null, "week_number = ?", new String[]{String.valueOf(weekNumber)}, null, null, null);
        PregnancyWeek week = null;

        if (cursor.moveToFirst()) {
            int num = cursor.getInt(cursor.getColumnIndexOrThrow("week_number"));
            String size = cursor.getString(cursor.getColumnIndexOrThrow("size_label"));
            String dev = cursor.getString(cursor.getColumnIndexOrThrow("development"));
            String t1 = cursor.getString(cursor.getColumnIndexOrThrow("tip_one"));
            String t2 = cursor.getString(cursor.getColumnIndexOrThrow("tip_two"));
            String t3 = cursor.getString(cursor.getColumnIndexOrThrow("tip_three"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("source_url"));
            week = new PregnancyWeek(num, size, dev, t1, t2, t3, url);
        }
        cursor.close();
        return week;
    }
}
