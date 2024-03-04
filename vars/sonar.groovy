

def runSonar(String sonarProjectKey, String githubRepository, String sonarLoginToken, javaVersion = "17"){
	withCredentials([string(credentialsId: "${sonarLoginToken}", variable: 'SONARCLOUD_TOKEN')]) {
		withEnv(['MAVEN_OPTS=-Xmx4g']) {
			def jacocoParameters = "-Dsonar.java.coveragePlugin=jacoco -Dsonar.core.codeCoveragePlugin=jacoco "
			def sonarCommon = "sonar:sonar -Dsonar.projectKey=${sonarProjectKey} -Dsonar.organization=eclipse-capella -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONARCLOUD_TOKEN} -Dsonar.skipDesign=true -Dsonar.dynamic=reuseReports -Dsonar.java.source=${javaVersion} -Dsonar.scanner.force-deprecated-java-version=true "
			def sonarBranchAnalysis = "-Dsonar.branch.name=${BRANCH_NAME}"
			def sonarPullRequestAnalysis = (github.isPullRequest() ? "-Dsonar.pullrequest.provider=GitHub -Dsonar.pullrequest.github.repository=${githubRepository} -Dsonar.pullrequest.key=${CHANGE_ID} -Dsonar.pullrequest.branch=${CHANGE_BRANCH}" : "")
			def sonar = sonarCommon + jacocoParameters + (github.isPullRequest() ? sonarPullRequestAnalysis : sonarBranchAnalysis)
	      	sh "mvn ${sonar}"
		}
	}
}
