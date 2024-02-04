package org.eterk.util

import com.osinka.i18n.Lang

object LanguageSetting {

  private var lang_ = Lang("en")

  implicit var langeImp: Lang = lang

  def setLang(lang: Lang): Unit = {
    this.langeImp = lang
    this.lang_ = lang
  }

  def lang: Lang = lang_


}
