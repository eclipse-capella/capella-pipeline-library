def call(body) {

    // Jenkinsfile:
    // capellaAddon {
    //   url = "git repo url"  // where the git clone comes from
    //   name = "addon-name"   // name will be used for:
    //                              * download folder name: download.eclipse.org/capella/addons/<name>/
    //                              * dropins and site zip name
    //
    //   (optional) targetPlatform = <path-to-tp-pom>, relative to the project root directory
    //   (optional) versionFolder = <folder-name>. By default, artifacts go to <name>/updates/nightly/<branch>
    //                               but we want to avoid using 'master' as a folder name, so in your master branch
    //                               you can e.g. set this to 'v1.5.x' to copy artifacts to
    //                               <name>/updates/nightly/v1.5.x/.
    // }.

    def pipelineParams= [targetPlatform: 'releng/capella-releng-parent/tp/capella-default-addon-target/pom.xml',
			 versionFolder: env.BRANCH_NAME
    ]


    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {

	options { skipDefaultCheckout() }

	agent {
	    label "migration"
	}

	tools {
	    maven "apache-maven-latest"
	    jdk "openjdk-jdk14-latest"
	}

	stages {

	    stage ('Checkout addon code') {
		steps {

		    checkout([$class: 'GitSCM',
			      branches: [[name: "*/${env.BRANCH_NAME}"]],
			      doGenerateSubmoduleConfigurations: false,
			      extensions: [[$class: 'RelativeTargetDirectory',
					    relativeTargetDir: pipelineParams.name],
					   [
				$class: 'SubmoduleOption',
				disableSubmodules: false,
				parentCredentials: true,
				recursiveSubmodules: true,
				reference: '',
				trackingSubmodules: true
			    ]
			],
			      submoduleCfg: [], userRemoteConfigs: [[url: pipelineParams.url]]])
		}
	    }


	    stage ('Generate Targetplatform'){
		steps {
		    sh "mvn --batch-mode --activate-profiles generate-target -f \"${pipelineParams.name}/${pipelineParams.targetPlatform}\" clean install"
		}
	    }

	    stage ('Build') {
		steps {
		    wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
			sh "mvn --batch-mode -DpackagedSiteName=\"${pipelineParams.name}\" -f \"${pipelineParams.name}/pom.xml\" clean verify"
		    }
		}
	    }


	    stage ('Publish Nightly'){
		when {
		    not {
			changeRequest()
		    }
		}

		steps {
		    dir (pipelineParams.name) {
			sshagent (['projects-storage.eclipse.org-bot-ssh']) {
			    sh "releng/capella-releng-parent/scripts/deployAddonNightly.sh -n \"${pipelineParams.name}\" -b \"${pipelineParams.versionFolder}\""
			}
		    }
		}
	    }
	}
    }
}
