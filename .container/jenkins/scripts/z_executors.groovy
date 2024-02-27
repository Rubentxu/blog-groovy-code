import jenkins.model.*
Jenkins.instance.setNumExecutors(3) // Recommended to not run builds on the built-in node
hudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT = true

