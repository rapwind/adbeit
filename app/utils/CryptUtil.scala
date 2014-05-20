package utils

import java.security.MessageDigest
import java.util.Calendar
import java.text.SimpleDateFormat

import play.api._
import play.api.mvc._

object CryptUtil {
  /**
  * ハッシュ（SHA-256)を取得します。
  * @param s 取得対象の文字列
  * @return 生成したハッシュ値
  */
  def getSha(s: String):String = {
    val md = MessageDigest.getInstance("SHA-256")
    md.digest(s.getBytes).map(_ & 0xFF).map(_.toHexString).mkString
  }


  /*
   *get uuid. random id.
   */
  def uuid = java.util.UUID.randomUUID.toString

  /**
  * 指定回数ハッシュを行う
  * @param s ハッシュ対象文字
  * @param i ハッシュ実行回数
  */
  def getSha256(s: String, i:Int):String = {
    var ret:String = s
    for( x <- Range(1,i)) {
      ret = getSha(ret)
    }
    ret
  }

  /**
   * 日付、時刻の取得
   */
  def getCurrentDate: String = {
    val today = Calendar.getInstance().getTime()
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val currentDateAsString = dateFormat.format(today)
    currentDateAsString
  }

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str)
}