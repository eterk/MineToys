package org.eterk


object AppFactory {

  import org.eterk.app._

  def availableApp: Seq[App] =
    Seq(ExportWav,
      DominantColor,
      HelloApp,
      GradientIcon,
      SingleColorIcon,
      ImageConvert,
      DesktopInI,
      WavToText)


}
