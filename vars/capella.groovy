def getDownloadURL(branch = "master", platform = "win", proxy = ""){
  
  def url = "https://download.eclipse.org/capella/core/products/nightly/${branch}/"
  def script = proxy.isEmpty() ? "curl -ks ${url}" : "curl -ks -x ${proxy} ${url}"
  
  def html = sh(script: "${script}", returnStdout: true)

  def regex = ""

  switch(platform){
      case ~/mac/:
          regex = /(capella-.*macosx-cocoa-x86.*zip)'/
          break
      
      case ~/linux/:
          regex = /(capella-.*linux-gtk-x86.*zip)'/
          break
          
      case ~/win/:
           regex = /(capella-.*win32-win32-x86.*zip)'/
           break
      
      default:
          regex = /(capella-.*win32-win32-x86.*zip)'/
          break
  }
  
  def zipName = (html =~ regex)[0][1]

  return url + zipName
}