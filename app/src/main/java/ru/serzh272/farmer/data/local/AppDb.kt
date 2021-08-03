package ru.serzh272.farmer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.serzh272.farmer.BuildConfig
import ru.serzh272.farmer.data.local.converters.FieldConverter
import ru.serzh272.farmer.data.local.dao.FieldDao
import ru.serzh272.farmer.data.local.entities.Field


@Database(
    entities = [Field::class],
    version = AppDb.DATABASE_VERSION,
    exportSchema = false,
    views = []
)
@TypeConverters(FieldConverter::class)
abstract class AppDb : RoomDatabase(){
    companion object{
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }
    abstract fun fieldDao(): FieldDao

}