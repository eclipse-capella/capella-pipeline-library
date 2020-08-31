def isPullRequest() {
  return "${BRANCH_NAME}".contains('PR-')
}

def getPullRequestId() {
  return "${BRANCH_NAME}".replace('PR-', '')
}

def getProjectName() {
	return (GIT_URL =~ /[^\/]+(?=\.git$)/)[0]
}

def pullRequestComment(message) {
  def pullRequestId = getPullRequestId()
	def projectName = getProjectName()
  def apiURL = "https://api.github.com/repos/eclipse/${projectName}/issues/${pullRequestId}/comments"
  
  withCredentials([string(credentialsId: '0dea5761-867c-44db-92fa-9304c81a8653', variable: 'password')]) {
  	sh """curl ${apiURL} -d '{"body":"${message}"}' -u 'eclipse-capella-bot:${password}' -X POST -H 'Content-Type: application/json'"""
  }
}

def buildStartedComment() {
  def buildId = "${BUILD_KEY}-${BRANCH_NAME}-${BUILD_ID}".replaceAll('/','-')
  def message = ":rocket: Build [${buildId}](${BUILD_URL}) started!"
  
  pullRequestComment(message)
}

def buildSuccessfullComment() {
  def buildDir = "${BUILD_KEY}-${BRANCH_NAME}-${BUILD_ID}".replaceAll('/','-')
  def message = ":thumbsup: Build [${buildDir}](${BUILD_URL}) is successfull! The product is available [here](http://download.eclipse.org/capella/core/products/nightly/${buildDir})."
  
  pullRequestComment(message)
}

def buildFailedComment() {
  def buildId = "${BUILD_KEY}-${BRANCH_NAME}-${BUILD_ID}".replaceAll('/','-')
  def message = ":disappointed: Build [${buildId}](${BUILD_URL}) failed!"
  
  pullRequestComment(message)
}

def buildUnstableComment() {
  def buildDir = "${BUILD_KEY}-${BRANCH_NAME}-${BUILD_ID}".replaceAll('/','-')
  def message = ":worried: Build [${buildDir}](${BUILD_URL}) is unstable! The product is available [here](http://download.eclipse.org/capella/core/products/nightly/${buildDir})."
  
  pullRequestComment(message)
}

def buildAbortedComment() {
  def buildId = "${BUILD_KEY}-${BRANCH_NAME}-${BUILD_ID}".replaceAll('/','-')
  def message = ":neutral_face: Build [${buildId}](${BUILD_URL}) has been aborted!"
  
  pullRequestComment(message)
}

def private isDraftCommit() {
  def commitMsg = sh (script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
  
  return commitMsg.contains("DRAFT")
}

def private getExistingLabels() {
  def pullRequestId = getPullRequestId()
  def projectName = getProjectName()
  def apiURL = "https://api.github.com/repos/eclipse/${projectName}/issues/${pullRequestId}/labels"
	def labels = ""
  
  withCredentials([string(credentialsId: '0dea5761-867c-44db-92fa-9304c81a8653', variable: 'password')]) {
    def script = """curl ${apiURL} -u "eclipse-capella-bot:${password}" -H "Content-Type: application/json" """
    
    labels = sh(script: "${script}", returnStdout: true)
  }
  
  return labels
}

def private getCustomLabels() {
  return ['build-aborted', 'build-unstable', 'build-failed', 'build-successfull', 'build-started']
}

def private removeLabel(label) {
  def pullRequestId = getPullRequestId()
  def projectName = getProjectName()
  def apiURL = "https://api.github.com/repos/eclipse/${projectName}/issues/${pullRequestId}/labels/${label}"

  withCredentials([string(credentialsId: '0dea5761-867c-44db-92fa-9304c81a8653', variable: 'password')]) {
		sh """curl ${apiURL} -X DELETE -u "eclipse-capella-bot:${password}" -H "Content-Type: application/json" """
  }
}

def removeCustomPullRequestLabels() {
  def pullRequestId = getPullRequestId()
  def projectName = getProjectName()
  def existingLabels = getExistingLabels()
  def customLabels = getCustomLabels()
  
  customLabels.each {
    if(existingLabels.contains("${it}")) {
      removeLabel("${it}")
    }
  }
}

def private addPullRequestLabel(label) {
  def pullRequestId = getPullRequestId()
  def projectName = getProjectName()
  def apiURL = "https://api.github.com/repos/eclipse/${projectName}/issues/${pullRequestId}/labels"
  
  withCredentials([string(credentialsId: '0dea5761-867c-44db-92fa-9304c81a8653', variable: 'password')]) {
  	sh """curl ${apiURL} --data '{"labels":["${label}"]}' -u "eclipse-capella-bot:${password}" --request POST -H "Content-Type: application/json" """
  }
}

def buildStartedLabel() {
  addPullRequestLabel('build-started')
}

def buildSuccessfullLabel() {
  addPullRequestLabel('build-successfull')
}

def buildFailedLabel() {
	addPullRequestLabel('build-failed')
}

def buildUnstableLabel() {
  addPullRequestLabel('build-unstable')
}

def buildAbortedLabel() {
  addPullRequestLabel('build-aborted')
}

def removeBuildStartedLabel() {
  removeLabel('build-started')
}
