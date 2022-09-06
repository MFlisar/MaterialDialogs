package com.michaelflisar.dialogs.apps

import android.content.Context
import android.content.Intent
import com.michaelflisar.dialogs.DialogList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

@Parcelize
object AppsManager : DialogList.Loader {

    override suspend fun load(context: Context): List<AppListItem> {
        return withContext(Dispatchers.IO) {
            val apps = ArrayList<App>()
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (info in resolveInfos) {
                // preloading label here inside the IO context
                val label = info.loadLabel(pm)?.toString() ?: ""
                apps.add(App(label, info))
            }
            apps.sortWith { o1, o2 -> o1.name.compareTo(o2.name, true) }
            apps.mapIndexed { index, app ->
                AppListItem(
                    index,
                    app
                )
            }
        }
    }
}