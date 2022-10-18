package com.michaelflisar.dialogs

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.classes.DebugDialogData
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
    }
}