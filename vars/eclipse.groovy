def buildUpdateSiteFromProduct(equinoxJarPath, sourceProductPath, targetUpdateSitePath) {
  sh "java -jar ${equinoxJarPath} -source ${sourceProductPath} -metadataRepository ${targetUpdateSitePath} -artifactRepository ${targetUpdateSitePath} -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -compress -publishArtifacts"
}

def installFeature(String targetProductPath, String featureRepository, String featureName) {
	sh "${targetProductPath} -repository ${featureRepository} -installIU ${featureName} -application org.eclipse.equinox.p2.director -noSplash"  
}

def installFeature(String targetProductPath, String featureRepository, String featureName, String vmargs) {
	sh "${targetProductPath} -repository ${featureRepository} -installIU ${featureName} -application org.eclipse.equinox.p2.director -noSplash -vmargs ${vmargs}"
}

def addFeatureToTargetPlatform(targetPlatformPath, updateSitePath, featureName) {
  def updateSiteLocation = "location \"${updateSitePath}\" {\t${featureName}\n}"
  def tpFileContent = readFile targetPlatformPath
  
  writeFile file: targetPlatformPath, text: tpFileContent + "\n\n" + updateSiteLocation + "\n\n"
}