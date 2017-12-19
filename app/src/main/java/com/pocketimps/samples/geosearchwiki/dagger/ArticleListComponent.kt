package com.pocketimps.samples.geosearchwiki.dagger

import com.pocketimps.samples.geosearchwiki.ui.ArticleListActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [
    ArticleListModule::class
])
interface ArticleListComponent {
    fun injectTo(activity: ArticleListActivity)
}
