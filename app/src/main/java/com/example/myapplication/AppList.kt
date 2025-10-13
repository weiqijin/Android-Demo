package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class AppList : ComponentActivity() {
    private var installedApps: List<ApplicationInfo> = emptyList()
    private lateinit var adapter: AppListAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private val handler = Handler(Looper.getMainLooper())
    private val refreshIntervalMillis = 3000L // 每3秒刷新一次


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)
        refreshLayout = findViewById(R.id.swipe_refresh_layout)

        val listView = findViewById<ListView>(R.id.app_list)
        adapter = AppListAdapter(this, installedApps)
        listView.adapter = adapter

        // 设置点击事件
        listView.setOnItemClickListener { _, _, position, _ ->
            val appInfo = installedApps[position]
            val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
            startActivity(intent)
        }

        // 获取已安装的应用列表
        getInstalledApps()

        // 设置下拉刷新监听
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            getInstalledApps()
//            adapter = AppListAdapter(this, installedApps)
//            listView.adapter = adapter
        }

        // 注册广播接收器以监听应用安装和卸载事件
        registerReceiver(packageChangeReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        })

    }

    override fun onStart() {
        super.onStart()
        getInstalledApps()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(packageChangeReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        })
        getInstalledApps()
//        val listView = findViewById<ListView>(R.id.app_list)
//        adapter = AppListAdapter(this, installedApps)
//        listView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(packageChangeReceiver)
        refreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理资源
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(packageChangeReceiver)
    }

    // 获取已安装的应用列表
    private fun getInstalledApps(){
        try {
            installedApps = emptyList()
            installedApps = packageManager.getInstalledApplications(PackageManager.MATCH_ALL)
                .filter {
                    // 过滤系统应用，只显示用户安装的应用
                    it.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }

            runOnUiThread {
                adapter.updateData(installedApps)
                refreshLayout.isRefreshing = false
            }
            val listView = findViewById<ListView>(R.id.app_list)
            adapter = AppListAdapter(this, installedApps)
            listView.adapter = adapter
//            packageManager.clearPackagePreferredActivities(packageName)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error getting installed applications", e)
            refreshLayout.isRefreshing = false
        }

    }

    // 设置广播接收器来更新应用列表
    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.data?.schemeSpecificPart?.let { packageName ->
                getInstalledApps()
            }
        }
    }

    // 内部类用于自定义适配器
    private inner class AppListAdapter(
        context: Context,
        private var apps: List<ApplicationInfo>
    ) : ArrayAdapter<ApplicationInfo>(
    context,
    R.layout.item_app,
    R.id.app_name,
    apps
    ) {
        fun updateData(newApps: List<ApplicationInfo>) {
            apps = emptyList()
            apps = newApps
            packageManager.clearPackagePreferredActivities(packageName)
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.item_app, parent, false)

            val appInfo = getItem(position)!!
            view.findViewById<TextView>(R.id.app_name).text = appInfo.loadLabel(packageManager) // 获取应用名称
            view.findViewById<ImageView>(R.id.app_icon).setImageDrawable(appInfo.loadIcon(packageManager)) // 获取应用图标
            return view
        }
    }

}
