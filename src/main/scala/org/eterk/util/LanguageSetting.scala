package org.eterk.util

import com.osinka.i18n.Lang

object LanguageSetting {

  private var lang_ = Lang("zh")


  def setLang(lang: Lang): Unit = {
    this.lang_ = lang
  }

  def lang: Lang = lang_


}
