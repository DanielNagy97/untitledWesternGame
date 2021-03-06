package hu.unimiskolc.iit.mobile.untitledwestern.application.di

import hu.unimiskolc.iit.mobile.untitledwestern.application.fragment.EndGameViewModel
import hu.unimiskolc.iit.mobile.untitledwestern.application.fragment.MainGameViewModel
import hu.unimiskolc.iit.mobile.untitledwestern.application.fragment.StartGameViewModel
import hu.unimiskolc.iit.mobile.untitledwestern.framework.db.WesternDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { WesternDatabase.getInstance(androidContext()) }

    viewModel { MainGameViewModel(get()) }
    viewModel { EndGameViewModel(get()) }
    viewModel { StartGameViewModel() }
}