package me.weishu.kernelsu.ui.viewmodel

import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.weishu.kernelsu.Settings
import me.weishu.kernelsu.ksuApp
import me.weishu.kernelsu.ui.module.EmbeddedModules
import me.weishu.kernelsu.ui.util.listModules
import me.weishu.kernelsu.ui.util.overlayFsAvailable
import org.json.JSONArray
import org.json.JSONObject
import java.text.Collator
import java.util.*

class ModuleViewModel : ViewModel() {


    class ModuleInfo(
        val id: String,
        val name: String,
        val author: String,
        val version: String,
        val versionCode: Int,
        val description: String,
        val switchable: Boolean,
        val enabled: Boolean,
        val update: Boolean,
        val remove: Boolean,
        val updateJson: String,
    )

    data class ModuleUpdateInfo(
        val version: String,
        val versionCode: Int,
        val zipUrl: String,
        val changelog: String,
    )

    var isRefreshing by mutableStateOf(false)
        private set

    var isOverlayAvailable by mutableStateOf(overlayFsAvailable())
        private set

    val embeddedModuleList by derivedStateOf {
        return@derivedStateOf embeddedModules.also {
            isRefreshing = false
        }
    }

    val moduleList by derivedStateOf {
        val comparator = compareBy(Collator.getInstance(Locale.getDefault()), ModuleInfo::id)
        return@derivedStateOf modules.sortedWith(comparator).also {
            isRefreshing = false
        }
    }

    var isNeedRefresh by mutableStateOf(false)
        private set

    fun markNeedRefresh() {
        isNeedRefresh = true
    }

    fun fetchModuleList() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true

            val oldModuleList = modules

            val start = SystemClock.elapsedRealtime()

            kotlin.runCatching {
                isOverlayAvailable = overlayFsAvailable()

                val result = listModules()

                Log.i(TAG, "result: $result")

                val array = JSONArray(result)

                val embeddedArray = arrayListOf(
                    EmbeddedModules.KernelSU,
                    EmbeddedModules.EcosedKit,
                    EmbeddedModules.KeyboardLight,
                    EmbeddedModules.EyeProtectionMode
                )

                val alo = arrayListOf<ModuleInfo>()
                embeddedArray.forEach {
                    alo.add(
                        ModuleInfo(
                            id = it.id,
                            name = it.name,
                            author = it.author,
                            version = it.version,
                            versionCode = it.versionCode,
                            description = it.description,
                            switchable = it.switchable,
                            enabled = readSP(id = it.id),
                            update = false,
                            remove = false,
                            updateJson = ""
                        )
                    )
                }

                embeddedModules = alo

                modules = (0 until array.length())
                    .asSequence()
                    .map {
                        array.getJSONObject(it)
                    }
                    .map { obj ->
                        ModuleInfo(
                            id = obj.getString("id"),
                            name = obj.optString("name"),
                            author = obj.optString("author", "Unknown"),
                            version = obj.optString("version", "Unknown"),
                            versionCode = obj.optInt("versionCode", 0),
                            description = obj.optString("description"),
                            switchable = true,
                            enabled = obj.getBoolean("enabled"),
                            update = obj.getBoolean("update"),
                            remove = obj.getBoolean("remove"),
                            updateJson = obj.optString("updateJson")
                        )
                    }
                    .toList()
                isNeedRefresh = false
            }.onFailure { e ->
                Log.e(TAG, "fetchModuleList: ", e)
                isRefreshing = false
            }

            // when both old and new is kotlin.collections.EmptyList
            // moduleList update will don't trigger
            if (oldModuleList === modules) {
                isRefreshing = false
            }

            Log.i(TAG, "load cost: ${SystemClock.elapsedRealtime() - start}, modules: $modules")
        }
    }

    fun writeSP(id: String, value: Boolean) {
       Settings.getPreferencesEditor().putBoolean(id, value).apply()
    }

    private fun readSP(id: String): Boolean {
        return Settings.getPreferences().getBoolean(id, true)
    }

    fun checkUpdate(m: ModuleInfo): Triple<String, String, String> {
        val empty = Triple("", "", "")
        if (m.updateJson.isEmpty() || m.remove || m.update || !m.enabled) {
            return empty
        }
        // download updateJson
        val result = kotlin.runCatching {
            val url = m.updateJson
            Log.i(TAG, "checkUpdate url: $url")
            val response = okhttp3.OkHttpClient()
                .newCall(
                    okhttp3.Request.Builder()
                        .url(url)
                        .build()
                ).execute()
            Log.d(TAG, "checkUpdate code: ${response.code}")
            if (response.isSuccessful) {
                response.body?.string() ?: ""
            } else {
                ""
            }
        }.getOrDefault("")
        Log.i(TAG, "checkUpdate result: $result")

        if (result.isEmpty()) {
            return empty
        }

        val updateJson = kotlin.runCatching {
            JSONObject(result)
        }.getOrNull() ?: return empty

        val version = updateJson.optString("version", "")
        val versionCode = updateJson.optInt("versionCode", 0)
        val zipUrl = updateJson.optString("zipUrl", "")
        val changelog = updateJson.optString("changelog", "")
        if (versionCode <= m.versionCode || zipUrl.isEmpty()) {
            return empty
        }

        return Triple(zipUrl, version, changelog)
    }

    companion object {

        private const val TAG = "ModuleViewModel"
        private var modules by mutableStateOf<List<ModuleInfo>>(emptyList())
        private var embeddedModules by mutableStateOf<List<ModuleInfo>>(value = emptyList())
    }
}
