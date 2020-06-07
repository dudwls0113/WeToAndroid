package com.ninano.weto.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ToDo.class, ToDoData.class, FavoriteLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    //데이터베이스를 매번 생성하는건 리소스를 많이사용하므로 싱글톤이 권장된다고한다.

    private static AppDatabase INSTANCE;

    public abstract ToDoDao todoDao();

    //싱글톤 객체 가져오기
    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "weto-db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();

        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, " + "`name` TEXT, PRIMARY KEY(`id`))");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Book " + " ADD COLUMN pub_year INTEGER");
        }
    };


    //디비객체제거
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
