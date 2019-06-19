package me.ibrahimsn.viewmodel.ui.list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.ibrahimsn.viewmodel.data.model.Repo
import me.ibrahimsn.viewmodel.data.model.User
import me.ibrahimsn.viewmodel.data.rest.RepoRepository
import me.ibrahimsn.viewmodel.util.BaseSchedulerProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.reset
import org.mockito.stubbing.OngoingStubbing


internal class MainActivityViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    val testObserver = mock<Observer<List<Repo>>>()

    @InjectMocks
    val repository = mock<RepoRepository>()

    val schedulersProvider = object : BaseSchedulerProvider {
        override fun io() = Schedulers.trampoline()

        override fun computation() = Schedulers.trampoline()

        override fun ui() = Schedulers.trampoline()
    }

    val viewmodel by lazy { ListViewModel(repository, schedulersProvider) }

    @Before
    fun init() {
        reset(testObserver)
    }

    @Test
    fun showDataFromApi() {
        val result = listOf(
                Repo(1, "Petr", "Nothing", User("PetrB"), 20, 10),
                Repo(1, "Michal", "Nothing", User("PetrB"), 20, 10),
                Repo(1, "Ondra", "Nothing", User("PetrB"), 20, 10)
        )
        Mockito
                .`when`(repository.repositories)    // Když je zaslán tento požadavek
                .thenReturn(Single.just(result))    // vrať tento výsledek

        viewmodel.repos.observeForever(testObserver)
        viewmodel.fetchRepos()

//        val captor = argumentCaptor<List<Repo>>()

        assert(viewmodel.repos.value != null)
        assert(viewmodel.repos.value!!.count() == 3)
        assert(!viewmodel.error.value!!)
        assert(!viewmodel.loading.value!!)
    }
}

inline fun <reified T> mock() = Mockito.mock(T::class.java)
inline fun <T> whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)