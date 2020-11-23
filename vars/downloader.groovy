def downloadWindowsJDK(jdkWinFolder) {
	def jdk = 'jdk-14.0.2'
  
  def jdkWinURL = 'https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_windows-x64_bin.zip'
  def jdkWinZip = 'jdkWin.zip'	
      
	sh "curl -k -o ${jdkWinZip} ${jdkWinURL}"
	sh "unzip -q ${jdkWinZip} -d ${jdkWinFolder}"
	sh "mv ${jdkWinFolder}/${jdk} ${jdkWinFolder}/jre"        
  
  println "Windows JDK dowloaded to ${jdkWinFolder}/jre"
  sh "ls ${jdkWinFolder}/jre"
}

def downloadLinuxJDK(jdkLinuxFolder) {
  def jdk = 'jdk-14.0.2'
  
  def jdkLinuxURL = 'https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_linux-x64_bin.tar.gz'
  def jdkLinuxTar = 'jdkLinux.tar.gz'
      
	sh "curl -k -o ${jdkLinuxTar} ${jdkLinuxURL}"
	sh "mkdir ${jdkLinuxFolder}"
  sh "tar xzf ${jdkLinuxTar} -C ${jdkLinuxFolder}"
	sh "mv ${jdkLinuxFolder}/${jdk} ${jdkLinuxFolder}/jre"
  
  println "Linux JDK downloaded to ${jdkLinuxFolder}/jre"    
	sh "ls ${jdkLinuxFolder}/jre"
}

def downloadMacJDK(jdkMacFolder) {
  def jdk = 'jdk-14.0.2'
  
  def jdkMacURL = 'https://download.java.net/java/GA/jdk14.0.2/205943a0976c4ed48cb16f1043c5c647/12/GPL/openjdk-14.0.2_osx-x64_bin.tar.gz'
  def jdkMacTar = 'jdkMac.tar.gz'
        
	sh "curl -k -o ${jdkMacTar} ${jdkMacURL}"
	sh "mkdir ${jdkMacFolder}"
	sh "tar xzf ${jdkMacTar} -C ${jdkMacFolder}"
	sh "mv ${jdkMacFolder}/${jdk}.jdk ${jdkMacFolder}/jre"

  println "Mac JDK downloaded to ${jdkMacFolder}/jre"
	sh "ls ${jdkMacFolder}/jre"
}