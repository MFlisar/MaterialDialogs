package com.michaelflisar.dialogs

import android.app.Application
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.interfaces.IMaterialDialogImageLoader
import com.michaelflisar.lumberjack.L
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import timber.log.ConsoleTree

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // init lumberjack...
        L.plant(ConsoleTree())

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
                val tint = MaterialDialogUtil.getThemeColorAttr(
                    imageView.context,
                    R.attr.colorOnBackground
                )
                Glide
                    .with(this@MainApp)
                    .load(data)
                    .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(tint)))
                    .into(imageView)
                return true
            }
        }
    }
}