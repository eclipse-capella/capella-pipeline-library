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

def fetchTemurinJDK17(name, os) {
    fetchTemurinJDK(name, 17,os)
}

/**
 * Prefetch the latest patch of the specified JDK version. To be used by the prefetch job to save a JDK zip file in a job's area.
 * 
 * @param version The major version for which we need the latest available patch. Use https://api.adoptium.net/v3/info/available_releases to determine allowed versions.
 * @param name The name of this JDK version, which will be used to name the pre-fetched zip file.
 * @param os The OS for which we're fetching a JDK.
 */
def fetchTemurinJDK(version, name, os) {
    def path = ''
    def extension = ''
    
    switch (os) {
        case 'win':
            path = 'https://api.adoptium.net/v3/binary/latest/' + version + '/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk'
            extension = '.zip'
            break;
        case 'mac':
            path = 'https://api.adoptium.net/v3/binary/latest/' + version + '/ga/mac/x64/jdk/hotspot/normal/eclipse?project=jdk'
            extension = '.tar.gz'
            break;
        case 'mac-aarch64':
            path = 'https://api.adoptium.net/v3/binary/latest/' + version + '/ga/mac/aarch64/jdk/hotspot/normal/eclipse?project=jdk'
            extension = '.tar.gz'
            break;	    
        case 'linux':
            path = 'https://api.adoptium.net/v3/binary/latest/' + version + '/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk'
            extension = '.tar.gz'
            break;
        case 'linux-aarch64':
            path = 'https://api.adoptium.net/v3/binary/latest/' + version + '/ga/linux/aarch64/jdk/hotspot/normal/eclipse?project=jdk'
            extension = '.tar.gz'
            break; 
        default:
            return;
    }
    
    def jdkURL = 'https://api.adoptium.net/v3/binary/latest/' + version + path
    def jdkArchive = name + '-' + os + extension
    
    sh "curl -L -k -o ${jdkArchive} ${jdkURL}"
    sh "ls -la ${jdkArchive}"
    deployer.uploadCapellaJDK(jdkArchive)
	sh "rm ${jdkArchive}"
}

/**
 * Download Temurin JDK 17 version in <code>jdkFolder</code> folder for operating system <code>os</code>
 * @param jdkFolder
 * @param os The operating system. Expected one of 'win', 'linux', 'mac'
 * @return Nothing but extracts the downloaded JDK to <code>${jdkFolder}/jre</code>.
 */
def downloadTemurinJDK17(jdkFolder, os) {
    retrieveTemurinJDK(jdkFolder, 17, os)
}

/**
 * Retrieves the specified JDK from the pre-fetch job. This will unzip the specified JDK inside the "${jdkFolder}/jre" folder of the calling job.
 *
 * @param jdkFolder The folder in which to unzip the JDK.
 * @param version The major version for which we need the latest pre-fetched zip.
 * @param os The OS for which we need the pre-fetched zip.
 */
def retrieveTemurinJDK(jdkFolder, version, os) {
    def extension = ''
    switch (os) {
        case 'win':
            extension = '.zip'
            break;
        case 'mac':
            extension = '.tar.gz'
            break;
        case 'mac-aarch64':
            extension = '.tar.gz'
            break;
        case 'linux':
            extension = '.tar.gz'
            break;
        case 'linux-aarch64':
            extension = '.tar.gz'
            break;
        default:
            return;
    }
    
    def jdkURL = getCapellaJDKPath()
    def jdkArchive = 'jdk' + version + '-' + os + extension
    
    sh "curl -L -k -o ${jdkArchive} ${jdkURL}${jdkArchive}"
    sh "ls -la ${jdkArchive}"
    
    switch (os) {
        case 'win':
            sh "unzip -q ${jdkArchive} -d ${jdkFolder}"
            break;
        case 'mac':
            sh "mkdir ${jdkFolder}"
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            break;
        case 'mac-aarch64':
            sh "mkdir ${jdkFolder}"
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            break;
        case 'linux':
            sh "mkdir ${jdkFolder}"
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            break;
        case 'linux-aarch64':
            sh "mkdir ${jdkFolder}"
            sh "tar xzf ${jdkArchive} -C ${jdkFolder}"
            break;
        default:
            break;
    }
    
    sh "mv ${jdkFolder}/jdk* ${jdkFolder}/jre"
    println "${os} JDK downloaded to ${jdkFolder}/jre"
    sh "ls ${jdkFolder}/jre"
}

def private getCapellaJDKPath() {
  return "https://download.eclipse.org/capella/releng/.jdk/"
}
