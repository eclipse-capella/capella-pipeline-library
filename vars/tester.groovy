private def getRunnerCmd(String capellaProductPath) {
  return "${capellaProductPath} " +
      "-port 8081 " +
      "-application org.polarsys.capella.test.run.application " +
      "-data ${WORKSPACE}/runner >> ${WORKSPACE}/runner.log"
}

private def getJunitCmdTemplate(String capellaProductPath, String applicationParam) {
  
  // extract the capella path, without the executable name
  def capellaPath = capellaProductPath.substring(0, capellaProductPath.lastIndexOf("/"))
  
  return "sleep 10 && " +
    "java " +
      "-Xms1024m -Xmx3500m -XX:+CMSClassUnloadingEnabled -ea " +
      "-Declipse.p2.data.area=@config.dir/../p2 " +
      "-Dfile.encoding=Cp1252 " +
      "-classpath ${capellaPath}/plugins/org.eclipse.equinox.launcher_*.jar org.eclipse.equinox.launcher.Main " +
      "-os linux " +
      "-ws gtk " +
      "-arch x86_64 " +
      "-version 3 " +
      "-port 8081 " +
      "-testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader " +
      "-loaderpluginname org.eclipse.jdt.junit4.runtime " +
      "-application ${applicationParam} " +
      "-product org.polarsys.capella.rcp.product " +
      "-testApplication org.polarsys.capella.core.platform.sirius.ui.perspective.id " +
      "-configuration file:${capellaPath}/configuration " +
      "-buildKey ${BUILD_KEY}"
}


private def getUIJunitCmd(String capellaProductPath) {
	return getJunitCmdTemplate(capellaProductPath, 'org.eclipse.pde.junit.runtime.uitestapplication')
}

private def getNONUIJunitCmd(String capellaProductPath) {
  return getJunitCmdTemplate(capellaProductPath, 'org.eclipse.pde.junit.runtime.nonuithreadtestapplication')
}

def runUITests(String capellaProductPath, String suiteTitle, String testPluginName, List<String> testClassNames) {
    def runnerCmd = getRunnerCmd(capellaProductPath)
    def junitCmd = getUIJunitCmd(capellaProductPath)
    def testClassNamesParam = testClassNames.join(' ')
    
    sh "${runnerCmd} -title ${suiteTitle} & ${junitCmd} -data ${WORKSPACE}/${suiteTitle} -testpluginname ${testPluginName} -classNames ${testClassNamesParam}"
}

def runNONUITests(String capellaProductPath, String suiteTitle, String testPluginName, List<String> testClassNames) {
    def runnerCmd = getRunnerCmd(capellaProductPath)
    def junitCmd = getNONUIJunitCmd(capellaProductPath)
    def testClassNamesParam = testClassNames.join(' ')
    
    sh "${runnerCmd} -title ${suiteTitle} & ${junitCmd} -data ${WORKSPACE}/${suiteTitle} -testpluginname ${testPluginName} -classNames ${testClassNamesParam}"
}