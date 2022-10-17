package com.michaelflisar.dialogs.interfaces

import android.widget.ImageView
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.classes.Icon

interface IMaterialDialogImageLoader {
    fun display(imageView: ImageView, icon: Icon): Boolean
}