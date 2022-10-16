package com.michaelflisar.dialogs.classes

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

sealed class MaterialDialogParent {

    abstract val context: android.content.Context

    class Activity(activity: FragmentActivity) : MaterialDialogParent(), FragmentManagerProvider {
        override val context: android.content.Context = activity
        override val fragmentManager = activity.supportFragmentManager
    }

    class Fragment(fragment: androidx.fragment.app.Fragment) : MaterialDialogParent(),
        FragmentManagerProvider {
        override val context: android.content.Context = fragment.requireContext()
        override val fragmentManager = fragment.childFragmentManager
    }

    class Context(override val context: android.content.Context) : MaterialDialogParent()

    interface FragmentManagerProvider {
        val fragmentManager: FragmentManager
    }

    companion object {
        fun create(context: android.content.Context) : MaterialDialogParent {
            return if (context is FragmentActivity) {
                Activity(context)
            } else Context(context)
        }
        fun create(fragment: androidx.fragment.app.Fragment) : MaterialDialogParent{
            return if (fragment.parentFragment != null)
                return Fragment(fragment.requireParentFragment())
            else if (fragment.activity != null)
                Activity(fragment.requireActivity())
            else throw RuntimeException("Fragment not attached to parent fragment nor parent activity!")
        }
    }
}