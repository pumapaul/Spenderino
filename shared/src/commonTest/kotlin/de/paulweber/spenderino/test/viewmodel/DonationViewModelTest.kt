@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.donation.DonationInformation
import de.paulweber.spenderino.model.repositories.donation.DonationRepository
import de.paulweber.spenderino.model.repositories.donation.Recipient
import de.paulweber.spenderino.model.repositories.donation.StripeResult
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.DonationAction
import de.paulweber.spenderino.viewmodel.DonationRoute
import de.paulweber.spenderino.viewmodel.DonationState
import de.paulweber.spenderino.viewmodel.DonationViewModel
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.collect
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class DonationViewModelTest : BaseTest() {
    private val donationInfo = DonationInformation(
        recipient = Recipient("recipient"),
        paymentSecret = "paymentSecret",
        paymentIntentId = "paymentId",
        customerId = "customerId",
        customerSecret = "customerSecret"
    )

    @Test
    fun `code gets extracted from url properly`() = runBlocking {
        val expected = "some code"
        val url = "$BASE_URL/r/$expected"
        var result = ""
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } answers {
                result = this.args[0] as String
                Result.success(mockk(relaxed = true))
            }
        }

        DonationViewModel(url)
        delay(100)

        assertEquals(expected, result)
    }

    @Test
    fun `initial state is loading and will be error if request fails`() = runBlocking {
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } coAnswers {
                delay(200)
                Result.failure(Exception())
            }
        }

        val results = mutableListOf<DonationState>()
        val viewModel = DonationViewModel("")
        scope.launch { viewModel.state.collect { results.add(it) } }

        delay(300)

        val expected = listOf(DonationState.Loading, DonationState.Error.NetworkError)
        assertEquals(expected, results)
    }

    @Test
    fun `404 on fetchPaymentData results in UnknownCode state`() = runBlocking {
        declareMock<DonationRepository> {
            val exception = createMockedClientException(HttpStatusCode.NotFound)
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.failure(exception)
        }

        val viewModel = DonationViewModel("")
        delay(200)

        val expected = DonationState.Error.UnknownCode
        assertEquals(expected, viewModel.state.value)
    }

    @Test
    fun `DonationInfo results in correct Base state`() = runBlocking {
        val fee = 100L * 3 / 100 + 26
        val expected = DonationState.Base(
            isTransactionInProgress = false,
            isUpdatingSum = false,
            donationValue = 100L,
            transactionFee = fee,
            totalValue = 100L + fee,
            donationInfo = donationInfo
        )
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
        }

        val viewModel = DonationViewModel("")
        delay(200)

        assertEquals(expected, viewModel.state.value)
    }

    @Test
    fun `ChangeDonationValue action changes all relevant currency values`() = runBlocking {
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
        }

        val viewModel = DonationViewModel("")
        delay(200)

        val newValue = 200L
        val fee = newValue * 3 / 100 + 26

        viewModel.perform(DonationAction.ChangeDonationValue(200L))
        delay(100)

        val result = viewModel.state.value as DonationState.Base
        assertEquals(newValue, result.donationValue)
        assertEquals(fee, result.transactionFee)
        assertEquals(newValue + fee, result.totalValue)
    }

    @Test
    fun `ChangeDonationValue action changes payment id amount with intermittent loading states`() =
        runBlocking {
            val mockedRepo = declareMock<DonationRepository> {
                coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
                coEvery { updateDonationSum(any(), any(), any()) } returns Result.success(Unit)
            }

            val results = mutableListOf<DonationState>()
            val viewModel = DonationViewModel("")
            delay(200)
            scope.launch { viewModel.state.collect { results.add(it) } }

            viewModel.perform(DonationAction.ChangeDonationValue(200L))
            delay(200)

            assertEquals(4, results.size)
            val baseResults = results.mapNotNull { it as? DonationState.Base }

            assertEquals(true, baseResults[2].isUpdatingSum)
            assertEquals(false, baseResults[3].isUpdatingSum)

            coVerify(exactly = 1) { mockedRepo.updateDonationSum(any(), any(), any()) }
            confirmVerified()
        }

    @Test
    fun `Reload action fetches payment information again`() {
        val mockedRepo = declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
        }

        val viewModel = DonationViewModel("")

        viewModel.perform(DonationAction.Reload)

        coVerify(exactly = 2) { mockedRepo.getPaymentIntentClientSecret(any()) }
        confirmVerified()
    }

    @Test
    fun `ChangeTransactionInProgress changes isTransactionInProgress flag`() = runBlocking {
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
        }

        val viewModel = DonationViewModel("")
        delay(200)

        viewModel.perform(DonationAction.ChangeTransactionInProgress(true))

        val result = viewModel.state.value as DonationState.Base
        assertEquals(true, result.isTransactionInProgress)
    }

    @Test
    fun `TransactionResult action sets success state on success`() = runBlocking {
        declareMock<DonationRepository> {
            coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
        }

        val viewModel = DonationViewModel("")
        delay(200)

        viewModel.perform(DonationAction.TransactionResult(StripeResult.Completed))

        val expectedMessage = L10n.format(
            "donation_success",
            100L.toEuroString(),
            donationInfo.recipient.name
        )
        val expected = DonationState.Success(expectedMessage)

        assertEquals(viewModel.state.value, expected)
    }

    @Test
    fun `TransactionResult action sets isTransactionInProgress flag to false on cancel`() =
        runBlocking {
            declareMock<DonationRepository> {
                coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
            }

            val viewModel = DonationViewModel("")
            delay(200)

            viewModel.perform(DonationAction.TransactionResult(StripeResult.Canceled))

            val result = viewModel.state.value as DonationState.Base
            assertEquals(false, result.isTransactionInProgress)
        }

    @Test
    fun `TransactionResult action sets isTransactionInProgress flag to false on fail`() =
        runBlocking {
            declareMock<DonationRepository> {
                coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
            }

            val viewModel = DonationViewModel("")
            delay(200)

            viewModel.perform(DonationAction.TransactionResult(StripeResult.Failed("")))

            val result = viewModel.state.value as DonationState.Base
            assertEquals(false, result.isTransactionInProgress)
        }

    @Test
    fun `TransactionResult action sets payment failed alert route to false on fail`() =
        runBlocking {
            declareMock<DonationRepository> {
                coEvery { getPaymentIntentClientSecret(any()) } returns Result.success(donationInfo)
            }

            val viewModel = DonationViewModel("")
            delay(200)

            val expectedErrorMessage = "some message"
            viewModel.perform(
                DonationAction.TransactionResult(
                    StripeResult.Failed(
                        expectedErrorMessage
                    )
                )
            )

            assertEquals(true, viewModel.route.value is DonationRoute.Alert)
            val alert = (viewModel.route.value as DonationRoute.Alert).alert

            assertEquals("donation_alert_payment_failed_title", alert.title)
            assertEquals(expectedErrorMessage, alert.message)
        }
}
