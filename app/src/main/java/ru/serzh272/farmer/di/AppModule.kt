package ru.serzh272.farmer.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.serzh272.farmer.App
import ru.serzh272.farmer.data.local.AppDb
import ru.serzh272.farmer.data.local.dao.FieldDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDb {
        return Room.databaseBuilder(
            context,
            AppDb::class.java,
            AppDb.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideFieldsDataSourse(database: AppDb): FieldDao{
        return database.fieldDao()
    }
}

