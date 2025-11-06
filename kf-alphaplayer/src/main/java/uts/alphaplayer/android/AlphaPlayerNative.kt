package uts.alphaplayer.android
// 引用系统类  
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.view.ViewGroup
import android.view.View
// 引用 uts 基础库相关类
import io.dcloud.uts.UTSAndroid
import io.dcloud.uts.console
// 引入vap相关类
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType

object AlphaPlayerNative {
	fun getMemInfoKotlin():Array<Number>{
	  val activityManager = UTSAndroid.getUniActivity()?.getSystemService(ACTIVITY_SERVICE) as ActivityManager
	  val memoryInfo = ActivityManager.MemoryInfo()
	  activityManager.getMemoryInfo(memoryInfo)
	  val availMem = memoryInfo.availMem / 1024 / 1024
	  val totalMem = memoryInfo.totalMem / 1024 / 1024
	  console.log("getMemInfoKotlin", availMem, totalMem)
	  return arrayOf(availMem,totalMem)
	}
	
	fun animationPlay(type: String) {
		console.log("AlphaPlayerNative AnimationPlay", type)
		try {
			Thread {
				var filePath = UTSAndroid.getResourcePath("static/animation/" + type + ".mp4")
				val file = File(filePath)
				console.log("获取到动效文件：", file)
				val md5 = FileUtil.getFileMD5(file)
				console.log("检查md5：", md5)
				val activity = UTSAndroid.getUniActivity()
				console.log("activity：", activity)
				if (activity != null) {
					activity.runOnUiThread {
						val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
						console.log("获取根节点：", rootView)
						val animView = AnimView(activity)
						animView.setScaleType(ScaleType.FIT_CENTER)
						rootView.addView(animView)
						animView.startPlay(file)
					}
				} else {
					console.log("无法获取Activity实例")
				}
			}.start()
		} catch (e: Exception) {
			console.log("播放出现异常: ${e.message}")
		}
	}
}