ARG tag='2.440.1-jdk17'
FROM jenkins/jenkins:$tag
ENV ASDF_DIR="/var/jenkins_home/.asdf"
ENV ASDF_DATA_DIR="/var/jenkins_home/.asdf-data"
ENV PATH="${PATH}:${ASDF_DATA_DIR}/shims:${ASDF_DATA_DIR}/bin:${ASDF_DIR}/bin"
ENV SHELL="/bin/bash"

USER root
RUN apt-get update && apt-get install -y apt-transport-https lsb-release \
    ca-certificates curl gnupg2
RUN echo 'deb https://deb.debian.org/debian buster-backports main' >> /etc/apt/sources.list \
    && echo 'deb https://download.opensuse.org/repositories/devel:/kubic:/libcontainers:/stable/Debian_10/ /' > /etc/apt/sources.list.d/devel:kubic:libcontainers:stable.list \
    && curl -L https://download.opensuse.org/repositories/devel:/kubic:/libcontainers:/stable/Debian_10/Release.key | apt-key add -
RUN apt-get update && apt-get -y -t buster-backports install libseccomp2 \
    && apt-get install -y podman runc crun unzip git tar vim software-properties-common \
    uidmap fuse3 fuse-overlayfs \
    python3 python3-pip


RUN mkdir -m777 -p /opt/repositories && mkdir -m777 -p /opt/cache && mkdir -p /etc/ssl/certs/java  \
    && mkdir -m777 -p ${ASDF_DATA_DIR} && mkdir -m777 -p /opt/libs && mkdir -m777 -p /var/jenkins_home/plugins \
    && mkdir -p /var/lib/shared/overlay-images /var/lib/shared/overlay-layers; touch /var/lib/shared/overlay-images/images.lock; touch /var/lib/shared/overlay-layers/layers.lock \
    && chown jenkins:jenkins -R /opt /var/jenkins_home/plugins \
    && echo  '[ ! -f /var/jenkins_home/.env ] || export $(grep -v '^#' /var/jenkins_home/.env | xargs)' >> /root/.bashrc \
    && echo  '[ ! -f /var/jenkins_home/.env ] || export $(grep -v '^#' /var/jenkins_home/.env | xargs)' >> /var/jenkins_home/.bashrc \
    && echo ". /var/jenkins_home/.asdf/asdf.sh" >> /var/jenkins_home/.bashrc \
    && echo "export PATH=\"\$PATH:${ASDF_DIR}/bin/\"" >> /var/jenkins_home/.bashrc \
    && git config --global user.email "cicd@rubentxu.dev" \
    && git config --global user.name "Rubentxu"

COPY containers.conf /etc/containers/containers.conf
COPY storage.conf /etc/containers/storage.conf
#COPY certificates/ca-bundle.crt /usr/local/share/ca-certificates/ca-bundle.crt
COPY --chown=jenkins:jenkins .m2/* /var/jenkins_home/.m2/
#COPY --chown=jenkins:jenkins hudson.model.UpdateCenter.xml /var/jenkins_home/hudson.model.UpdateCenter.xml


#RUN update-ca-certificates  \
#    && ln -s /usr/local/share/ca-certificates/ca-bundle.crt /etc/ssl/ca-bundle.pem \
#    && keytool -importcert -file /usr/local/share/ca-certificates/ca-bundle.crt -noprompt \
#        -alias rubentxu -keystore /etc/ssl/certs/java/cacerts -storepass changeit

USER jenkins
WORKDIR /var/jenkins_home
RUN jenkins-plugin-cli --plugins workflow-cps pipeline-groovy-lib ivy:2.2 git git-client scm-api -d /var/jenkins_home/plugins
