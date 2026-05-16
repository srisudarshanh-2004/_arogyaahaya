package com.arogyasahaya.di

import android.content.Context
import com.arogyasahaya.data.dao.*
import com.arogyasahaya.data.database.ArogyaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ArogyaDatabase =
        ArogyaDatabase.getDatabase(context)

    @Provides
    fun provideMedicineDao(db: ArogyaDatabase): MedicineDao = db.medicineDao()

    @Provides
    fun provideMedicineDoseLogDao(db: ArogyaDatabase): MedicineDoseLogDao = db.medicineDoseLogDao()

    @Provides
    fun provideVitalLogDao(db: ArogyaDatabase): VitalLogDao = db.vitalLogDao()

    @Provides
    fun provideUserProfileDao(db: ArogyaDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideAshaEventDao(db: ArogyaDatabase): AshaEventDao = db.ashaEventDao()
}
