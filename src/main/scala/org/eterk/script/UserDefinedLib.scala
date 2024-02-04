package org.eterk.script

object UserDefinedLib {

  // 定义一个 PowerShell 脚本的字符串

  // 定义一个函数，接受五个参数，返回一个 string
  def createRegEntries(CLSID: String, Name: String, InfoTip: String, Info: String, TargetFolderPath: String, iconPath: String): String = {
    // 使用三个引号来表示多行字符串，使用 $ 符号来插入变量

    s"""Windows Registry Editor Version 5.00

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID]
@="$Name"
"DescriptionID"=dword:00000003
"InfoTip"="$InfoTip"
"Info"="$Info"
"System.IsPinnedToNameSpaceTree"=dword:00000001
"SortOrderIndex"=dword:00000100

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID\\DefaultIcon]
@="$iconPath"

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID\\InProcServer32]
@="C:\\\\Windows\\\\System32\\\\Shell32.dll"
"ThreadingModel"="Both"

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID\\Instance]
"CLSID"="{0E5AAE11-A475-4c5b-AB00-C66DE400274E}"

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID\\Instance\\InitPropertyBag]
"Attributes"=dword:00000011
"TargetKnownFolder"=-
"TargetFolderPath"="$TargetFolderPath"

[HKEY_CURRENT_USER\\SOFTWARE\\Classes\\CLSID\\$CLSID\\ShellFolder]
"Attributes"=dword:f080004d
"FolderValueFlags"=dword:00000029

[HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\MyComputer\\NameSpace\\$CLSID]

[HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Desktop\\NameSpace\\$CLSID]

[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\HideDesktopIcons\\NewStartPanel]
"$CLSID"=dword:00000001

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID]
@="$Name"
"DescriptionID"=dword:00000003
"InfoTip"="$InfoTip"
"Info"="${Info}"
"System.IsPinnedToNameSpaceTree"=dword:00000001
"SortOrderIndex"=dword:00000100

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID\\DefaultIcon]
@="$iconPath,0"

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID\\InProcServer32]
@="C:\\\\Windows\\\\System32\\\\Shell32.dll"
"ThreadingModel"="Both"

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID\\Instance]
"CLSID"="{0E5AAE11-A475-4c5b-AB00-C66DE400274E}"

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID\\Instance\\InitPropertyBag]
"Attributes"=dword:00000011
"TargetKnownFolder"=-
"TargetFolderPath"="$TargetFolderPath"

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Classes\\CLSID\\$CLSID\\ShellFolder]
"Attributes"=dword:f080004d
"FolderValueFlags"=dword:00000029

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Explorer\\MyComputer\\NameSpace\\$CLSID]

[HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Desktop\\NameSpace\\$CLSID]

"""

  }


  // 使用 PowerShell 命令执行脚本，并获取输出
  //  val output = s"powershell -Command $script".!!


  def main(args: Array[String]): Unit = {

    /**
     *  注意路径地址 S:// 盘符输出的时候必须是双杠，大写，否则不起作用，目前也图标设置没起作用
     *  2024/01/28
     */
    val content = createRegEntries("{9FC55268-A534-4CFF-B53A-4E8DFDFD021F}", "LIB", "library", "library", "S:\\\\lib", "S:\\\\util\\icon\\disk_s\\lib.ico")
    scala.io.Source.fromString(content)
      .getLines
      .foreach(line => scala.tools.nsc.io.File("s:/util/lib1.reg").appendAll(line + "\r\n"))
  }


}
