package me.weishu.kernelsu.ui.module

import me.weishu.kernelsu.BuildConfig

sealed class EmbeddedModules(
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val versionCode: Int,
    val description: String,
    val switchable: Boolean
) {
    object KernelSU : EmbeddedModules(
        id = "kernel_su",
        name = "KernelSU",
        author = "weishu",
        version = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        description = "KernelSU Manager",
        switchable = false
    )

    object EcosedKit : EmbeddedModules(
        id = "ecosed_kit",
        name = "EcosedKit",
        author = "wyq0918dev",
        version = "1.0",
        versionCode = 1,
        description = "Ecosed Developer Kit",
        switchable = false
    )

    object KeyboardLight : EmbeddedModules(
        id = "keyboard_light",
        name = "键盘灯",
        author = "wyq0918dev",
        version = "1.0",
        versionCode = 1,
        description = "亮屏开启熄屏关闭",
        switchable = true
    )

    object EyeProtectionMode : EmbeddedModules(
        id = "eye_protection_mode",
        name = "护眼模式",
        author = "wyq0918dev",
        version = "1.0",
        versionCode = 1,
        description = "解锁开启锁屏关闭",
        switchable = true
    )
}