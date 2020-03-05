def isPullRequest() {
  return "${BRANCH_NAME}".contains('PR-');
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
