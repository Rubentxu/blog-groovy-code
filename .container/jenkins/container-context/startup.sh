#!/bin/bash
jenkins-plugin-cli --plugins workflow-cps pipeline-groovy-lib ivy:2.2 git git-client scm-api -d /opt/jenkins/plugins
/usr/local/bin/jenkins.sh
