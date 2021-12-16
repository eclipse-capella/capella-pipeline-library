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

/**
 * Download Temurin JDK 17 version in <code>jdkFolder</code> folder for operating system <code>os</code>
 * @param jdkFolder
 * @param os The operating system. Expected one of 'win', 'linux', 'mac'
 * @return Nothing but extracts the downloaded JDK to <code>${jdkFolder}/jre</code>.
 */
def downloadTemurinJDK17(jdkFolder, os) {
    def jdk = 'jdk-17.0.1+12'
    
    def jdkLinuxURL = 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_linux_hotspot_17.0.1_12.tar.gz'
    def jdkWinURL = 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_windows_hotspot_17.0.1_12.zip'
    def jdkMacURL = 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_mac_hotspot_17.0.1_12.tar.gz'
    
    def jdkArchive = 'jdk'
    def jdkURL = ''
    
    switch (os) {
        case 'win':
            jdkURL = jdkWinURL
            jdkArchive += os + '.zip'
            break;
        case 'mac':
            jdkURL = jdkMacURL
            jdkArchive += os + '.tar.gz'
            break;
        case 'linux':
            jdkURL = jdkLinuxURL
            jdkArchive += os + '.tar.gz'
            break;
        default:
            return;
    }
    
    sh "curl -k -o ${jdkArchive} ${jdkURL}"
    sh "mkdir ${jdkFolder}"
    
    def osPrint = ''
    switch (os) {
        case 'win':
            sh "unzip -q ${jdkArchive} -d ${jdkFolder}"
            osPrint = 'Win'
            break;
        case 'mac':
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            sh "mv ${jdkFolder}/${jdk}.jdk ${jdkFolder}/jre"
            osPrint = 'Mac'
            break;
        case 'linux':
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            sh "mv ${jdkFolder}/${jdk} ${jdkFolder}/jre"
            osPrint = 'Linux'
            break;
        default:
            break;
    }

    println "${osPrint} JDK downloaded to ${jdkFolder}/jre"
    sh "ls ${jdkFolder}/jre"
    
}
