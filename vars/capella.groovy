def getDownloadURL(branch = "master", platform = "win", proxy = ""){
  
  def url = "https://download.eclipse.org/capella/core/products/nightly/${branch}/"
  switch(branch){
    case '5.2.0':
      url = "https://download.eclipse.org/capella/core/products/releases/5.2.0-R20211130-125709/"
      break
    case '1.4.2':
      url = "https://download.eclipse.org/capella/core/products/releases/1.4.2-R20201014-090868/"
      break
    case ~/\d\.\d\.\d/:
      url = "https://download.eclipse.org/capella/core/products/releases/${branch}/"
      break
    default:
      break
  }
  
  def script = proxy.isEmpty() ? "curl -ks ${url}" : "curl -ks -x ${proxy} ${url}"
  
  def html = sh(script: "${script}", returnStdout: true)

  def regex = ""

  switch(platform){
      case ~/mac/:
          regex = /(capella-[\d.]*-macosx-cocoa-x86_64.tar.gz)/
          break
      
      case ~/linux/:
          if (branch.startsWith('1.')) {
              regex = /(capella-[\d.]*-linux-gtk-x86_64.zip)/
          } else {
            regex = /(capella-[\d.]*-linux-gtk-x86_64.tar.gz)/
          }
          break
          
      case ~/win/:
           regex = /(capella-[\d.]*-win32-win32-x86_64.zip)/
           break
      
      default:
          regex = /(capella-[\d.]*-win32-win32-x86_64.zip)/
          break
  }
  
  def zipName = (html =~ regex)[0][1]

  return url + zipName
}

def getUpdateSiteURL(branch = "master") {
  switch(branch){
    case '5.2.0':
      return "https://download.eclipse.org/capella/core/updates/releases/5.2.0-R20211130-125709/"
    case '1.4.2':
      return "https://download.eclipse.org/capella/core/updates/releases/1.4.2-R20201014-090868/"
    case ~/\d\.\d\.\d/:
      return "https://download.eclipse.org/capella/core/updates/releases/${branch}/"
    default:
      return "https://download.eclipse.org/capella/core/updates/nightly/${branch}/"
  }
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