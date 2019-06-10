package com.example.shalomhalbert.rocketinsightsapp

import com.example.shalomhalbert.rocketinsightsapp.model.Date
import com.example.shalomhalbert.rocketinsightsapp.model.Service
import com.example.shalomhalbert.rocketinsightsapp.model.fake.*
import com.example.shalomhalbert.rocketinsightsapp.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertFailsWith

class ViewModelTest : KoinTest {

    companion object {
        private val DATES_LIST = listOf(Date("2017-02-12"), Date("2017-02-11"),
                Date("2017-02-10"), Date("2017-02-09"), Date("2017-02-08"))
    }

    private val viewModel: MainViewModel by inject()
    private val service: Service = mockk()

    @Before
    fun before() {
        //Replaces Service with FakeService, and changes ViewModel CoroutineContext to Unconfined
        val testModule = module {
            single(override = true) { service }
            single(override = true) { Dispatchers.Unconfined }
        }

        startKoin { modules(listOf(modelModules, viewModelModules, testModule)) }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Dates returns filled list`() = runBlocking {
        coEvery { service.dates() } returns async { DATES_LIST }

        //Join removes the need for using CourtineScope
        viewModel.onUpdateList().join()

        val dates = viewModel.dates.get()
        val expected = FilledList.defaultResponse
        assertEquals(expected, dates)
    }

    @Test
    fun `Date returns empty list`() = runBlocking {
        coEvery { service.dates() } returns async { emptyList<Date>() }

        viewModel.onUpdateList().join()

        val dates = viewModel.dates.get()
        val expected = EmptyList.defaultResponse
        assertEquals(expected, dates)

    }

    @Test
    fun `Date throws HttpException and returns an empty list`() = runBlocking {
        coEvery { service.dates() } throws HttpException(Response.success(""))

        viewModel.onUpdateList().join()

        val dates = viewModel.dates.get()
        val expected = HttpError.defaultResponse
        assertEquals(expected, dates)
    }

    @Test(expected = Throwable::class)
    fun `Date throws Throwable`() = runBlocking {
        coEvery { service.dates() } throws Throwable()

        viewModel.onUpdateList().join()

        assertFailsWith<Throwable> { viewModel.dates.get() }
        Unit
    }
}