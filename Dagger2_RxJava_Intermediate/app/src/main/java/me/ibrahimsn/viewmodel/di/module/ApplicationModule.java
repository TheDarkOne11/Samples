package me.ibrahimsn.viewmodel.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ibrahimsn.viewmodel.data.rest.RepoService;
import me.ibrahimsn.viewmodel.util.BaseSchedulerProvider;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
@Module(includes = ViewModelModule.class)
public class ApplicationModule {

    private static final String BASE_URL = "https://api.github.com/";

    @Singleton
    @Provides
    static Retrofit provideRetrofit() {
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    static RepoService provideRetrofitService(Retrofit retrofit) {
        return retrofit.create(RepoService.class);
    }

    @Singleton
    @Provides
    static SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Singleton
    @Provides
    static BaseSchedulerProvider provideBaseSchedulerProvider() {
        return new BaseSchedulerProvider() {

            @NotNull
            @Override
            public Scheduler ui() {
                return AndroidSchedulers.mainThread();
            }

            @NotNull
            @Override
            public Scheduler computation() {
                return Schedulers.computation();
            }

            @NotNull
            @Override
            public Scheduler io() {
                return Schedulers.io();
            }
        };
    }
}
