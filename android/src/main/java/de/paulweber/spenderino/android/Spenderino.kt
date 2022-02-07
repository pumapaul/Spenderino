package de.paulweber.spenderino.android

import android.app.Application
import com.stripe.android.PaymentConfiguration
import de.paulweber.spenderino.initKoin
import de.paulweber.spenderino.utility.L10n
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class Spenderino : Application() {
    override fun onCreate() {
        super.onCreate()

        L10n.context = this

        initKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@Spenderino)
        }
        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.StripeKey
        )
    }
}
