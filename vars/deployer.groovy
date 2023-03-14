def capellaNightlyProduct(String inputPath, String outputDirName) {
	if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	else {
		def outputPath = getFullCapellaProductPath(outputDirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} mkdir -p ${outputPath}"
			sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
		}
	}
}

def capellaNightlyUpdateSite(String inputPath, String outputDirName) {
	if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	else {
		def outputPath = getFullCapellaUpdateSitePath(outputDirName)
		def sshAccount = getSSHAccount()
	  
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} mkdir -p ${outputPath}"
			sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
		}
	}
}

def cleanCapellaNightlyArtefacts(String dirName) {
	if (isInvalid(dirName)) {
		log.error("Deployment Error: ${dirName} is not recognised")
	}
	else {
		
		def productPath = getFullCapellaProductPath(dirName)
		def updateSitePath = getFullCapellaUpdateSitePath(dirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} rm -rf ${productPath}"
			sh "ssh ${sshAccount} rm -rf ${updateSitePath}"
		}
	}
 
}

def addonNightlyDropins(String inputPath, String outputDirName) {
	def addonDirName = getAddonDirName()
	
	if(isInvalid(addonDirName)) {
		log.error("Deployment Error: ${GIT_URL} repository is not recognised")
		
	} else if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	else {		
		def outputPath = getFullAddonDropinsPath(addonDirName, outputDirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} mkdir -p ${outputPath}"
			sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
		}
	}
}

def addonNightlyUpdateSite(String inputPath, String outputDirName) {
	def addonDirName = getAddonDirName()
	
	if(isInvalid(addonDirName)) {
		log.error("Deployment Error: ${GIT_URL} repository is not recognised")
		
	} else if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	else {		
		def outputPath = getFullAddonDropinsUpdateSitePath(addonDirName, outputDirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} mkdir -p ${outputPath}"
			sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
		}
	}
}

def addonNightlyProduct(String inputPath, String outputDirName) {
	def addonDirName = getAddonDirName()
	
	if(isInvalid(addonDirName)) {
		log.error("Deployment Error: ${GIT_URL} repository is not recognised")
		
	} else if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	
	else {		
		def outputPath = getFullAddonProductPath(addonDirName, outputDirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} mkdir -p ${outputPath}"
			sh "scp -rp ${inputPath} ${sshAccount}:${outputPath}"
		}
	}
}

def cleanAddonNightlyArtefacts(String outputDirName) {
	def addonDirName = getAddonDirName()
	
	if(isInvalid(addonDirName)) {
		log.error("Deployment Error: ${GIT_URL} repository is not recognised")
		
	} else if (isInvalid(outputDirName)) {
		log.error("Deployment Error: ${outputDirName} is not recognised")
	}
	else {		
		def dropinPath = getFullAddonDropinsPath(addonDirName, outputDirName)
		def updateSitePath = getFullAddonDropinsUpdateSitePath(addonDirName, outputDirName)
		def sshAccount = getSSHAccount()
		
		sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			sh "ssh ${sshAccount} rm -rf ${dropinPath}"
			sh "ssh ${sshAccount} rm -rf ${updateSitePath}"
		}
	}
}


/**
 * Extracts the addon directory name from the git branch.
 */
private def getAddonDirName() {
	switch(GIT_URL){
		
		case ~/.*capella-sss-transition.*/:
				return 'subsystemtransition'
		
		case ~/.*capella-cybersecurity.*/:
				return 'cybersecurity'
		
		case ~/.*capella-requirements-vp.*/:
				return 'requirements'
		
		case ~/.*capella-filtering.*/:
				return 'filtering'
				
		case ~/.*capella-textual-editor.*/:
			return 'textualeditor'
		
		case ~/.*capella-deferred-merge.*/:
			return 'deferredmerge'
		
		case ~/.*capella-vpms.*/:
			return 'vpms'
			 
		case ~/.*capella-xhtml-docgen.*/:
			return 'xhtmldocgen'
		
		default:
				return ''
	}
}

private def isInvalid(path) {
  return path.isEmpty() || path.contains('..')
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

private def getFullAddonDropinsPath(String addonDirName, String dirName) {
	return "/home/data/httpd/download.eclipse.org/capella/addons/${addonDirName}/dropins/nightly/${dirName}/"
}

private def getFullAddonDropinsUpdateSitePath(String rootDirName, String dirName) {
	return "/home/data/httpd/download.eclipse.org/capella/addons/${addonDirName}/updates/nightly/${dirName}/"
}

private def getFullAddonProductPath(String rootDirName, String dirName) {
	return "/home/data/httpd/download.eclipse.org/capella/addons/${addonDirName}/products/nightly/${dirName}/"
}
