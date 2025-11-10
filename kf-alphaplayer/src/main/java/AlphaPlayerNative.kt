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
import com.tencent.qgame.animplayer.Constant
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType

object AlphaPlayerNative {
	private var currentAnimView: AnimView? = null
	fun getMemInfoKotlin():Array<Number>{
	  val activityManager = UTSAndroid.getUniActivity()?.getSystemService(ACTIVITY_SERVICE) as ActivityManager
	  val memoryInfo = ActivityManager.MemoryInfo()
	  activityManager.getMemoryInfo(memoryInfo)
	  val availMem = memoryInfo.availMem / 1024 / 1024
	  val totalMem = memoryInfo.totalMem / 1024 / 1024
	  console.log("getMemInfoKotlin", availMem, totalMem)
	  return arrayOf(availMem,totalMem)
	}
	
	fun animationPlay(type: String, place: String) {
		try {
			Thread {
				var scaleType = when(place) {
					"FIT_XY" -> ScaleType.FIT_XY
					"FIT_CENTER" -> ScaleType.FIT_CENTER
					"CENTER_CROP" -> ScaleType.CENTER_CROP
					else -> ScaleType.FIT_XY
				}
				console.log("AlphaPlayerNative AnimationPlay", type, scaleType)
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
						// 先停止并移除之前可能存在的动画视图
						if (currentAnimView != null && currentAnimView!!.parent == rootView) {
							rootView.removeView(currentAnimView)
							currentAnimView!!.stopPlay()
							currentAnimView = null
						}

						val animView = AnimView(activity)
						animView.setVideoMode(Constant.VIDEO_MODE_SPLIT_HORIZONTAL_REVERSE)
						animView.enableVersion1(true)
						animView.setScaleType(scaleType)
						rootView.addView(animView)
						// 保存引用供后续停止使用
						currentAnimView = animView
						val animListener = object : IAnimListener {
							override fun onVideoConfigReady(config: AnimConfig): Boolean {
								console.log("onVideoConfigReady", config)
								return true
							}
							override fun onVideoStart() {
								console.log("onVideoStart")
							}
							override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
								console.log("onVideoRender", frameIndex, config)
							}
							override fun onVideoComplete() {
								console.log("onVideoComplete")
							}
							override fun onVideoDestroy() {
								console.log("onVideoDestroy")
							}
							override fun onFailed(errorType: Int, errorMsg: String?) {
								console.log("onFailed", errorType, errorMsg)
								activity.runOnUiThread {
									rootView.removeView(animView)
								}
							}
						}
						animView.setAnimListener(animListener)
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

	fun animationStop() {
		console.log("AlphaPlayerNative AnimationStop")
		val activity = UTSAndroid.getUniActivity()
		if (activity != null) {
			activity.runOnUiThread {
				val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
				console.log("获取根节点：", rootView)
				
				// 使用保存的动画视图引用
				if (currentAnimView != null) {
					console.log("停止动画视图")
					currentAnimView!!.stopPlay()
					
					// 从父容器中移除视图
					if (currentAnimView!!.parent == rootView) {
						rootView.removeView(currentAnimView)
					}
					
					// 清除引用
					currentAnimView = null
				} else {
					console.log("没有找到正在播放的动画视图")
				}
			}
		}
	}
}