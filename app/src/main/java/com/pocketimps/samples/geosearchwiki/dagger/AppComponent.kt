package com.pocketimps.samples.geosearchwiki.dagger

import com.pocketimps.samples.geosearchwiki.app.App
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class
])

interface AppComponent {
    fun inject(app: App)

    fun plus(childModule: ArticleListModule): ArticleListComponent
}
