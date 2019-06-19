package me.ibrahimsn.viewmodel.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import me.ibrahimsn.viewmodel.data.model.Repo;
import me.ibrahimsn.viewmodel.data.rest.RepoRepository;
import me.ibrahimsn.viewmodel.data.rest.RepoService;
import me.ibrahimsn.viewmodel.util.BaseSchedulerProvider;

public class ListViewModel extends ViewModel {

    private BaseSchedulerProvider schedulerProvider;
    private final RepoRepository repoRepository;
    private CompositeDisposable disposable;

    private final MutableLiveData<List<Repo>> repos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    @Inject
    public ListViewModel(RepoRepository repoRepository, BaseSchedulerProvider schedulerProvider) {
        this.repoRepository = repoRepository;
        this.schedulerProvider = schedulerProvider;
        disposable = new CompositeDisposable();
        fetchRepos();
    }

    LiveData<List<Repo>> getRepos() {
        return repos;
    }
    LiveData<Boolean> getError() {
        return repoLoadError;
    }
    LiveData<Boolean> getLoading() {
        return loading;
    }

    public void fetchRepos() {
        loading.setValue(true);
        disposable.add(repoRepository.getRepositories()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableSingleObserver<List<Repo>>() {
                    @Override
                    public void onSuccess(List<Repo> value) {
                        repoLoadError.setValue(false);
                        repos.setValue(value);
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        repoLoadError.setValue(true);
                        loading.setValue(false);
                    }
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }
}
