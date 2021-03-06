package com.example.deliveryapp.di.modules

import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
class DeliveryModule {

    @Provides
    internal fun providesDeliveryDao(localDatabase: LocalDatabase): DeliveryDao {
        return localDatabase.deliveryDao()
    }

}