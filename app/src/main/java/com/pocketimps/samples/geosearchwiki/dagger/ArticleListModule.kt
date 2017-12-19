package com.pocketimps.samples.geosearchwiki.dagger

import com.pocketimps.samples.geosearchwiki.ui.controller.*
import dagger.Module
import dagger.Provides

@Module
class ArticleListModule(private val mObtainLocationListener: ObtainLocationListener,
                        private val mDataLoadListener: DataLoadListener) {
    @Provides
    @ActivityScope
    fun provideDataController(impl: DataLoadControllerImpl): DataLoadController = impl

    @Provides
    @ActivityScope
    fun provideObtainLocationListener() = mObtainLocationListener

    @Provides
    @ActivityScope
    fun provideLocationController(impl: ObtainLocationControllerImpl): ObtainLocationController = impl

    @Provides
    @ActivityScope
    fun provideDataLoadListener() = mDataLoadListener
}
