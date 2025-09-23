
package com.example.applistdemo

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.app_list)
        val appList = getInstalledApps()
        
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            appList.map { it.loadLabel(packageManager).toString() }
        )
    }

    private fun getInstalledApps(): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { 
                // 过滤系统应用，只显示用户安装的应用
                it.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
    }
}
