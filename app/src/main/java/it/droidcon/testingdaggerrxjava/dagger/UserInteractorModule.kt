package it.droidcon.testingdaggerrxjava.dagger

import dagger.Module
import dagger.Provides
import it.droidcon.testingdaggerrxjava.core.UserInteractor
import it.droidcon.testingdaggerrxjava.core.gson.StackOverflowService
import javax.inject.Singleton

@Module object UserInteractorModule {

    @JvmStatic @Provides @Singleton fun provideUserInteractor(stackOverflowService: StackOverflowService) =
            UserInteractor(stackOverflowService)
}
