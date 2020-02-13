def capellaNightlyProduct(String inputPath, String outputDirName) {
  def outputPath = getFullCapellaProductPath(outputDirName)
  def sshAccount = getSSHAccount()
  
  sshagent (['projects-storage.eclipse.org-bot-ssh']) {
    sh "ssh ${sshAccount} mkdir -p ${outputPath}"
    sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
  }
}

def capellaNightlyUpdateSite(String inputPath, String outputDirName) {
  def outputPath = getFullCapellaUpdateSitePath(outputDirName)
  def sshAccount = getSSHAccount()
  
  sshagent (['projects-storage.eclipse.org-bot-ssh']) {
    sh "ssh ${sshAccount} mkdir -p ${outputPath}"
    sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
  }
}

def cleanCapellaNightlyArtefacts(String dirName) {
  def productPath = getFullCapellaProductPath(dirName)
  def updateSitePath = getFullCapellaUpdateSitePath(dirName)
  def sshAccount = getSSHAccount()
  
  sshagent (['projects-storage.eclipse.org-bot-ssh']) {
    sh "ssh ${sshAccount} rm -rf ${productPath}"
    sh "ssh ${sshAccount} rm -rf ${updateSitePath}"
  }
 
}

def addonNightlyProduct(Enum parentDir, String inputArtefactPath) {
  // TODO
}

def addonNightlyDropins(String parentDir, String inputArtefactPath) {
  // TODO
}

def addonNightlyUpdateSite(Enum parentDir, String inputArtefactPath) {
  // TODO
}

private def getSSHAccount() {
  return "genie.capella@projects-storage.eclipse.org"
}

private def getFullCapellaProductPath(String dirName) {
  return "/home/data/httpd/download.eclipse.org/capella/core/products/nightly/${dirName}/"
}

private def getFullCapellaUpdateSitePath(String dirName) {
  return "/home/data/httpd/download.eclipse.org/capella/core/updates/nightly/${dirName}/"
}