def getDownloadURL(branch = "master", platform = "win", proxy = ""){
  
  def url = "https://download.eclipse.org/capella/core/products/nightly/${branch}/"
  def script = proxy.isEmpty() ? "curl -ks ${url}" : "curl -ks -x ${proxy} ${url}"
  
  def html = sh(script: "${script}", returnStdout: true)

  def regex = ""

  switch(platform){
      case ~/mac/:
          regex = /(capella-[\d.]*-macosx-cocoa-x86.*zip)'/
          break
      
      case ~/linux/:
          regex = /(capella-[\d.]*-linux-gtk-x86.*zip)'/
          break
          
      case ~/win/:
           regex = /(capella-[\d.]*-win32-win32-x86.*zip)'/
           break
      
      default:
          regex = /(capella-[\d.]*-win32-win32-x86.*zip)'/
          break
  }
  
  def zipName = (html =~ regex)[0][1]

  return url + zipName
}

def getUpdateSiteURL(branch = "master") {
	return "https://download.eclipse.org/capella/core/updates/nightly/${branch}/"
}

def getTestUpdateSiteURL(branch = "master") {
	return getUpdateSiteURL(branch) + "org.polarsys.capella.test.site/"
}

def getRCPUpdateSiteURL(branch = "master") {
	return getUpdateSiteURL(branch) + "org.polarsys.capella.rcp.site/"
}

def getEGFUpdateSiteURL(branch = "master") {
	return getUpdateSiteURL(branch) + "org.polarsys.capella.egf.site/"
}