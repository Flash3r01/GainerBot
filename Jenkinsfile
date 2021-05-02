#!/usr/bin/env groovy

def remote = [:]
remote.name = "vServer"
remote.host = "144.91.86.28"
remote.allowAnyHosts = true

node {
    //TODO Send mails on fail.
    stage('Build') {
        echo 'Building...'
        sh 'chmod u+x gradlew'
        sh './gradlew shadowJar'
        //TODO Failed on failure
    }
    stage('Archive') {
        echo 'Archiving...'
        archiveArtifacts artifacts: 'build/libs/*.jar', followSymlinks: false, onlyIfSuccessful: true
        //TODO Failed on failure
    }
    withCredentials([sshUserPrivateKey(credentialsId: 'liveServer', keyFileVariable: 'keyFile', passphraseVariable: 'passPhrase', usernameVariable: 'userName')]) {
        remote.user = userName
        remote.passphrase = passPhrase
        remote.identityFile = keyFile
        stage('Deploy') {
            echo 'Deploying...'
            sshCommand remote: remote, command: 'sudo /bin/systemctl stop gainerbot'
            sshCommand remote: remote, command: 'rm /home/deploy/gainerBot/gainerbot-all.jar'
            sshPut remote: remote, from: 'build/libs/gainerbot-all.jar', into: '/home/deploy/gainerBot/'
            sshCommand remote: remote, command: 'sudo /bin/systemctl start gainerbot'
            //TODO Unstable on failure
        }
    }
}