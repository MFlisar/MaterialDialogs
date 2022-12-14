package com.michaelflisar.dialogs

import android.app.Application
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.classes.DebugDialogData
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.interfaces.IMaterialDialogImageLoader
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.lumberjack.L
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import timber.log.ConsoleTree

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // init lumberjack...
        L.plant(ConsoleTree())

        // init one debug data manager for some automatically handled debug settings
        DebugDialogData.initDebugManager(this)

        // OPTIONAL! resources will work without this as well!
        // this loader will use Glide for loading any image data
        MaterialDialog.imageLoader = object : IMaterialDialogImageLoader {

            private fun getIconData(icon: Icon): Any? {
                return when (icon) {
                    is Icon.Data -> icon.data
                    is Icon.String -> icon.data
                    Icon.None -> null
                    is Icon.Resource -> icon.res
                }
            }

            override fun display(imageView: ImageView, icon: Icon): Boolean {
                val data = getIconData(icon) ?: return false
                var loader = Glide
                    .with(this@MainApp)
                    .load(data)
                if (icon.tintInTextColor) {
                    val tint = MaterialDialogUtil.getThemeColorAttr(
                        imageView.context,
                        R.attr.colorOnBackground
                    )
                    loader = loader.apply(RequestOptions.bitmapTransform(ColorFilterTransformation(tint)))
                }
                loader.into(imageView)
                return true
            }
        }

        // OPTIONAL Global listener... make sure to unregister whenever appropriate if you use this outside of the application class!
        MaterialDialog.addGlobalCallback { presenter, event ->
            L.tag("GLOBAL-LISTENER").d { "presenter: $presenter | parent = ${presenter.parent} | lifecycleOwner = ${presenter.lifecycleOwner} | event: $event" }
        }

        // OPTIONAL: Logger
        MaterialDialog.logger = { level, info, exception ->
            if (exception != null) {
                L.callStackCorrection(2).tag("DIALOG-LOG").log(level, exception) { info }
            } else {
                L.callStackCorrection(2).tag("DIALOG-LOG").log(level) { info }
            }
        }

        // [OPTIONAL] KotBilling logger
        KotBilling.logger = { level, info, exception ->
            if (exception != null) {
                L.callStackCorrection(2).tag("KOTBILLING-LOG").log(level, exception) { info }
            } else {
                L.callStackCorrection(2).tag("KOTBILLING-LOG").log(level) { info }
            }
        }
    }
}