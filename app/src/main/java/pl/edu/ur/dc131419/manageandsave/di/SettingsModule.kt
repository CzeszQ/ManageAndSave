package pl.edu.ur.dc131419.manageandsave.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.edu.ur.dc131419.manageandsave.data.preferences.SettingsRepository


@Module
@InstallIn(SingletonComponent::class)
object SettingsDiModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSettingsRepository(prefs: SharedPreferences): SettingsRepository =
        SettingsRepository(prefs)
}