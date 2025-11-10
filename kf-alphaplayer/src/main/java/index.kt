@file:Suppress("UNCHECKED_CAST", "USELESS_CAST", "INAPPLICABLE_JVM_NAME", "UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING", "UNNECESSARY_NOT_NULL_ASSERTION")
package uts.sdk.modules.kfAlphaplayer
import io.dcloud.uniapp.*
import io.dcloud.uniapp.extapi.*
import io.dcloud.uts.*
import io.dcloud.uts.Map
import io.dcloud.uts.Set
import io.dcloud.uts.UTSAndroid
import kotlin.properties.Delegates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import uts.alphaplayer.android.AlphaPlayerNative
open class AnimationOpitons (
    @JsonNotNull
    open var type: String,
    @JsonNotNull
    open var place: String,
) : UTSObject()
typealias AnimationPlay = (options: AnimationOpitons) -> Unit
typealias AnimationStop = () -> Unit
typealias GetMemoryInfo = () -> UTSArray<Number>
val animationPlay: AnimationPlay = fun(options: AnimationOpitons) {
    console.log("播放动效", options)
    AlphaPlayerNative.animationPlay(options.type, options.place)
}
val animationStop: AnimationStop = fun() {
    console.log("结束动效")
    AlphaPlayerNative.animationStop()
}
val getMemoryInfo: GetMemoryInfo = fun(): UTSArray<Number> {
    var kotlinArray = AlphaPlayerNative.getMemInfoKotlin()
    return UTSArray.fromNative(kotlinArray)
}
open class AnimationOpitonsJSONObject : UTSJSONObject() {
    open lateinit var type: String
    open lateinit var place: String
}
fun animationPlayByJs(options: AnimationOpitonsJSONObject): Unit {
    return animationPlay(AnimationOpitons(type = options.type, place = options.place))
}
fun animationStopByJs(): Unit {
    return animationStop()
}
fun getMemoryInfoByJs(): UTSArray<Number> {
    return getMemoryInfo()
}
